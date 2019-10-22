package com.example.beerism.Login;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.beerism.R;
import com.example.beerism.VO.UsersVO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = SignUpActivity.class.getSimpleName();
    UsersVO usersVO;
    private AppCompatEditText usernameBox, emailBox, passwordBox;
    String username, password, email;
    private Context context;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_signup);

        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.hide();
        }

        AppCompatTextView signUpLink = (AppCompatTextView) findViewById(R.id.signup_link);
        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(SignUpActivity.this, Login.class);
                startActivity(loginIntent);
                finish();
            }
        });

        usernameBox = findViewById(R.id.username);
        emailBox = findViewById(R.id.email);
        passwordBox = findViewById(R.id.password);

        AppCompatButton signUpBtn = findViewById(R.id.signup_btn);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = Objects.requireNonNull(usernameBox.getText()).toString();
                password = Objects.requireNonNull(passwordBox.getText()).toString();
                email = Objects.requireNonNull(emailBox.getText()).toString();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUpActivity.this, "필수 데이터를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    //todo authenticate with Firestore
                    firebaseFirestore.collection(Constants.USER_COLLECTION)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    //데이터를 가져오는 작업이 잘 작동했을 경우
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                            if (documentSnapshot.getString("email").equals(emailBox.getText().toString())) {
                                                Toast.makeText(getApplicationContext(), "이메일이 이미 존재합니다.", Toast.LENGTH_SHORT).show();
                                                break;
                                            } else {
                                                Map<String, Object> user = new HashMap<>();
                                                user.put(Constants.DocumentFileds.NAME, username);
                                                user.put(Constants.DocumentFileds.EMAIL, email);
                                                user.put(Constants.DocumentFileds.PASSWORD, password);
                                                firebaseFirestore.collection(Constants.USER_COLLECTION).document(email).set(user);
                                            }
                                        }
                                    } else {
                                        task.getException();
                                    }
                                }

                            });
                }
            }
        });
    }
}