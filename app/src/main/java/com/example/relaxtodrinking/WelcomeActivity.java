package com.example.relaxtodrinking;

/***************************************************************/


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

/***************************************************************/

public class WelcomeActivity extends AppCompatActivity {
    private String TAG = "歡迎畫面";
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//
    private ImageView ivLogo_Welcome;
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝宣告＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getSupportActionBar().hide(); //隱藏標題列
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        ivLogo_Welcome = findViewById(R.id.ivLogo_Welcome);
        ivLogo_Welcome.setImageResource(R.drawable.app_logo_nobackground);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(2500);
                    startActivity(new Intent().setClass(WelcomeActivity.this,MainActivity.class));
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
