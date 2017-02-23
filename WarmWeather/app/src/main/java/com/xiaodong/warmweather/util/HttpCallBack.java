package com.xiaodong.warmweather.util;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by yxd on 2017/2/23.
 */
public class HttpCallBack implements Callback {
    @Override
    public void onFailure(Call call, IOException e) {
        LogUtil.e("onFailure======"+e.toString());
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        LogUtil.e("onResponse======="+response.body().string());
    }
}
