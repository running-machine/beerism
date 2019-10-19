package com.example.beerism.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beerism.MainActivity;
import com.example.beerism.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class Login extends AppCompatActivity {
    // 인텐트 result
    private static final int RC_SIGN_IN = 900;
    // 파이어베이스 인증
    FirebaseAuth firebaseAuth;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton buttonGoogle;
    private Button buttonLogin, buttonsignUp;
    //구글 클라이언트
    private GoogleSignInClient googleSignInClient;

    @Override
    public void onStart() {
        super.onStart();
        // 활동을 초기화할 때 사용자가 현재 로그인되어 있는지 확인합니다.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buttonGoogle = findViewById(R.id.login_goolge);
        // Google 로그인을 앱에 통합
        // GoogleSignInOptions 개체를 구성할 때 requestIdToken을 호출
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        firebaseAuth = FirebaseAuth.getInstance();

        buttonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 구글로그인 버튼 응답
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // 구글 로그인 성공
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            } catch (ApiException e) {

            }
        }
    }

    // 사용자가 정상적으로 로그인한 후에 GoogleSignInAccount 개체에서 ID 토큰을 가져와서
// Firebase 사용자 인증 정보로 교환하고 Firebase 사용자 인증 정보를 사용해 Firebase에 인증합니다.
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공
                            Toast.makeText(Login.this, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            // 로그인 실패
                            Toast.makeText(Login.this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

//    public void signOut() {
//
//        mGoogleApiClient.connect();
//
//        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
//
//            @Override
//
//            public void onConnected(@Nullable Bundle bundle) {
//
//                mAuth.signOut();
//
//                if (mGoogleApiClient.isConnected()) {
//
//                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
//                        @Override
//                        public void onResult(@NonNull Status status) {
//                            if (status.isSuccess()) {
//                                Log.v("알림", "로그아웃 성공");
//                                setResult(1);
//                            } else {
//                                setResult(0);
//                            }
//                            finish();
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onConnectionSuspended(int i) {
//                Log.v("알림", "Google API Client Connection Suspended");
//                setResult(-1);
//                finish();
//            }
//        });
//    }

}
