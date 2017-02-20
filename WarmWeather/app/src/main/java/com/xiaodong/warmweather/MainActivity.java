package com.xiaodong.warmweather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.xiaodong.warmweather.util.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HttpUtil.sendOkHttpRequest("http://guolin.tech/api/china", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                Toast.makeText(MainActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                Log.e("failure=======", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("response=======",response.body().string());
            }
        });

    }
}
