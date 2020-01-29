package bd.com.siba.siba_diuhelper.Activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import bd.com.siba.siba_diuhelper.CustomAdapter.PlaceAutocompleteAdapter;
import bd.com.siba.siba_diuhelper.OtherClass.CustomToast;
import bd.com.siba.siba_diuhelper.OtherClass.LocationType;
import bd.com.siba.siba_diuhelper.R;


public class PickLocationMapActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener{

    private FirebaseAuth firebaseAuth;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(23.661769, 90.3196123), new LatLng(23.891162,90.5063353));

    private GoogleApiClient googleApiClient;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private FragmentManager fragmentManager;
    private GoogleMapOptions googleMapOptions;
    private GoogleMap map;
    private Boolean locationPermissionsGranted = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String address;
    private LatLng addressLatLng;
    private String userID;
    private String locationType;
    private Boolean isAutoCompleteItemClicked = false;

    private AutoCompleteTextView pickLocationACTV;
    private ImageView searchBtnIV;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_location);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        locationType = getIntent().getStringExtra("locationType");
        initialize();

        getLocationPermission();
    }

    private void initialize() {

        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        firebaseAuth = FirebaseAuth.getInstance();
        userID=firebaseAuth.getCurrentUser().getUid();


        pickLocationACTV = findViewById(R.id.pickLocationATVID);
        searchBtnIV = findViewById(R.id.searchBtnIV);

        fragmentManager = getSupportFragmentManager();
        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this,googleApiClient,LAT_LNG_BOUNDS,null);
        pickLocationACTV.setOnItemClickListener(pickLocationAutocompleteClickListener);
        pickLocationACTV.setAdapter(placeAutocompleteAdapter);
    }


    private void initializeMap(){

        googleMapOptions = new GoogleMapOptions();
        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance(googleMapOptions);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().replace(R.id.frameContainerID, supportMapFragment);
        fragmentTransaction.commit();
        supportMapFragment.getMapAsync(this);
    }



    private void getDeviceLocation(){

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(locationPermissionsGranted){

                final Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),14);

                        }else{
                            CustomToast.errorToast("Unable to access your current location",PickLocationMapActivity.this);
                        }
                    }
                });
            }
        }catch (SecurityException e){
        }
    }


    /*-------------------------------------------------------------
    on map ready -- start
    -------------------------------------------------------------*/
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        if (locationPermissionsGranted) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            getDeviceLocation();

            map.setMyLocationEnabled(true);
            map.getUiSettings().setMapToolbarEnabled(false);
            map.getUiSettings().setMyLocationButtonEnabled(false);


            map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {

                    LatLng latLng = map.getCameraPosition().target;

                    address = getAddress(latLng.latitude,latLng.longitude);
                    addressLatLng = latLng;

                    /*if (isAutoCompleteItemClicked==false){
                        pickLocationACTV.setText(address);
                        pickLocationACTV.dismissDropDown();
                    }*/
                    pickLocationACTV.setText(address);
                    pickLocationACTV.dismissDropDown();
                }
            });
        }
    }

    private void moveCamera(LatLng latLng, float zoom){
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }

    /*-------------------------------------------------------------
    on map ready -- end
    -------------------------------------------------------------*/





    /*-------------------------------------------------------------
    request for location permission -- start
    -------------------------------------------------------------*/
    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationPermissionsGranted = true;
                initializeMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        locationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            locationPermissionsGranted = false;
                            return;
                        }
                    }
                    locationPermissionsGranted = true;
                    //initialize our map
                    initializeMap();
                }
            }
        }
    }

    /*-------------------------------------------------------------
    request for location permission -- end
    -------------------------------------------------------------*/








    /*-------------------------------------------------------------
    Google place autocomplete -- start
    -------------------------------------------------------------*/
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private AdapterView.OnItemClickListener pickLocationAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            getItemFromAdapter(i);
        }
    };


    private void getItemFromAdapter(int i) {

        AutocompletePrediction item = placeAutocompleteAdapter.getItem(i);
        String placeId = item.getPlaceId();

        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                .getPlaceById(googleApiClient, placeId);

        placeResult.setResultCallback(updatePickLocationDetailsCallback);

    }



    private ResultCallback<PlaceBuffer> updatePickLocationDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            getDetailsFromCallBack(places);
        }
    };


    private void getDetailsFromCallBack(PlaceBuffer places) {
        if(!places.getStatus().isSuccess()){
            places.release();
            return;
        }
        Place place = places.get(0);

        isAutoCompleteItemClicked = true;
        moveCamera(place.getLatLng(),14);

        places.release();

        hideSoftKeyboard();
    }

    /*-------------------------------------------------------------
    Google place autocomplete -- end
    -------------------------------------------------------------*/





    private void hideSoftKeyboard(){
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }


    public String getAddress(double lat, double lon){
        String address = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(lat,lon,1);
            if (addresses.size()>0){
                address = addresses.get(0).getAddressLine(0);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }



    private void saveLocationInfoToDB() {
        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("UserList/"+userID);

        Map<String,Object> locationMap = new HashMap<>();
        locationMap.put("address",address);
        locationMap.put("latitude",addressLatLng.latitude);
        locationMap.put("longitude",addressLatLng.longitude);

        if (locationType.equals(LocationType.PERMANENT)){
            userDB.child("AddressInfo").child("PermanentAddress").updateChildren(locationMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            onBackPressed();
                        }
                    });
        }else if (locationType.equals(LocationType.PRESENT)){
            userDB.child("AddressInfo").child("PresentAddress").updateChildren(locationMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            onBackPressed();
                        }
                    });
        }

    }



    public void onDoneBtnClick(View view) {
        saveLocationInfoToDB();
    }

    public void MyLocationBtnTap(View view) {
        isAutoCompleteItemClicked = false;
        getDeviceLocation();
    }

    public void searchBtnTap(View view) {
        isAutoCompleteItemClicked = false;

        openKeyboard(pickLocationACTV);
        pickLocationACTV.setEnabled(true);
        pickLocationACTV.setText("");

    }

    private void openKeyboard(View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(
                view.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }


    //---------------------Top-left back button or phone's back button clicked---------------------
    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition( R.anim.stay, R.anim.slide_out_up );
    }
}
