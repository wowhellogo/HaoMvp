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

package com.hao.base.utils;

import android.app.Activity;
import android.support.annotation.CallSuper;
import android.support.annotation.StringRes;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hao.base.R;

import io.reactivex.subscribers.ResourceSubscriber;

/**
 * 作者:林国定 邮件:lingguoding@gmail.com
 * 创建时间:16/8/14 上午1:15
 * 描述:
 */
public abstract class LocalSubscriber<T> extends ResourceSubscriber<T> {
    protected MaterialDialog mLoadingDialog;
    protected Activity mActivity;
    protected String mMsg;
    protected boolean mCancelable;

    public LocalSubscriber() {
    }

    public LocalSubscriber(Activity activity) {
        this(activity, R.string.loading);
    }

    public LocalSubscriber(Activity activity, boolean cancelable) {
        this(activity, R.string.loading, cancelable);
    }

    public LocalSubscriber(Activity activity, @StringRes int resId) {
        this(activity, activity.getString(resId));
    }

    public LocalSubscriber(Activity activity, @StringRes int resId, boolean cancelable) {
        this(activity, activity.getString(resId), cancelable);
    }

    public LocalSubscriber(Activity activity, String msg) {
        this(activity, msg, true);
    }

    public LocalSubscriber(Activity activity, String msg, boolean cancelable) {
        mActivity = activity;
        mMsg = msg;
        mCancelable = cancelable;
    }

    @Override
    public void onStart() {
        if (mActivity != null && StringUtil.isNotEmpty(mMsg)) {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(mActivity)
                    .content(mMsg)
                    .progress(true, 0);

            if (mCancelable) {
                // 点击取消的时候取消订阅
                builder.cancelListener(dialog -> {
                    if (!isDisposed()) {
                        dispose();
                    }
                });
            }

            mLoadingDialog = builder.show();
        }
    }

    @CallSuper
    @Override
    public void onComplete() {
        dismissLoadingDialog();
    }

    @Override
    public void onError(Throwable e) {
        if (AppManager.getInstance().isBuildDebug()) {
            e.printStackTrace();
        }

        dismissLoadingDialog();

        onError(AppManager.getApp().getString(R.string.try_again_later));
    }

    /**
     * 隐藏加载对话框
     */
    protected void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    public void onError(String msg) {
        ToastUtil.showSafe(msg);
    }
}
