package com.hao.demo.di;

import android.app.Application;

import com.hao.demo.App;
import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * @Desc app 注入器
 * @Author linguoding
 * @Email lingguodingg@gmail.com
 * @Date 2018/5/10
 */
@Singleton
@Component(modules = {
        ApplicationModule.class,
        BindingModule.class,
        AndroidSupportInjectionModule.class})
public interface AppComponent extends AndroidInjector<App> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        AppComponent.Builder application(Application application);

        AppComponent build();
    }

}
