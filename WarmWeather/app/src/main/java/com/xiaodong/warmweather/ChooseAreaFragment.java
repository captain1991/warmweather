package com.xiaodong.warmweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaodong.warmweather.db.City;
import com.xiaodong.warmweather.db.County;
import com.xiaodong.warmweather.db.Province;
import com.xiaodong.warmweather.util.HttpUtil;
import com.xiaodong.warmweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by yxd on 2017/2/20.
 */
public class ChooseAreaFragment extends Fragment {
    private ImageView back;
    private TextView title;
    private ListView area_list;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> datalist = new ArrayList<>();
    private final int LEVEL_PROVINCE = 0;
    private final int LEVEL_CITY = 1;
    private final int LEVEL_COUNTY = 2;
    private int level;
    List<City> cityList = new ArrayList<>();
    List<Province> provinceList = new ArrayList<>();
    List<County> countyList = new ArrayList<>();
    Province selectedProvince;
    City selectedCity;
    public static final String SELECTED_WEATHERCODE="selected_weathercode";
    public static final String SELECTED_CITYNAME="selected_cityname";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container);
        back = (ImageView) view.findViewById(R.id.image_back);
        title = (TextView) view.findViewById(R.id.text_title);
        area_list = (ListView) view.findViewById(R.id.list_area);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, datalist);
        area_list.setAdapter(arrayAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (level==LEVEL_CITY){
                    queryProvince();
                }else if(level == LEVEL_COUNTY){
                    queryCity();
                }
            }
        });
        area_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (level == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCity();
                }else if(level==LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCounty();
                }else if (level == LEVEL_COUNTY){
                    Intent intent = new Intent();
                    intent.setAction("com.warmweather.act");
                    intent.putExtra(SELECTED_CITYNAME, countyList.get(position).getCountyName());
                    intent.putExtra(SELECTED_WEATHERCODE, countyList.get(position).getWeather_id());
                    startActivity(intent);
                }
            }
        });
        queryProvince();
    }

    public void queryProvince() {
        level = LEVEL_PROVINCE;
        provinceList.clear();
        provinceList = DataSupport.findAll(Province.class);
        back.setVisibility(View.GONE);
        title.setText("中国");
        if (provinceList != null && provinceList.size() > 0) {
            datalist.clear();
            for (Province province : provinceList) {
                datalist.add(province.getProvinceName());
            }
            area_list.setSelection(0);
            arrayAdapter.notifyDataSetChanged();

        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, LEVEL_PROVINCE);
        }
    }

    public void queryCity(){
        level = LEVEL_CITY;
        cityList.clear();
        cityList = DataSupport.where("provinceId=?",String.valueOf(selectedProvince.getId())).find(City.class);
        back.setVisibility(View.VISIBLE);
        title.setText(selectedProvince.getProvinceName());
        if(cityList.size()>0){
            datalist.clear();
            for (City city:cityList){
                datalist.add(city.getCityName());
            }
            arrayAdapter.notifyDataSetChanged();
            area_list.smoothScrollToPosition(0);
            area_list.setSelection(0);

        }else {
            String address =  "http://guolin.tech/api/china/"+selectedProvince.getProvinceCode();
            Log.e("address", address);
            queryFromServer(address,LEVEL_CITY);
        }
    }

    public void queryCounty(){
        level = LEVEL_COUNTY;
        countyList.clear();
        countyList = DataSupport.where("cityId=?",String.valueOf(selectedCity.getId())).find(County.class);
        title.setText(selectedCity.getCityName());
        back.setVisibility(View.VISIBLE);
        if(countyList.size()!=0){
            datalist.clear();
            for (County county:countyList) {
                datalist.add(county.getCountyName());
            }
            arrayAdapter.notifyDataSetChanged();
            area_list.setSelection(0);
        }else {
            String address = "http://guolin.tech/api/china/"+selectedProvince.getProvinceCode()+"/"+selectedCity.getCityCode();
            Log.e("address", address);
            queryFromServer(address,LEVEL_COUNTY);
        }

    }

    public void queryFromServer(String adderss, final int level) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(adderss, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dismissProgressDialog();
                Toast.makeText(getActivity(), "获取地址失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                Log.e("response=========", response.body().string());

                boolean result;
                switch (level) {
                    case LEVEL_PROVINCE:
                        result = Utility.handleProvinceResponse(response);
                        if (result) {
                            getActivity().runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            dismissProgressDialog();
                                            queryProvince();
                                        }
                                    }
                            );
                        }
                        break;
                    case LEVEL_CITY:
                        result = Utility.handleCityResponse(selectedProvince.getId(), response);
                        if (result) {
                            getActivity().runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            dismissProgressDialog();
                                            queryCity();
                                        }
                                    }
                            );
                        }
                        break;
                    case LEVEL_COUNTY:
                        result = Utility.handleCountyResponse(selectedCity.getId(), response);
                        if (result) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissProgressDialog();
                                    queryCounty();
                                }
                            });
                        }
                        break;
                }
            }
        });
    }
    ProgressDialog dialog;
    public void showProgressDialog(){
        if(dialog==null) {
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("加载中...");
            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.show();
    }
    public  void dismissProgressDialog(){
        if (dialog!=null){
            dialog.dismiss();
        }
    }
}
