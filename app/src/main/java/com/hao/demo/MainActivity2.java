package com.hao.demo;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.widget.TextView;

import com.hao.base.view.BaseActivity;
import com.hao.base.view.BaseBindingActivity;
import com.hao.base.view.TopBarType;
import com.hao.demo.databinding.ActivityMain2Binding;


/**
 * 作者:林国定 邮件:lingguodingg@gmail.com
 * 创建时间:2018/5/29
 * 描述:
 */
public class MainActivity2 extends BaseBindingActivity<ActivityMain2Binding> {

    @Override
    protected int getRootLayoutResID() {
        return R.layout.activity_main2;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        addFragment(R.id.fl_root, new DemoFragment());
        mBinding.tvText.setText("奔奔d");
        mBinding.tvText.setTextSize(36);
        //showLoading();

    }

    @Override
    protected TopBarType getTopBarType() {
        return TopBarType.TitleBar;
    }

    @Override
    protected boolean isInjectionLoadingLayout() {
        return true;
    }


    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

}
