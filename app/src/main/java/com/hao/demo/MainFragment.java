package com.hao.demo;

import android.os.Bundle;

import com.hao.base.view.BaseFragment;

import butterknife.ButterKnife;

/**
 * 作者:林国定 邮件:lingguodingg@gmail.com
 * 创建时间:2018/5/31
 * 描述:
 */
public class MainFragment extends BaseFragment {
    @Override
    protected int getRootLayoutResID() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        showLoading();
    }


    @Override
    protected boolean isInjectionLoadingLayout() {
        return true;
    }

    @Override
    protected void initButterKnifeInjectView() {
        ButterKnife.bind(this, mContentView);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }
}
