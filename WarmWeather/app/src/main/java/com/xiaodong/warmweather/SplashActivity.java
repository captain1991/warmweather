package com.xiaodong.warmweather;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {
    private List<String> permissionList;
    private boolean isCanJump;
    private RelativeLayout contaner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        contaner = (RelativeLayout) findViewById(R.id.contaner);
        permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(permissionList.size()>0){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions,0);
            }else {
                fetchSplashAD();
            }
        }else {
            fetchSplashAD();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isCanJump=false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isCanJump){
            next();
        }
        isCanJump=true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case 0:
                if(grantResults.length>0){
                    for(int grantResult:grantResults){
                        if(grantResult!=PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"请同意所有相关权限",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
                fetchSplashAD();
                break;
        }

    }

    private void fetchSplashAD(){
        SplashAD splashAD = new SplashAD(this, contaner, "1106001444", "6010910918774574", new SplashADListener() {
            @Override
            public void onADDismissed() {
                next();
            }

            @Override
            public void onNoAD(int i) {
                Intent intent = new Intent(SplashActivity.this, AreaActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onADPresent() {

            }

            @Override
            public void onADClicked() {

            }

            @Override
            public void onADTick(long l) {

            }
        });
    }

    public void next(){
        if(isCanJump) {
            Intent intent = new Intent(this, AreaActivity.class);
            startActivity(intent);
            finish();
        }else {
            isCanJump = true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK||keyCode==KeyEvent.KEYCODE_HOME){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
