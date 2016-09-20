package com.catiweather.app.utils;

/**
 * 回调服务返回的结果响应interface
 * Created by Administrator on 2016/9/20.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
