package com.example.beerism;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.example.beerism.Login.Constants;
import com.example.beerism.Recommandation.CFAlgorithm;
import com.example.beerism.VO.ChoiceBeerVO;
import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.yarolegovich.lovelydialog.LovelySaveStateHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private String TAG = "ObjectDetection";
    NavigationTabStrip main_nts;
    ViewPager main_vp;
    private static final int ID_SINGLE_CHOICE_DIALOG = R.id.fab3;
    private LovelySaveStateHandler saveStateHandler;
    private static final int FROM_CAMERA = 0;
    private static final int FROM_ALBUM = 1;
    FloatingActionButton DetectionFab, ListFab, recFab;
    private FirebaseFirestore firebaseFirestore;

    private AlertDialog alert;

//    FloatingActionMenu arcLayout;

    private Uri imgUri, photoURI, albumURI;
    private String mCurrentPhotoPath;
    FirebaseAuth auth;

    String loginEmail = "fjdjs@naver.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        saveStateHandler = new LovelySaveStateHandler();

        firebaseFirestore = FirebaseFirestore.getInstance();

//        // 앱 최초 실행 여부 //
//        SharedPreferences pref = getSharedPreferences("isFirst", Activity.MODE_PRIVATE);
//
//        // 타겟 지정
//        Target target = new ViewTarget(R.id.main_vp, this);
//
//        boolean first = pref.getBoolean("isFirst", false);
//
//        if (!first) {
//            SharedPreferences.Editor editor = pref.edit();
//            editor.putBoolean("isFirst", true);
//            editor.apply();
//            new ShowcaseView.Builder(this)
//                    .setTarget(target)
//                    .setContentTitle("ShowcaseView")
//                    .setContentText("This is highlighting the Home button")
//                    .hideOnTouchOutside()
//                    .build();
//        }

        recFab = findViewById(R.id.fab3);
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
                Intent intent = new Intent(getApplicationContext(), BeerList.class);
                startActivity(intent);
            }
        });

//        initDialog(); // DetectionFab에 click 메소드를 할당하기 전에 dialog 초기화
        DetectionFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),DetectorActivity.class);
                startActivity(intent);
                finish();
            }
        });


//        String[] choiceItems = getResources().getStringArray(R.array)

        final ArrayAdapter<ChoiceBeerVO> arrayAdapter = new choiceBeer(this, loadDonationOptions());
        recFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection(Constants.SCORE_COLLECTION)
                    .get()
                    .addOnCompleteListener(task -> {
                        //데이터를 가져오는 작업이 잘 작동했을 경우
                        if (task.isSuccessful()) {
                            CFAlgorithm recoObj = CFAlgorithm.getInstance();
                            recoObj.initData();
                            Map<String, Double> movieInfo = new HashMap<>();
                            List<Map.Entry<String, Double>> rank = new ArrayList<>();
                            // 기존 데이터를 가져옴
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> tempMovies = document.getData();
//                                Log.d(TAG, document.getId());
                                for (String key : tempMovies.keySet()) {
                                    movieInfo.put(key, Double.parseDouble(tempMovies.get(key).toString()));
//                                    Log.d(TAG, "\t" + key + "의 점수는 " + tempMovies.get(key).toString() + "점");
                                }

                                recoObj.inputData(document.getId(), movieInfo);
                            }

                            // 기존 사용자들에 따라 예상 평점을 추출
                            for (Map.Entry<String, Double> item : CFAlgorithm.getInstance().getRecommandation(loginEmail)) {
                                rank.add(item);
                            }

                            for (Map.Entry<String, Double> item : CFAlgorithm.getInstance().getUserData(loginEmail)) {
                                rank.add(item);
                            }

                            rank.sort((o1, o2) -> {
                                if (o1.getValue() == o2.getValue()) return 0;
                                else if (o1.getValue() > o2.getValue()) return -1;
                                else return 1;
                            });

//                            Log.d(TAG, loginEmail + "의 개인 점수");
//                            for (Map.Entry<String, Double> item : rank) {
//                                Log.d(TAG, item.getKey() + "의 점수는 " + item.getValue() + "점");
//                            }
                            String[] beer;
                            String[] beer_list = {"hoegaarden", "kloud", "filite", "filgood", "filiteweizen",
                                    "jejuale", "tsingtao", "casslight", "filitefresh", "terra",
                                    "hite", "cass", "heineken", "budweiser", "fitz" };

                            int size = rank.size() >= 3 ? 3 : rank.size();
                            beer = new String[size];
                            for (int i = 0; i < size ; i++) {
                                beer[i] = rank.get(i).getKey() + " : " + rank.get(i).getValue();
                            }

                            survey_dialog(beer, beer_list);
                        } else {
                            task.getException();
                        }
                    });
            }
        });

    }
    void survey_dialog(String[] beer, String[] beer_list)
    {
        final List<String> ListItems = new ArrayList<>();
        for(String item : beer){
            ListItems.add(item);
        }
        final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("AlertDialog Title");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                score_dialog(items[which].toString());
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("다른맥주", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                other_beer(beer_list);
            }
        });
        builder.show();
    }
    private void other_beer(String[] beer_list){
        final List<String> ListItems = new ArrayList<>();
        for(String item : beer_list){
            ListItems.add(item);
        }
        final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("AlertDialog Title");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                score_dialog(items[which].toString());
        }
    });
        builder.show();
    }
    private void score_dialog(String beerName){
//        Log.d(TAG, "선택된 아이템 : " + beerName);
        final List<String> ListItems = new ArrayList<>();
        ListItems.add("1점");
        ListItems.add("2점");
        ListItems.add("3점");
        ListItems.add("4점");
        ListItems.add("5점");
        final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);
        final List SelectedItems  = new ArrayList();
        int defaultItem = 0;
        SelectedItems.add(defaultItem);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder overlapBuilder = new AlertDialog.Builder(this);
        builder.setTitle("맥주 평점");
        builder.setSingleChoiceItems(items, defaultItem,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SelectedItems.clear();
                        SelectedItems.add(which);
                    }
                });
        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String msg="";

                        if (!SelectedItems.isEmpty()) {
                            int index = (int) SelectedItems.get(0);
                            String seledteditem = ListItems.get(index);
                            seledteditem = seledteditem.substring(0, seledteditem.length() - 1);

                            firebaseFirestore.collection(Constants.SCORE_COLLECTION).document(loginEmail).update(beerName, Double.parseDouble(seledteditem));
                            msg = ListItems.get(index);
                        }

                        Toast.makeText(getApplicationContext(),
                                "Items Selected.\n"+ msg , Toast.LENGTH_LONG)
                                .show();
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }


    private List<ChoiceBeerVO> loadDonationOptions() {
        List<ChoiceBeerVO> result = new ArrayList<>();
        String[] raw = getResources().getStringArray(R.array.choiceStyle);
        for (String op : raw) {
//            String[] info = op.split("%");
            result.add(new ChoiceBeerVO(raw[0], raw[1]));
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // 뒤로가기 버튼 누를시 navigation바 없에는 부분
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void InitializeLayout() {
        //toolBar를 통해 App Bar 생성
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.main_logo);

        //App Bar의 좌측 영영에 Drawer를 Open 하기 위한 Icon 추가
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
                switch (menuItem.getItemId()) {
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
//                false).setPositiveButton("사진촬영", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // 사진 촬영 클릭
//                        Log.v("알림", "다이얼로그 > 사진촬영 선택");
//                        takePhoto();
//                    }
//                }).setNeutralButton("앨범선택", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialogInterface, int id) {
//                        Log.v("알림", "다이얼로그 > 앨범선택 선택");
//                        //앨범에서 선택
//                        selectAlbum();
//                    }
//                }).setNegativeButton("취소   ", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        Log.v("알림", "다이얼로그 > 취소 선택");
//                        dialog.cancel();
//                    }
//                });
//        AlertDialog alert = alt_bld.create();
//        alert.show();
//    }

//    private void initDialog() {
//        AlertDialog.Builder alt_bld = new AlertDialog.Builder(MainActivity.this);
//        LayoutInflater inflater = getLayoutInflater();
//        View view = inflater.inflate(R.layout.dialog_detection, null);
//
//        final Button take_ph = (Button) view.findViewById(R.id.take_photo_bt);
//        final Button choice = (Button) view.findViewById(R.id.choice_bt);
//        final Button close = (Button) view.findViewById(R.id.cancel_bt);
//
//        alt_bld.setView(view);
//        alert = alt_bld.create();
//
//        take_ph.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                takePhoto();
//            }
//        });
//        choice.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                selectAlbum();
//            }
//        });
//        close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                alert.cancel();
//            }
//        });
//    }

    /**
     * 사진 촬영 후 해당 파일을 임시적으로 저장하는 메소드
     */
    public void takePhoto() {
        // 촬영 후 이미지 가져옴
        String state = Environment.getExternalStorageState();

        // 사진 촬영 권한 확인
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 0); // 권한 허용 요구
        }

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
                    // 찍은 사진을 onActivityResult 메소드에 넘김
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, providerURI);
                    startActivityForResult(intent, FROM_CAMERA);
                }
            }
        } else {
            Log.v("알림", "저장공간에 접근 불가능");
            return;
        }
    }

    /**
     * 임시적으로 생긴 파일의 경로를 mCurrentPhotoPath에 넣고 반환하는 메소드
     *
     * @return
     * @throws IOException
     */
    public File createImageFile() throws IOException {
        String imgFileName = System.currentTimeMillis() + ".jpg";
        File imageFile = null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "file");
        // 이미지 파일 경로가 없으면 생성
        if (!storageDir.exists()) {
            Log.v("알림", "storageDir 존재 x " + storageDir.toString());
            storageDir.mkdirs();
        }

        Log.v("알림", "storageDir 존재함 " + storageDir.toString());

        imageFile = new File(storageDir, imgFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }

    public void selectAlbum() {
        //앨범 열기
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");
        startActivityForResult(intent, FROM_ALBUM);
    }

    /**
     * mCurrentPhotoPath의 임시 파일을 저장
     */
    public void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
        Toast.makeText(this, "사진이 저장되었습니다", Toast.LENGTH_SHORT).show();
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