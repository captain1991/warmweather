package com.xiaodong.warmweather;

import com.xiaomi.ad.AdSdk;

import org.litepal.LitePalApplication;

/**
 * Created by yxd on 2017/2/28.
 */
public class MyApplication extends LitePalApplication{
//    2882303761517552375
    private static final String APP_ID = "2882303761517552375";

    @Override
    public void onCreate() {
        super.onCreate();
        AdSdk.setDebugOn();
        AdSdk.setMockOn();
        AdSdk.initialize(this, APP_ID);
    }
}
