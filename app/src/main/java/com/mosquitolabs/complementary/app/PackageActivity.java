package com.mosquitolabs.complementary.app;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;


public class PackageActivity extends ActionBarActivity {

    private ViewPager pager;
    private PagerAdapter pagerAdapter;
    private TextView textViewPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Utility.hideActionbar(this);

        pager = (ViewPager) findViewById(R.id.pager);
        textViewPoints = (TextView) findViewById(R.id.textViewPoints);

        pagerAdapter = new MenuPagerAdapter(getSupportFragmentManager());

        pager.setOffscreenPageLimit(2);
        pager.setBackgroundColor(Color.TRANSPARENT);
        pager.setAdapter(pagerAdapter);

        initPagerMargin();
    }


    @Override
    protected void onResume() {
        updateViews();

        super.onResume();
    }

    private void initPagerMargin() {
        pager.setPageMargin(-Utility.getWidth(this) * 8 / 100);
    }

    private void updateViews() {
        textViewPoints.setText("Points: " + LevelCollection.getInstance().getTotalPoints());
        try {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                ((PackageFragment) fragment).updateListItems();
            }
        } catch (Exception e) {
        }
    }


    public class MenuPagerAdapter extends FragmentPagerAdapter {
        public MenuPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PackageFragment.newInstance(position);
        }


        @Override
        public int getCount() {
            return LevelCollection.getInstance().getNumberOfLevels() / 30;
        }

    }

}
