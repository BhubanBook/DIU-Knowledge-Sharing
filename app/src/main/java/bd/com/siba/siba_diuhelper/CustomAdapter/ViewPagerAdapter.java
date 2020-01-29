package bd.com.siba.siba_diuhelper.CustomAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitleList = new ArrayList<>();


    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }


    @Override
    public int getCount() {

        return fragmentTitleList.size();

    }


    @Override
    public CharSequence getPageTitle(int position) {

        return fragmentTitleList.get(position);

    }


    public void addFragment(Fragment fragment,String title){

        fragmentList.add(fragment);
        fragmentTitleList.add(title);

    }

}
