package com.example.beerism;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.example.beerism.Fragment.Cu_Fragment;
import com.example.beerism.Fragment.GS_Fragment;
import com.example.beerism.Fragment.SevenEleven_Fragment;
import com.gigamole.navigationtabstrip.NavigationTabStrip;
<<<<<<< HEAD

=======
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.github.clans.fab.FloatingActionButton;
>>>>>>> 2be9244fed9c83fde6c744e9bf996ec123057257
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {
    NavigationTabStrip main_nts;
    ViewPager main_vp;

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

<<<<<<< HEAD
=======

>>>>>>> 2be9244fed9c83fde6c744e9bf996ec123057257
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
<<<<<<< HEAD
=======

        // 앱 최초 실행 여부 //
        SharedPreferences pref = getSharedPreferences("isFirst", Activity.MODE_PRIVATE);

        // 타겟 지정
        Target target = new ViewTarget(R.id.main_vp, this);

        boolean first = pref.getBoolean("isFirst", false);

        if (!first) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isFirst", true);
            editor.apply();
            new ShowcaseView.Builder(this)
                    .setTarget(target)
                    .setContentTitle("ShowcaseView")
                    .setContentText("This is highlighting the Home button")
                    .hideOnTouchOutside()
                    .build();
        }


        fab_detetion = findViewById(R.id.menu_item_detect);
        fab_rec = findViewById(R.id.menu_item_recommand);
        fab_search = findViewById(R.id.menu_item_search);
>>>>>>> 2be9244fed9c83fde6c744e9bf996ec123057257

//        fab_detetion = findViewById(R.id.menu_item_detect);
//        fab_rec = findViewById(R.id.menu_item_recommand);
//        fab_search = findViewById(R.id.menu_item_search);

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
