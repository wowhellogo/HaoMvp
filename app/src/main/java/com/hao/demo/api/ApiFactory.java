package com.hao.demo.api;

import com.hao.base.net.NetConverterFactory;
import com.hao.base.utils.AppManager;
import com.hao.demo.Constant;

import java.util.concurrent.TimeUnit;

import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * 作者:林国定 邮件:lingguodingg@gmail.com
 * 创建时间:2018/5/28
 * 描述:
 */
public class ApiFactory {

    public static final String BASE_URL = "";
    public static final String DEBUG_URL = "";
    private Api mApi;

    private ApiFactory() {
        boolean isBuildDebug = AppManager.getInstance().isBuildDebug();
        HttpLoggingInterceptor.Level logLevel = isBuildDebug ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE;
        OkHttpClient client = new OkHttpClient().newBuilder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(logLevel))
                .connectTimeout(10000, TimeUnit.MILLISECONDS)
                .readTimeout(10000, TimeUnit.MILLISECONDS)
                .writeTimeout(10000, TimeUnit.MILLISECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(isBuildDebug ? Constant.DEBUG_URL : Constant.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())) // 指定在 io 线程进行网络请求
                .addConverterFactory(NetConverterFactory.create())
                .client(client)
                .build();
        mApi = retrofit.create(Api.class);

    }

    private static class SingletonHolder {
        private static final ApiFactory INSTANCE = new ApiFactory();
    }

    public static ApiFactory getInstance() {
        return ApiFactory.SingletonHolder.INSTANCE;
    }

    public static Api getApi() {
        return getInstance().mApi;
    }

}
