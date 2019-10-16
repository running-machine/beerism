package com.example.beerism;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gun0912.tedpermission.PermissionListener;

import java.util.List;


public class Object_Detection_Camera extends AppCompatActivity {
    private static final int FROM_CAMERA = 0;
    private static final int FROM_ALBUM = 1;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionListener permissionlistener = new PermissionListener() {

            @Override
            public void onPermissionGranted() {

                Toast.makeText(Object_Detection_Camera.this, "Permission Granted", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(Object_Detection_Camera.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };
//        new TedPermission(this)
//
//                .setPermissionListener(permissionlistener)
//
//                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
//
//                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
//
//                .check();
    }


}

