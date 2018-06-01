package com.hao.demo.di;

import android.app.Application;
import android.content.Context;

import dagger.Binds;
import dagger.Module;

/**
 * @Desc
 * @Author linguoding
 * @Email lingguodingg@gmail.com
 * @Date 2018/5/10
 */
@Module
public abstract class ApplicationModule {
    //expose Application as an injectable context
    @Binds
    abstract Context bindContext(Application application);
}
