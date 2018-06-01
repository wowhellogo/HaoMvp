/**
 * Copyright 2016 bingoogolapple
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hao.base.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ViewStubCompat;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding2.view.RxView;
import com.hao.base.R;
import com.hao.base.utils.KeyboardUtil;
import com.hao.base.utils.PermissionUtil;
import com.hao.base.utils.StatusBarUtil;
import com.hao.base.utils.SwipeBackHelper;
import com.hao.base.widget.LoadingAndRetryManager;
import com.hao.base.widget.OnLoadingAndRetryListener;
import com.hao.base.widget.TitleBar;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.List;
import java.util.concurrent.TimeUnit;

import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 作者:林国定 邮件:lingguodingg@gmail.com
 * 创建时间:15/9/2 下午5:07
 * 描述:
 */
public abstract class BaseActivity extends DaggerAppCompatActivity implements EasyPermissions.PermissionCallbacks,
        PermissionUtil.Delegate, TitleBar.Delegate, SwipeBackHelper.Delegate, LifecycleProvider<ActivityEvent> {
    private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();
    protected SwipeBackHelper mSwipeBackHelper;
    protected MaterialDialog mLoadingDialog;

    protected TitleBar mTitleBar;
    protected Toolbar mToolbar;
    protected LoadingAndRetryManager mLoadingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initSwipeBackFinish();
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(ActivityEvent.CREATE);
        initContentView();
        showContent();
        initView(savedInstanceState);
        processLogic(savedInstanceState);
        setListener();

    }

    protected void retryEvent() {

    }

    @Override
    @CallSuper
    protected void onStart() {
        super.onStart();
        lifecycleSubject.onNext(ActivityEvent.START);
    }

    @Override
    @CallSuper
    protected void onResume() {
        super.onResume();
        lifecycleSubject.onNext(ActivityEvent.RESUME);
    }

    @Override
    @CallSuper
    protected void onPause() {
        lifecycleSubject.onNext(ActivityEvent.PAUSE);
        super.onPause();
    }

    @Override
    @CallSuper
    protected void onStop() {
        lifecycleSubject.onNext(ActivityEvent.STOP);
        super.onStop();
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        lifecycleSubject.onNext(ActivityEvent.DESTROY);
        super.onDestroy();
    }

    /**
     * 初始化滑动返回。在 super.onCreate(savedInstanceState) 之前调用该方法
     */
    private void initSwipeBackFinish() {
        mSwipeBackHelper = new SwipeBackHelper(this, this);
        // 设置滑动返回是否可用。默认值为 true
        mSwipeBackHelper.setSwipeBackEnable(true);
        // 设置是否仅仅跟踪左侧边缘的滑动返回。默认值为 true
        mSwipeBackHelper.setIsOnlyTrackingLeftEdge(true);
        // 设置是否是微信滑动返回样式。默认值为 true
        mSwipeBackHelper.setIsWeChatStyle(true);
        // 设置阴影资源 id。默认值为 R.drawable.bga_sbl_shadow
        mSwipeBackHelper.setShadowResId(R.drawable.sbl_shadow);
        // 设置是否显示滑动返回的阴影效果。默认值为 true
        mSwipeBackHelper.setIsNeedShowShadow(true);
        // 设置阴影区域的透明度是否根据滑动的距离渐变。默认值为 true
        mSwipeBackHelper.setIsShadowAlphaGradient(true);
        // 设置触发释放后自动滑动返回的阈值，默认值为 0.3f
        mSwipeBackHelper.setSwipeBackThreshold(0.3f);
        // 设置底部导航条是否悬浮在内容上，默认值为 false
        mSwipeBackHelper.setIsNavigationBarOverlap(false);
    }


    protected void addFragment(int containerViewId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(containerViewId, fragment);
        fragmentTransaction.commit();
    }

    protected void replaceFragment(int containerViewId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 是否支持滑动返回。这里在父类中默认返回 true 来支持滑动返回，如果某个界面不想支持滑动返回则重写该方法返回 false 即可
     *
     * @return
     */
    @Override
    public boolean isSupportSwipeBack() {
        return true;
    }

    /**
     * 正在滑动返回
     *
     * @param slideOffset 从 0 到 1
     */
    @Override
    public void onSwipeBackLayoutSlide(float slideOffset) {
    }

    /**
     * 没达到滑动返回的阈值，取消滑动返回动作，回到默认状态
     */
    @Override
    public void onSwipeBackLayoutCancel() {
    }

    /**
     * 滑动返回执行完毕，销毁当前 Activity
     */
    @Override
    public void onSwipeBackLayoutExecuted() {
        mSwipeBackHelper.swipeBackward();
    }

    @Override
    public void onBackPressed() {
        // 正在滑动返回的时候取消返回按钮事件
        if (mSwipeBackHelper.isSliding()) {
            return;
        }
        mSwipeBackHelper.backward();
    }


    protected void setLoadingAndRetryManager(Object activityOrFragment) {
        mLoadingManager = LoadingAndRetryManager.generate(activityOrFragment, new OnLoadingAndRetryListener() {
            @Override
            public void setRetryEvent(View retryView) {
                retryEvent();
            }
        });
    }

    protected void initContentView() {
        if (getTopBarType() == TopBarType.None) {
            if (isInjectionLoadingLayout()) {
                setContentView(getRootLayoutResID());
                setLoadingAndRetryManager(this);
            } else {
                setContentView(getRootLayoutResID());
            }
        } else if (getTopBarType() == TopBarType.TitleBar) {
            initTitleBarContentView();
        } else if (getTopBarType() == TopBarType.Toolbar) {
            initToolbarContentView();
        }
        initButterKnifeInjectView();

    }

    protected void initButterKnifeInjectView() {

    }

    @SuppressLint("RestrictedApi")
    protected void initToolbarContentView() {
        super.setContentView(isLinear() ? R.layout.rootlayout_linear : R.layout.rootlayout_merge);

        ViewStubCompat toolbarVs = findViewById(R.id.toolbarVs);
        toolbarVs.setLayoutResource(R.layout.inc_toolbar);
        toolbarVs.inflate();

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewStubCompat viewStub = findViewById(R.id.contentVs);
        viewStub.setLayoutResource(getRootLayoutResID());
        View contentVs = viewStub.inflate();
        if (isInjectionLoadingLayout()) {
            setLoadingAndRetryManager(contentVs);
        }
    }

    @SuppressLint("RestrictedApi")
    protected void initTitleBarContentView() {
        super.setContentView(isLinear() ? R.layout.rootlayout_linear : R.layout.rootlayout_merge);

        ViewStubCompat toolbarVs = findViewById(R.id.toolbarVs);
        toolbarVs.setLayoutResource(R.layout.inc_titlebar);
        toolbarVs.inflate();

        mTitleBar = findViewById(R.id.titleBar);

        ViewStubCompat viewStub = findViewById(R.id.contentVs);
        viewStub.setLayoutResource(getRootLayoutResID());
        View contentVs = viewStub.inflate();
        if (isInjectionLoadingLayout()) {
            setLoadingAndRetryManager(contentVs);
        }
    }

    /**
     * 有 TitleBar 或者 Toolbar 时，是否为线性布局
     *
     * @return
     */
    protected boolean isLinear() {
        return true;
    }

    protected TopBarType getTopBarType() {
        return TopBarType.None;
    }


    /**
     * 是否注入LoadingLayout到内容布局
     *
     * @return
     */
    protected boolean isInjectionLoadingLayout() {
        return false;
    }

    /**
     * 显示加载布局
     */
    public void showLoading() {
        if (null != mLoadingManager) {
            mLoadingManager.showLoading();
        }
    }

    /**
     * 显示错误重试布局
     */
    public void showRetry() {
        if (null != mLoadingManager) {
            mLoadingManager.showRetry();
        }
    }

    /**
     * 显示内容布局
     */
    public void showContent() {
        if (null != mLoadingManager) {
            mLoadingManager.showContent();
        }
    }

    /**
     * 显示空布局
     */
    public void showEmpty() {
        if (null != mLoadingManager) {
            mLoadingManager.showEmpty();
        }
    }


    @Override
    public void setTitle(CharSequence title) {
        if (getTopBarType() == TopBarType.None) {
            super.setTitle(title);
        } else if (getTopBarType() == TopBarType.TitleBar) {
            mTitleBar.setTitleText(title);
        } else if (getTopBarType() == TopBarType.Toolbar) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onClickLeftCtv() {
        onBackPressed();
    }

    @Override
    public void onClickRightCtv() {
    }

    @Override
    public void onClickRightSecondaryCtv() {
    }

    @Override
    public void onClickTitleCtv() {
    }

    /**
     * 设置点击事件，并防止重复点击
     *
     * @param id
     * @param consumer
     */
    protected void setOnClick(@IdRes int id, Consumer consumer) {
        setOnClick(findViewById(id), consumer);
    }

    /**
     * 设置点击事件，并防止重复点击
     *
     * @param view
     * @param consumer
     */
    protected void setOnClick(View view, Consumer consumer) {
        RxView.clicks(view).throttleFirst(500, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(consumer);
    }

    /**
     * 获取布局文件根视图
     *
     * @return
     */
    protected abstract
    @LayoutRes
    int getRootLayoutResID();

    /**
     * 初始化View控件
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * 给View控件添加事件监听器
     */
    protected abstract void setListener();

    /**
     * 处理业务逻辑，状态恢复等操作
     *
     * @param savedInstanceState
     */
    protected abstract void processLogic(Bundle savedInstanceState);

    /**
     * 执行跳转到下一个Activity的动画
     */
    public void executeForwardAnim() {
        mSwipeBackHelper.executeForwardAnim();
    }

    /**
     * 执行回到到上一个Activity的动画
     */
    public void executeBackwardAnim() {
        mSwipeBackHelper.executeBackwardAnim();
    }

    /**
     * 跳转到下一个 Activity，并且销毁当前 Activity
     *
     * @param cls 下一个 Activity 的 Class
     */
    public void forwardAndFinish(Class<?> cls) {
        mSwipeBackHelper.forwardAndFinish(cls);
    }

    /**
     * 跳转到下一个 Activity，不销毁当前 Activity
     *
     * @param cls 下一个 Activity 的 Class
     */
    public void forward(Class<?> cls) {
        mSwipeBackHelper.forward(cls);
    }

    /**
     * 跳转到下一个 Activity，不销毁当前 Activity
     *
     * @param cls         下一个 Activity 的 Class
     * @param requestCode 请求码
     */
    public void forward(Class<?> cls, int requestCode) {
        mSwipeBackHelper.forward(cls, requestCode);
    }

    /**
     * 跳转到下一个 Activity，销毁当前 Activity
     *
     * @param intent 下一个 Activity 的意图对象
     */
    public void forwardAndFinish(Intent intent) {
        mSwipeBackHelper.forwardAndFinish(intent);
    }

    /**
     * 跳转到下一个 Activity,不销毁当前 Activity
     *
     * @param intent 下一个 Activity 的意图对象
     */
    public void forward(Intent intent) {
        mSwipeBackHelper.forward(intent);
    }

    /**
     * 跳转到下一个 Activity,不销毁当前 Activity
     *
     * @param intent      下一个 Activity 的意图对象
     * @param requestCode 请求码
     */
    public void forward(Intent intent, int requestCode) {
        mSwipeBackHelper.forward(intent, requestCode);
    }

    /**
     * 回到上一个 Activity，并销毁当前 Activity
     */
    public void backward() {
        mSwipeBackHelper.backward();
    }

    /**
     * 回到上一个 Activity，并销毁当前 Activity（应用场景：欢迎、登录、注册这三个界面）
     *
     * @param cls 上一个 Activity 的 Class
     */
    public void backwardAndFinish(Class<?> cls) {
        mSwipeBackHelper.backwardAndFinish(cls);
    }

    /**
     * 显示加载对话框
     *
     * @param resId
     */
    public void showLoadingDialog(@StringRes int resId) {
        showLoadingDialog(getString(resId));
    }

    public void showLoadingDialog(String msg) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new MaterialDialog.Builder(this)
                    .progress(true, 0)
                    .cancelable(false)
                    .build();
        }
        mLoadingDialog.setContent(msg);
        mLoadingDialog.show();
    }

    /**
     * 隐藏加载对话框
     */
    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        KeyboardUtil.handleAutoCloseKeyboard(isAutoCloseKeyboard(), getCurrentFocus(), ev, this);
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 点击非 EditText 时，是否自动关闭键盘
     *
     * @return
     */
    protected boolean isAutoCloseKeyboard() {
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        PermissionUtil.onPermissionsDenied(this, this, requestCode);
    }

    /**
     * 某些权限被永久拒绝
     *
     * @param requestCode 权限请求码
     */
    @Override
    public void onSomePermissionDenied(int requestCode) {
    }

    /**
     * 点击取消打开权限设置界面
     *
     * @param requestCode 权限请求码
     */
    @Override
    public void onClickCancelOpenPermissionsSettingsScreen(int requestCode) {
    }

    /**
     * 设置状态栏颜色
     *
     * @param color
     */
    protected void setStatusBarColor(@ColorInt int color) {
        setStatusBarColor(color, StatusBarUtil.DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * 设置状态栏颜色
     *
     * @param color
     * @param statusBarAlpha 透明度
     */
    public void setStatusBarColor(@ColorInt int color, @IntRange(from = 0, to = 255) int statusBarAlpha) {
        StatusBarUtil.setColorForSwipeBack(this, color, statusBarAlpha);
    }


    @Override
    @NonNull
    @CheckResult
    public final Observable<ActivityEvent> lifecycle() {
        return lifecycleSubject.hide();
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull ActivityEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindActivity(lifecycleSubject);
    }


}