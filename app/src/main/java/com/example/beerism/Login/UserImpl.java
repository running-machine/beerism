package com.example.beerism.Login;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.beerism.VO.UsersVO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserImpl implements MemberRepository {
    private static final String TAG = UserImpl.class.getSimpleName();
    private Context context;
    private FirebaseFirestore firebaseFirestore;
    private CustomApplication app;

    public UserImpl(Context context) {
        app = CustomApplication.getInstance();
        firebaseFirestore = app.getDbInstance();
    }

    // 이미 존재하는 계정(이메일)이 있을경우
    @Override
    public void doesUserEmailExist(String email, final UsersVO usersVO) {
        Query query = firebaseFirestore.collection(Constants.USER_COLLECTION).whereEqualTo("email", email);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (Objects.requireNonNull(task.getResult()).size() > 0) {
                    Toast.makeText(context, "이미 계정이 있습니다.", Toast.LENGTH_LONG).show();
                } else {
                    addNewRegisteredUser(usersVO);
                }
            }
        });
    }

    @Override
    public void addNewRegisteredUser(UsersVO usersVO) {
        String UserEnterName = usersVO.getName();

        Map<String, Object> user = new HashMap<>();

        user.put(Constants.DocumentFileds.NAME, usersVO.getName());
        user.put(Constants.DocumentFileds.EMAIL, usersVO.getEmail());
        user.put(Constants.DocumentFileds.PASSWORD, usersVO.getPassword());

        Task<Void> newUser = firebaseFirestore.collection(Constants.USER_COLLECTION).document(usersVO.getEmail()).set(user);
        newUser.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "정상적으로 회원가입되었습니다.", Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "회원가입에 실패했습니다.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void getLoginUserByEmail(String email) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(Constants.USER_COLLECTION).document(email)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    UsersVO user = Objects.requireNonNull(task.getResult()).toObject(UsersVO.class);
                    if (user != null) {
                        Toast.makeText(context, "유저정보는" + user.getEmail() + "입니다.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "유저 정보가 없습니다.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    String excep = Objects.requireNonNull(task.getException()).getMessage();
                    Toast.makeText(context, excep + "오류발생!", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "실패!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
