package com.example.beerism;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.example.beerism.Login.Login;

public class Splash extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        // gif 로딩 시작
        ImageView rabbit = (ImageView) findViewById(R.id.gif_image);
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(rabbit);
        Glide.with(this).load(R.drawable.splash).listener(new RequestListener<Integer, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                GifDrawable gifDrawable = null;
                Handler handler = new Handler();
                if (resource instanceof GifDrawable) {
                    gifDrawable = (GifDrawable) resource;

                    int duration = 0; // gif 로딩 완료 시간을 기준으로 다음 화면으로 넘어가기 위한 변수
                    GifDecoder decoder = gifDrawable.getDecoder();
                    for (int i = 0; i < gifDrawable.getFrameCount(); i++) {
                        duration += decoder.getDelay(i);
                    }

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(getApplication(), Login.class));
                            Splash.this.finish();
                        }
                    }, duration - 9000);

                }

                return false;
            }
        }).into(gifImage);

        // gif 로딩에 관계없이 특정 시간만 스플래쉬 할 경우 사용
//        Handler hd = new Handler();
//        hd.postDelayed(new splashhandler(), 5000); // 5초 후에 hd handler 실행  3000ms = 3초
    }

    private class SplashHandler implements Runnable {
        public void run(){
            startActivity(new Intent(getApplicationContext(), Login.class)); //로딩이 끝난 후, ChoiceFunction 이동
            Splash.this.finish(); // 로딩페이지 Activity stack에서 제거
        }
    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }
}