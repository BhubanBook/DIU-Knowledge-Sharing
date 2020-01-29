package bd.com.siba.siba_diuhelper.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bd.com.siba.siba_diuhelper.CustomAdapter.ViewPagerAdapter;
import bd.com.siba.siba_diuhelper.R;


public class MyActivityFragment extends Fragment {


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;


    public MyActivityFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_activity, container, false);

        adapter= new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        viewPager=view.findViewById(R.id.viewPager_id);
        tabLayout=view.findViewById(R.id.tabLayout_id);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter.addFragment(new ReceiveFragment(),"Request Received");
        adapter.addFragment(new SendFragment(),"Request Sent");

        viewPager.setOffscreenPageLimit(2);
        viewPager.setSaveFromParentEnabled(false);
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
    }
}
