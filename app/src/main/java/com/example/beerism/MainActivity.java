package com.example.beerism;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.example.beerism.Fragment.Cu_Fragment;
import com.example.beerism.Fragment.GS_Fragment;
import com.example.beerism.Fragment.SevenEleven_Fragment;
import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;

//import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainActivity extends AppCompatActivity {
    NavigationTabStrip main_nts;
    ViewPager main_vp;
    private FloatingActionButton DAFab;
    private static final int FROM_CAMERA = 0;
    private static final int FROM_ALBUM = 1;
    FloatingActionButton DetectionFab , ListFab;
//    FloatingActionMenu arcLayout;
    View centerItem;
    // 데이터베이스 객체 인스턴스 생성
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ImageView img1;
    private Uri imgUri, photoURI, albumURI;
    private String mCurrentPhotoPath;
    FirebaseAuth auth;
    private TextView welcomText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
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
        ListFab = findViewById(R.id.fab2);
        DetectionFab = findViewById(R.id.fab1);
        main_nts = findViewById(R.id.nts_top);
        main_nts.setTabIndex(0, true);
        main_nts.setStripFactor(2);

        main_vp = findViewById(R.id.main_vp);
        main_vp.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        main_vp.setPageTransformer(true, new CubeOutTransformer());
        main_nts.setViewPager(main_vp);
        this.InitializeLayout();

        ListFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),BeerList.class);
                startActivity(intent);
            }
        });

        DetectionFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeDialog();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        //뒤로가기 버튼 누를시 navigation바 없에는 부분
    }

    private void InitializeLayout() {

        //toolBar를 통해 App Bar 생성
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.main_logo);

        //App Bar의 좌측 영영에 Drawer를 Open 하기 위한 Incon 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.list_menu);

        DrawerLayout drawLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawLayout,
                toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );

        drawLayout.addDrawerListener(actionBarDrawerToggle);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.menuitem1:
                        Toast.makeText(getApplicationContext(), "SelectedItem 1", Toast.LENGTH_SHORT).show();
                    case R.id.menuitem2:
                        Toast.makeText(getApplicationContext(), "SelectedItem 2", Toast.LENGTH_SHORT).show();
                    case R.id.menuitem3:
                        Toast.makeText(getApplicationContext(), "SelectedItem 3", Toast.LENGTH_SHORT).show();
                }

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }


//    private void makeDialog() {
//        AlertDialog.Builder alt_bld = new AlertDialog.Builder(MainActivity.this, R.style.Alert);
//        alt_bld.setTitle("사진 업로드").setIcon(R.drawable.object_detection).setCancelable(
//                false).setPositiveButton("사진촬영",
//                new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int id) {
//                        // 사진 촬영 클릭
//                        Log.v("알림", "다이얼로그 > 사진촬영 선택");
//                        takePhoto();
//                    }
//
//                }).setNeutralButton("앨범선택",
//                new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialogInterface, int id) {
//                        Log.v("알림", "다이얼로그 > 앨범선택 선택");
//                        //앨범에서 선택
//                        selectAlbum();
//                    }
//
//                }).setNegativeButton("취소   ",
//                new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int id) {
//                        Log.v("알림", "다이얼로그 > 취소 선택");
//                        // 취소 클릭. dialog 닫기.
//                        dialog.cancel();
//                    }
//
//                });
//        AlertDialog alert = alt_bld.create();
//        alert.show();
//    }
private void makeDialog() {
    final AlertDialog.Builder alt_bld = new AlertDialog.Builder(MainActivity.this);
    LayoutInflater inflater = getLayoutInflater();
    View view = inflater.inflate(R.layout.dialog_detection, null);
    alt_bld.setView(view);
    final Button choice = (Button) view.findViewById(R.id.choice_bt);
    final Button take_ph = (Button) view.findViewById(R.id.take_photo_bt);
    final Button close = (Button) view.findViewById(R.id.cancel_bt);

    final AlertDialog dialog = alt_bld.create();
    close.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dialog.cancel();
        }
    });
    AlertDialog alert = alt_bld.create();
    alert.show();
    choice.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            takePhoto();
        }
    });
    take_ph.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectAlbum();
        }
    });

}

    //사진 찍기 클릭
    public void takePhoto() {
        // 촬영 후 이미지 가져옴
        String state = Environment.getExternalStorageState();
        //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (photoFile != null) {
                    Uri providerURI = FileProvider.getUriForFile(this, "com.example.beerism.fileprovider", photoFile);
                    imgUri = providerURI;
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, providerURI);
                    startActivityForResult(intent, FROM_CAMERA);
                }
            }
        } else {
            Log.v("알림", "저장공간에 접근 불가능");
            return;
        }
    }


    public File createImageFile() throws IOException {
        String imgFileName = System.currentTimeMillis() + ".jpg";
        File imageFile = null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "file");
        if (!storageDir.exists()) {
            Log.v("알림", "storageDir 존재 x " + storageDir.toString());
            storageDir.mkdirs();
        }
        Log.v("알림", "storageDir 존재함 " + storageDir.toString());
        imageFile = new File(storageDir, imgFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    //앨범 선택 클릭
    public void selectAlbum() {
        //앨범에서 이미지 가져옴
        //앨범 열기
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");
        startActivityForResult(intent, FROM_ALBUM);
    }


    public void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
        Toast.makeText(this, "사진이 저장되었습니다", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case FROM_ALBUM: {
                //앨범에서 가져오기
                if (data.getData() != null) {
                    try {
                        File albumFile = null;

                        albumFile = createImageFile();

                        photoURI = data.getData();
                        albumURI = Uri.fromFile(albumFile);
                        galleryAddPic();
                        img1.setImageURI(photoURI);
                        //cropImage();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.v("알림", "앨범에서 가져오기 에러");
                    }
                }
                break;
            }

            case FROM_CAMERA: {
                //카메라 촬영
                try {
                    Log.v("알림", "FROM_CAMERA 처리");
                    galleryAddPic();
                    img1.setImageURI(imgUri);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
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
