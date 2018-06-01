package com.hao.demo

import android.os.Bundle
import com.hao.base.view.BaseActivity
import com.hao.base.view.TopBarType

class MainActivity : BaseActivity() {
    override fun getRootLayoutResID(): Int {
        return R.layout.activity_main

    }

    override fun getTopBarType(): TopBarType {
        return TopBarType.TitleBar
    }

    override fun isInjectionLoadingLayout(): Boolean {
        return true
    }

    override fun initView(savedInstanceState: Bundle?) {
        showLoading()
    }

    override fun setListener() {

    }

    override fun processLogic(savedInstanceState: Bundle?) {

    }


}
