package com.xiaodong.warmweather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.xiaodong.warmweather.db.County;
import com.xiaodong.warmweather.util.LogUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {
    LocationClient locationClient;
    TextView text_location;
    MapView mapView;
    BaiduMap baiduMap;
    boolean isFirstLocate = true;
    BDLocation bdLocation;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map);
        text_location = (TextView) findViewById(R.id.text_location);
        locationClient = new LocationClient(this);
        locationClient.registerLocationListener(new MyLocationListener());
        mapView = (MapView) findViewById(R.id.mapView);
        button = (Button) findViewById(R.id.dingwei);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocation();
            }
        });
    }

    private void queryLocation() {
        initLocation();
        locationClient.start();
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        locationClient.setLocOption(option);
    }

    private void navigateTo(BDLocation location){
        if(isFirstLocate) {
            LogUtil.d("navigateTo==========");
            LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(26f);
            baiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        MyLocationData.Builder builder = new MyLocationData.Builder().latitude(location.getLatitude()).longitude(location.getLongitude());
        MyLocationData myLocationData =builder.build();
        baiduMap.setMyLocationData(myLocationData);
    }

    private void showLocation(){
        LatLng ll = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
        baiduMap.animateMapStatus(update);
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

    @Override
    protected void onDestroy() {
        locationClient.stop();
        super.onDestroy();
    }

    class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            MapActivity.this.bdLocation = bdLocation;
            StringBuilder position = new StringBuilder();
            position.append("纬度：").append(bdLocation.getLatitude() + "\n");
            position.append("经度：").append(bdLocation.getLongitude() + "\n");
            position.append("市：").append(bdLocation.getCity() + "\n");
            position.append("定位方式\n");
            if(bdLocation.getLocType()==BDLocation.TypeGpsLocation){
                position.append("GPS\n");
            }else if (bdLocation.getLocType()==BDLocation.TypeNetWorkLocation){
                position.append("网络\n");
            }
            text_location.setText(position);
            navigateTo(bdLocation);
//            setSelectedCity(bdLocation.getCity().replace("市", ""));
        }
    }

    private void setSelectedCity(String cityName){
        List<County> countys =DataSupport.where("countyName=?", cityName).find(County.class);
        for (County county:countys) {
            LogUtil.d(county.getCountyName());
        }
    }
}
