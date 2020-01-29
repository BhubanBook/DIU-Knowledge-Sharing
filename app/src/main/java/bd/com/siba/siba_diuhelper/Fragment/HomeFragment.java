package bd.com.siba.siba_diuhelper.Fragment;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import bd.com.siba.siba_diuhelper.Model.Address;
import bd.com.siba.siba_diuhelper.OtherClass.CoursesInfo;
import bd.com.siba.siba_diuhelper.OtherClass.CustomProgressDialog;
import bd.com.siba.siba_diuhelper.OtherClass.CustomToast;
import bd.com.siba.siba_diuhelper.R;
import bd.com.siba.siba_diuhelper.UI.SendRequestBottomSheet;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class HomeFragment extends Fragment implements
        OnMapReadyCallback {


    private Location currentLocation;
    private Boolean locationPermissionsGranted = false;

    private GoogleMap map;
    private FragmentManager fragmentManager;
    private GoogleMapOptions googleMapOptions;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String userId;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 13f;

    private FloatingActionButton myLocationFABtn;
    private AutoCompleteTextView searchByCourseNameACTV;
    private ImageView crossBtnIV;

    private CoursesInfo coursesInfo;
    private ProgressDialog progressDialog;

    private boolean isSearchByCourseName = false;
    private String searchedCourseName = "";
    private boolean isMapLoaded = false;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initialize(view);

        getLocationPermission();


        progressDialog = CustomProgressDialog.createProgressDialog(getActivity());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.isIndeterminate();
        progressDialog.show();


        ArrayAdapter<String> courseNameAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, coursesInfo.courseNameList);
        searchByCourseNameACTV.setThreshold(1);
        searchByCourseNameACTV.setAdapter(courseNameAdapter);
        searchByCourseNameACTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isSearchByCourseName = true;
                hideSoftKeyboard();
                searchedCourseName = searchByCourseNameACTV.getText().toString();
                getFilteredUserFromDB(searchedCourseName);
            }
        });


        crossBtnIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchByCourseNameACTV.getText().clear();
            }
        });

        searchByCourseNameACTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchByCourseNameACTV.getText().toString().isEmpty()) {
                    crossBtnIV.setVisibility(View.GONE);
                    isSearchByCourseName = false;
                    searchedCourseName = "";
                    getUserFromDB();
                } else {
                    crossBtnIV.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        if (isSearchByCourseName == false && searchedCourseName.equals("")) {
            getUserFromDB();
        } else if (isSearchByCourseName == true && !searchedCourseName.equals("")) {
            getFilteredUserFromDB(searchedCourseName);
        }

    }

    private void initialize(View view) {
        coursesInfo = new CoursesInfo();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        myLocationFABtn = view.findViewById(R.id.myLocationFAB);
        searchByCourseNameACTV = view.findViewById(R.id.searchByCourseNameACTV);
        crossBtnIV = view.findViewById(R.id.crossBtnIV);
        fragmentManager = getActivity().getSupportFragmentManager();

        myLocationFABtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });

    }


    private void initializeMap() {

        googleMapOptions = new GoogleMapOptions();
        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance(googleMapOptions);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().replace(R.id.mapViewFrameLayout, supportMapFragment);
        fragmentTransaction.commit();
        supportMapFragment.getMapAsync(this);
    }

    private void getFilteredUserFromDB(final String courseName) {

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        DatabaseReference userDB = databaseReference.child("UserList");
        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (isMapLoaded == true) {
                        map.clear();
                    }
                    int counter = 0;
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        if (data.child("AddressInfo").child("PresentAddress").exists()
                                && data.child("CourseList").exists()
                                && data.child("GeneralInfo").exists()
                                && data.child("UniversityInfo").exists()
                                && !data.getKey().equals(userId)) {
                            boolean isCourseMatch = false;


                            for (DataSnapshot courseData : data.child("CourseList").getChildren()) {
                                if (courseData.child("courseName").getValue().toString().contains(courseName)) {
                                    isCourseMatch = true;
                                    break;
                                }
                            }

                            if (isCourseMatch == true) {
                                Address address = data.child("AddressInfo").child("PresentAddress").getValue(Address.class);
                                if (isMapLoaded == true) {
                                    counter = counter + 1;
                                    setMarker(new LatLng(address.getLatitude(), address.getLongitude()), data.getKey());
                                }
                            }

                        }
                    }
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (counter == 0) {
                        CustomToast.errorToast("No user found", getActivity());
                    }
                } else {
                    CustomToast.errorToast("No user found!", getActivity());
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getUserFromDB() {
        DatabaseReference userDB = databaseReference.child("UserList");
        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (isMapLoaded == true) {
                        map.clear();
                    }

                    int counter = 0;

                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        if (data.child("AddressInfo").child("PresentAddress").exists()
                                && data.child("CourseList").exists()
                                && data.child("GeneralInfo").exists()
                                && data.child("UniversityInfo").exists()
                                && !data.getKey().equals(userId)) {
                            Address address = data.child("AddressInfo").child("PresentAddress").getValue(Address.class);
                            if (isMapLoaded == true) {
                                counter = counter + 1;
                                setMarker(new LatLng(address.getLatitude(), address.getLongitude()), data.getKey());
                            }

                        }
                    }

                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (counter == 0) {
                        CustomToast.errorToast("No user found!", getActivity());
                    }
                } else {
                    CustomToast.errorToast("No user found!", getActivity());
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*-------------------------------------------------------------
    on map ready -- start
    -------------------------------------------------------------*/
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        changeMapDesign(map);

        if (locationPermissionsGranted) {

            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMapToolbarEnabled(false);
            map.getUiSettings().setMyLocationButtonEnabled(false);

        }

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                openBottomSheet(marker.getSnippet());
                return true;
            }
        });

        isMapLoaded = true;

    }

    private void changeMapDesign(GoogleMap map) {
        try {
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                    getActivity(), R.raw.map_style));


        } catch (Resources.NotFoundException e) {

        }
    }

    private void openBottomSheet(String selectedUserID) {
        if (!selectedUserID.equals(null)) {
            Bundle bundle = new Bundle();
            bundle.putString("selectedUserID", selectedUserID);
            SendRequestBottomSheet sendRequestBottomSheet = new SendRequestBottomSheet();
            sendRequestBottomSheet.setArguments(bundle);
            sendRequestBottomSheet.show(((FragmentActivity) getContext()).getSupportFragmentManager(), "SendRequestBottomSheet");
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void animateCamera(LatLng latLng, float zoom) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void setMarker(LatLng latLng, String snippet) {
        map.addMarker(new MarkerOptions().position(latLng).snippet(snippet).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_primary)));
    }

    /*-------------------------------------------------------------
    on map ready -- end
    -------------------------------------------------------------*/


    private void getDeviceLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try {
            if (locationPermissionsGranted) {

                final Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            currentLocation = (Location) task.getResult();
                            if (currentLocation != null) {
                                animateCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                            }

                        } else {

                            Toast.makeText(getActivity(), "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
        }
    }


    /*-------------------------------------------------------------
    get location permission -- start
    -------------------------------------------------------------*/
    private void getLocationPermission() {
        //Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationPermissionsGranted = true;
                initializeMap();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Log.d(TAG, "onRequestPermissionsResult: called.");
        locationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            locationPermissionsGranted = false;
                            //Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    //Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    locationPermissionsGranted = true;
                    //initialize our map
                    initializeMap();
                }
            }
        }
    }

    /*-------------------------------------------------------------
    get location permission -- end
    -------------------------------------------------------------*/


    private void addFragment(Fragment fragment) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.frameLayoutId, fragment);
        ft.commit();
    }


    private void hideSoftKeyboard() {
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
