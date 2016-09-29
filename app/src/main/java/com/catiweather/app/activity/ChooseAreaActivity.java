package com.catiweather.app.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.catiweather.app.R;
import com.catiweather.app.db.CatiWeatherDB;
import com.catiweather.app.model.City;
import com.catiweather.app.model.County;
import com.catiweather.app.model.Province;
import com.catiweather.app.tools.Utils;
import com.catiweather.app.utils.HttpCallbackListener;
import com.catiweather.app.utils.HttpUtil;
import com.catiweather.app.utils.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * 遍历省市县数据
 * */
public class ChooseAreaActivity extends AppCompatActivity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView tv_title;
    private ListView lv_view;
    private ArrayAdapter<String> adapter;
    private CatiWeatherDB catiWeatherDB;
    private List<String> dataList = new ArrayList<String>();

    /** 省列表 */
    private List<Province> provinceList;
    /** 市列表 */
    private List<City> cityList;
    /** 县列表 */
    private List<County> countyList;
    /** 选中的省份 */
    private Province selectedProvince;
    /** 选中的城市 */
    private City selectedCity;
    /** 选中的级别 */
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        lv_view = (ListView) findViewById(R.id.lv_view);
        tv_title = (TextView) findViewById(R.id.tv_title);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        lv_view.setAdapter(adapter);
        catiWeatherDB = CatiWeatherDB.getInstance(this);
        lv_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("CatiWeather", "-------  List的onItemClick事件中，currentLevel当前值为：" + currentLevel + "--------");
                if(currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(i);
                    queryCities();
                } else if(currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(i);
                    Log.e("CatiWeather", "-------  选择的城市是：" + selectedCity.getCityName() + "--------");
                    queryCounties();
                } else if(currentLevel == LEVEL_COUNTY) {
                    Log.e("CatiWeather", "-------  选择的县是：" + dataList.get(i)+ "--------");
                    Utils.print("------- 选择的县代码为：" + provinceList.get(i).getProvinceCode() + "  "
                            + cityList.get(i).getCityCode() + "  " + countyList.get(i).getCountyCode() + "-------");
                }
            }
        });
        queryProvinces();//首次加载省级数据
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
     * */
    private void queryProvinces() {
        provinceList = catiWeatherDB.loadProvinces();
        if(provinceList.size() > 0) {
            dataList.clear();
            for(Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            lv_view.setSelection(0);
            tv_title.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(null, "province");
        }
    }

    /**
     * 查询选中省内所有的市，优先查询数据库中的数据，如果没有查询到再去服务器上查询
     * */
    private void queryCities() {
        cityList = catiWeatherDB.loadCities(selectedProvince.getId());
        if(cityList.size() > 0) {
            dataList.clear();
            for(City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            lv_view.setSelection(0);
            tv_title.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    /**
     * 查询选中市内所有的县，优先查询数据库中的数据，如果没有查询到再去服务器上查询
     * */
    private void queryCounties() {
        countyList = catiWeatherDB.loadCounties(selectedCity.getId());
        if(countyList.size() > 0) {
            dataList.clear();
            for(County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            lv_view.setSelection(0);
            tv_title.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }

    /** 根据传入的代号和类型从服务器上查询省市县数据 */
    private void queryFromServer(final String code, final String type) {
        String address;
        if(!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpResquest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if("province".equals(type)) {
                    result = Utility.handleProvincesResponse(catiWeatherDB, response);
                } else if("city".equals(type)) {
                    result = Utility.handleCitiesResponse(catiWeatherDB, response,selectedProvince.getId());
                } else if("county".equals(type)) {
                    result = Utility.handleCountiesResponse(catiWeatherDB, response, selectedCity.getId());
                }
                if(result) {
                    //通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)) {
                                queryProvinces();
                            } else if("city".equals(type)) {
                                queryCities();
                            } else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ChooseAreaActivity.this, "加载数据失败", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度对话框
     * */
    private void showProgressDialog() {
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载中...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    /**
     * 关闭进度对话框
     * */
    private void closeProgressDialog(){
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * 捕获back按键，根据当前级别来判断，此时应该返回市列表，省列表，还是直接退出
     * */
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if(currentLevel == LEVEL_PROVINCE) {
            finish();
        } else if(currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else if(currentLevel == LEVEL_COUNTY) {
            queryCities();
        }

    }

}
