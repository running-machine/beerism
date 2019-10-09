package com.example.beerism;


import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.gigamole.navigationtabstrip.NavigationTabStrip;



public class MainActivity extends AppCompatActivity {
    NavigationTabStrip main_nts;
    ViewPager main_vp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        main_nts = findViewById(R.id.nts_top);
        main_nts.setTabIndex(0, true);
        main_nts.setStripFactor(2);

        main_vp = findViewById(R.id.main_vp);
        main_vp.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        main_vp.setPageTransformer(true, new CubeOutTransformer());
        main_nts.setViewPager(main_vp);

    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }


        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 1:
                    return GS_Fragment.instantiate();
                case 2:
                    return SevenEleven_Fragment.instantiate();
                default:
                    return Cu_Fragment.instantiate();
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

}
