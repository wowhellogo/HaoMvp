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
import android.databinding.DataBindingUtil;
import android.databinding.OnRebindCallback;
import android.databinding.ViewDataBinding;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.ViewStubCompat;
import android.transition.TransitionManager;
import android.view.ViewGroup;

import com.hao.base.BR;
import com.hao.base.R;


/**
 * 作者:林国定 邮件:lingguodingg@gmail.com
 * 创建时间:15/9/2 下午5:07
 * 描述:
 */
public abstract class BaseBindingActivity<B extends ViewDataBinding> extends BaseActivity {
    protected B mBinding;

    @Override
    protected void initContentView() {
        if (getTopBarType() == TopBarType.None) {
            if (isInjectionLoadingLayout()) {
                setBindingContentView(getRootLayoutResID());
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

    private void setBindingContentView(int layoutId) {
        mBinding = DataBindingUtil.setContentView(this, layoutId);
        mBinding.setVariable(BR.eventHandler, this);
        mBinding.addOnRebindCallback(new OnRebindCallback() {
            @Override
            public boolean onPreBind(ViewDataBinding binding) {
                ViewGroup view = (ViewGroup) binding.getRoot();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(view);
                }
                return true;
            }
        });
    }

    private void inflateBinding(ViewGroup container) {
        mBinding = DataBindingUtil.inflate(getLayoutInflater(), getRootLayoutResID(), container, false);
        container.addView(mBinding.getRoot());
        mBinding.setVariable(BR.eventHandler, this);
    }


    @SuppressLint("RestrictedApi")
    protected void initTitleBarContentView() {
        super.setContentView(isLinear() ? R.layout.rootlayout_linear : R.layout.rootlayout_merge);
        ViewStubCompat toolbarVs = findViewById(R.id.toolbarVs);
        toolbarVs.setLayoutResource(R.layout.inc_titlebar);
        toolbarVs.inflate();
        mTitleBar = findViewById(R.id.titleBar);


        ViewStubCompat viewStub = findViewById(R.id.contentVs);
        viewStub.setLayoutResource(R.layout.contentlayout_coordinatort);
        viewStub.inflate();
        ViewGroup contentVs =findViewById(R.id.content_layout);
        inflateBinding(contentVs);
        if (isInjectionLoadingLayout()) {
            setLoadingAndRetryManager(contentVs);
        }
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
        viewStub.setLayoutResource(R.layout.contentlayout_coordinatort);
        viewStub.inflate();
        ViewGroup contentVs =findViewById(R.id.content_layout);
        inflateBinding(contentVs);
        if (isInjectionLoadingLayout()) {
            setLoadingAndRetryManager(contentVs);
        }
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
    }

    @Override
    protected void setListener() {
    }
}