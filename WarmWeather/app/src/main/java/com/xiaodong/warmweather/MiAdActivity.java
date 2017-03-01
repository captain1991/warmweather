package com.xiaodong.warmweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.xiaomi.ad.SplashAdListener;
import com.xiaomi.ad.adView.SplashAd;

public class MiAdActivity extends AppCompatActivity {
    private static final String POSITION_ID = "a4e869f6f95f350000b3ae84b5c87048";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_ad);
        FrameLayout contanier = (FrameLayout) findViewById(R.id.splash_ad_container);
        SplashAd splashAd = new SplashAd(this, contanier, new SplashAdListener() {
            @Override
            public void onAdPresent() {

            }

            @Override
            public void onAdClick() {
                gotoNextActivity();
            }

            @Override
            public void onAdDismissed() {
                gotoNextActivity();
            }

            @Override
            public void onAdFailed(String s) {
                gotoNextActivity();
            }
        });
        splashAd.requestAd(POSITION_ID);
    }

    private void gotoNextActivity() {
        Intent intent = new Intent(this, AreaActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // 捕获back键，在展示广告期间按back键，不跳过广告
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
