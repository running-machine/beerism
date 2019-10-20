package com.example.beerism.Login;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.beerism.R;

import java.util.Objects;


public class Login extends AppCompatActivity {
    public static final String TAG = Login.class.getSimpleName();

    private AppCompatEditText emailbox;
    private AppCompatEditText passwordbox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.hide();
        }

        AppCompatTextView forgotPasswordBtn =  findViewById(R.id.forgot_password);
        forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Login.this, "Todo - Forgot password implementation", Toast.LENGTH_SHORT).show();
            }
        });

        AppCompatTextView signUpBtn = findViewById(R.id.signup_btn);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Login.this, "Todo - Sign implementation", Toast.LENGTH_SHORT).show();
                Intent signUpIntent = new Intent(Login.this, SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });

        emailbox = findViewById(R.id.email);
        passwordbox = findViewById(R.id.password);

        AppCompatButton submitBtn =  findViewById(R.id.login_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAddress = Objects.requireNonNull(emailbox.getText()).toString();
                Log.d(TAG, "Log email " + emailAddress);
                String password = Objects.requireNonNull(passwordbox.getText()).toString();

                if (TextUtils.isEmpty(emailAddress) || TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Login Fields must not be empty", Toast.LENGTH_SHORT).show();
                } else if (!emailAddress.contains("@")) {
                    Toast.makeText(Login.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                } else {
                    UserImpl userImplementation = new UserImpl(Login.this);
                    userImplementation.getLoginUserByEmail(emailAddress);
                }
            }
        });
    }
}

