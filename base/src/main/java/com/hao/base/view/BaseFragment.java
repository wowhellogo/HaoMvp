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
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ViewStubCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;
import com.hao.base.R;
import com.hao.base.utils.AppManager;
import com.hao.base.utils.KeyboardUtil;
import com.hao.base.utils.PermissionUtil;
import com.hao.base.utils.UMAnalyticsUtil;
import com.hao.base.widget.LoadingAndRetryManager;
import com.hao.base.widget.OnLoadingAndRetryListener;
import com.hao.base.widget.TitleBar;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;
import com.trello.rxlifecycle2.components.support.RxFragment;

import java.util.List;
import java.util.concurrent.TimeUnit;

import dagger.android.support.DaggerFragment;
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
public abstract class BaseFragment extends DaggerFragment implements EasyPermissions.PermissionCallbacks,
        PermissionUtil.Delegate, TitleBar.Delegate, LifecycleProvider<FragmentEvent> {
    protected View mContentView;
    protected BaseActivity mActivity;

    protected boolean mIsLoadedData = false;

    protected TitleBar mTitleBar;
    protected Toolbar mToolbar;


    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();
    private LoadingAndRetryManager mLoadingManager;

    @Override
    @NonNull
    @CheckResult
    public final Observable<FragmentEvent> lifecycle() {
        return lifecycleSubject.hide();
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull FragmentEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindFragment(lifecycleSubject);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getTopBarType() == TopBarType.None && isInjectionLoadingLayout()) {
            setLoadingAndRetryManager(this);
        }
        showContent();
        initView(savedInstanceState);
        processLogic(savedInstanceState);
        setListener();
        lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
    }

    @Override
    public void onStart() {
        super.onStart();
        lifecycleSubject.onNext(FragmentEvent.START);
    }


    @Override
    public void onStop() {
        lifecycleSubject.onNext(FragmentEvent.STOP);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
        super.onDestroyView();
    }


    @Override
    public void onDetach() {
        lifecycleSubject.onNext(FragmentEvent.DETACH);
        super.onDetach();
    }


    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        lifecycleSubject.onNext(FragmentEvent.ATTACH);
        mActivity = (BaseActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 避免多次从xml中加载布局文件
        if (mContentView == null) {
            initContentView();
        } else {
            ViewGroup parent = (ViewGroup) mContentView.getParent();
            if (parent != null) {
                parent.removeView(mContentView);
            }
        }
        return mContentView;
    }

    protected void initContentView() {
        if (getTopBarType() == TopBarType.None) {
            mContentView = LayoutInflater.from(getActivity()).inflate(getRootLayoutResID(), null);
        } else if (getTopBarType() == TopBarType.TitleBar) {
            initTitleBarContentView();
        } else if (getTopBarType() == TopBarType.Toolbar) {
            initToolbarContentView();
        }
        initButterKnifeInjectView();

    }


    protected void initButterKnifeInjectView() {

    }

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


    protected void setLoadingAndRetryManager(Object activityOrFragment) {
        mLoadingManager = LoadingAndRetryManager.generate(activityOrFragment, new OnLoadingAndRetryListener() {
            @Override
            public void setRetryEvent(View retryView) {
                retryEvent();
            }
        });
    }


    /**
     * 重试
     */
    private void retryEvent() {

    }


    @SuppressLint("RestrictedApi")
    protected void initTitleBarContentView() {
        mContentView = LayoutInflater.from(getActivity()).inflate(isLinear() ? R.layout.rootlayout_linear : R.layout.rootlayout_frame, null);

        ViewStubCompat toolbarVs = findViewById(R.id.toolbarVs);
        toolbarVs.setLayoutResource(R.layout.inc_titlebar);
        toolbarVs.inflate();

        mTitleBar = findViewById(R.id.titleBar);
        mTitleBar.setDelegate(this);

        ViewStubCompat viewStub = findViewById(R.id.contentVs);
        viewStub.setLayoutResource(getRootLayoutResID());
        View contentVs = viewStub.inflate();
        if (isInjectionLoadingLayout()) {
            setLoadingAndRetryManager(contentVs);
        }
    }

    @Override
    public void onClickLeftCtv() {
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

    @SuppressLint("RestrictedApi")
    protected void initToolbarContentView() {
        mContentView = LayoutInflater.from(getActivity()).inflate(isLinear() ? R.layout.rootlayout_linear : R.layout.rootlayout_frame, null);

        ViewStubCompat toolbarVs = findViewById(R.id.toolbarVs);
        toolbarVs.setLayoutResource(R.layout.inc_toolbar);
        toolbarVs.inflate();
        mToolbar = findViewById(R.id.toolbar);

        ViewStubCompat viewStub = findViewById(R.id.contentVs);
        viewStub.setLayoutResource(getRootLayoutResID());
        View contentVs = viewStub.inflate();
        if (isInjectionLoadingLayout()) {
            setLoadingAndRetryManager(contentVs);
        }
        setHasOptionsMenu(true);
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed()) {
            handleOnVisibilityChangedToUser(isVisibleToUser);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        lifecycleSubject.onNext(FragmentEvent.RESUME);
        if (getUserVisibleHint()) {
            handleOnVisibilityChangedToUser(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        lifecycleSubject.onNext(FragmentEvent.PAUSE);
        if (getUserVisibleHint()) {
            handleOnVisibilityChangedToUser(false);
        }
    }

    /**
     * 处理对用户是否可见
     *
     * @param isVisibleToUser
     */
    private void handleOnVisibilityChangedToUser(boolean isVisibleToUser) {
        UMAnalyticsUtil.onVisibilityChangedToUser(this, isVisibleToUser);

        if (isVisibleToUser) {
            // 对用户可见
            if (!mIsLoadedData) {
                Logger.d(this.getClass().getSimpleName() + " 懒加载一次");
                mIsLoadedData = true;
                onLazyLoadOnce();
            }
            Logger.d(this.getClass().getSimpleName() + " 对用户可见");
            onVisibleToUser();
        } else {
            // 对用户不可见
            Logger.d(this.getClass().getSimpleName() + " 对用户不可见");
            onInvisibleToUser();
        }
    }

    /**
     * 懒加载一次。如果只想在对用户可见时才加载数据，并且只加载一次数据，在子类中重写该方法
     */
    protected void onLazyLoadOnce() {
    }

    /**
     * 对用户可见时触发该方法。如果只想在对用户可见时才加载数据，在子类中重写该方法
     */
    protected void onVisibleToUser() {
    }

    /**
     * 对用户不可见时触发该方法
     */
    protected void onInvisibleToUser() {
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
     * 初始化 View 控件
     */
    protected void initView(Bundle savedInstanceState) {
    }

    /**
     * 给 View 控件添加事件监听器
     */
    protected abstract void setListener();

    /**
     * 处理业务逻辑，状态恢复等操作
     *
     * @param savedInstanceState
     */
    protected abstract void processLogic(Bundle savedInstanceState);

    /**
     * 跳转到下一个 Activity，并且销毁当前 Activity
     *
     * @param cls 下一个 Activity 的 Class
     */
    public void forwardAndFinish(Class<?> cls) {
        forward(cls);
        mActivity.finish();
    }

    /**
     * 跳转到下一个 Activity，不销毁当前 Activity
     *
     * @param cls 下一个 Activity 的 Class
     */
    public void forward(Class<?> cls) {
        KeyboardUtil.closeKeyboard(mActivity);
        startActivity(new Intent(mActivity, cls));
        mActivity.executeForwardAnim();
    }

    public void forward(Class<?> cls, int requestCode) {
        forward(new Intent(mActivity, cls), requestCode);
    }

    public void forwardAndFinish(Intent intent) {
        forward(intent);
        mActivity.finish();
    }

    public void forward(Intent intent) {
        KeyboardUtil.closeKeyboard(mActivity);
        startActivity(intent);
        mActivity.executeForwardAnim();
    }

    public void forward(Intent intent, int requestCode) {
        KeyboardUtil.closeKeyboard(mActivity);
        startActivityForResult(intent, requestCode);
        mActivity.executeForwardAnim();
    }

    /**
     * 回到上一个 Activity，并销毁当前 Activity
     */
    public void backward() {
        KeyboardUtil.closeKeyboard(mActivity);
        mActivity.finish();
        mActivity.executeBackwardAnim();
    }

    /**
     * 回到上一个 Activity，并销毁当前 Activity（应用场景：欢迎、登录、注册这三个界面）
     *
     * @param cls 上一个 Activity 的 Class
     */
    public void backwardAndFinish(Class<?> cls) {
        KeyboardUtil.closeKeyboard(mActivity);
        startActivity(new Intent(mActivity, cls));
        mActivity.executeBackwardAnim();
        mActivity.finish();
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
     * 查找View
     *
     * @param id   控件的id
     * @param <VT> View类型
     * @return
     */
    protected final <VT extends View> VT findViewById(@IdRes int id) {
        return (VT) mContentView.findViewById(id);
    }

    @Override
    public void onDestroy() {
        lifecycleSubject.onNext(FragmentEvent.DESTROY);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
}