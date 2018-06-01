package com.hao.demo;

import android.os.Bundle;

import com.hao.base.view.BaseBindingFragment;
import com.hao.base.view.TopBarType;
import com.hao.demo.databinding.FragmentDemoBinding;

/**
 * 作者:林国定 邮件:lingguodingg@gmail.com
 * 创建时间:2018/6/1
 * 描述:
 */
public class DemoFragment extends BaseBindingFragment<FragmentDemoBinding> {
    @Override
    protected int getRootLayoutResID() {
        return R.layout.fragment_demo;
    }

    @Override
    protected void setListener() {

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
    protected void initView(Bundle savedInstanceState) {
        mBinding.tvName.setText("Fragment databinding setName");
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }
}
