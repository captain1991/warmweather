package com.xiaodong.warmweather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {
    LocationClient locationClient;
    TextView text_location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        text_location = (TextView) findViewById(R.id.text_location);
        locationClient = new LocationClient(this);
        locationClient.registerLocationListener(new MyLocationListener());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionList = new ArrayList<>();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (permissionList.size() > 0) {
                String[] permissions = permissionList.toArray(new String[permissionList.size()]);
                requestPermissions(permissions, 1);
            } else {
                queryLocation();
            }
        } else {
            queryLocation();
        }
    }

    private void queryLocation() {
        locationClient.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if(grantResults.length>0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    queryLocation();
                }else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            StringBuilder position = new StringBuilder();
            position.append("纬度：").append(bdLocation.getLatitude()+"\n");
            position.append("经度：").append(bdLocation.getLongitude() + "\n");
            position.append("定位方式\n");
            if(bdLocation.getLocType()==BDLocation.TypeGpsLocation){
                position.append("GPS\n");
            }else if (bdLocation.getLocType()==BDLocation.TypeNetWorkLocation){
                position.append("网络\n");
            }
            text_location.setText(position);
        }
    }
}
