package com.hao.demo;

import android.app.Activity;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.hao.base.net.ApiException;
import com.hao.base.utils.AppManager;
import com.hao.base.utils.RxBus;
import com.hao.base.utils.RxEvent;
import com.hao.base.utils.SwipeBackHelper;
import com.hao.base.utils.UMAnalyticsUtil;
import com.hao.base.widget.LoadingAndRetryManager;
import com.hao.demo.di.DaggerAppComponent;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.logger.Logger;


import java.io.IOException;


import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * 作者:林国定 邮件:lingguodingg@gmail.com
 * 创建时间:18/05/23 下午4:13
 * 描述:
 */
public class App extends DaggerApplication implements AppManager.Delegate {

    @Override
    public void onCreate() {
        super.onCreate();

        if (AppManager.isInOtherProcess(this)) {
            Log.e("App", "enter the other process!");
            return;
        }
        LoadingAndRetryManager.BASE_RETRY_LAYOUT_ID = R.layout.base_retry;
        LoadingAndRetryManager.BASE_LOADING_LAYOUT_ID = R.layout.base_loading;
        LoadingAndRetryManager.BASE_EMPTY_LAYOUT_ID = R.layout.base_empty;
        Hawk.init(this).build();

        // 初始化应用程序管理器
        AppManager.getInstance().init(BuildConfig.BUILD_TYPE, this);
        // 初始化滑动返回
        SwipeBackHelper.init(this, null);
        // 初始化 RxJava 错误处理器
        initRxJavaErrorHandler();

        // 初始化友盟 SDK
        UMAnalyticsUtil.initSdk("5824622df29d9859ce0034dd", BuildConfig.FLAVOR);

        RxBus.toObservable(RxEvent.AppEnterForegroundEvent.class).subscribe(appEnterForegroundEvent -> appEnterForeground());
        RxBus.toObservable(RxEvent.AppEnterBackgroundEvent.class).subscribe(appEnterBackgroundEvent -> appEnterBackground());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    /**
     * Activity 中是否包含 Fragment。用于处理友盟页面统计，避免重复统计 Activity 和 Fragment
     *
     * @param activity
     * @return
     */
    @Override
    public boolean isActivityContainFragment(Activity activity) {
        return false;
    }

    /**
     * 处理全局网络请求异常
     *
     * @param apiException
     */
    @Override
    public void handleServerException(ApiException apiException) {
        Logger.i("处理网络请求异常");
    }

    private void appEnterForeground() {
        Logger.i("应用进入前台");
    }

    private void appEnterBackground() {
        Logger.i("应用进入后台");
    }

    // 初始化 RxJava 错误处理器
    private void initRxJavaErrorHandler() {
        RxJavaPlugins.setErrorHandler(e -> {
            if (e instanceof UndeliverableException) {
                e = e.getCause();
            }
            if (e instanceof IOException) { // 没事，无关紧要的网络问题或 API 在取消时抛出的异常
                return;
            }
            if (e instanceof InterruptedException) { // 没事，一些阻塞代码被 dispose 调用中断
                return;
            }
            if ((e instanceof NullPointerException) || (e instanceof IllegalArgumentException)) { // 这可能是程序的一个bug
                Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                return;
            }
            if (e instanceof IllegalStateException) { // 这是 RxJava 或自定义操作符的一个 bug
                Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                return;
            }
            Logger.w("Undeliverable exception received, not sure what to do");
            e.printStackTrace();
        });
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().application(this).build();

    }
}