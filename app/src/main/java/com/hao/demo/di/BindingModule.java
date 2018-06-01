package com.hao.demo.di;

import com.hao.demo.DemoFragment;
import com.hao.demo.MainActivity;
import com.hao.demo.MainActivity2;
import com.hao.demo.MainFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * @Desc
 * @Author linguoding
 * @Email lingguodingg@gmail.com
 * @Date 2018/5/10
 */

@Module
public abstract class BindingModule {
    /*@ActivityScoped
    @ContributesAndroidInjector(modules = TasksModule.class)
    abstract TasksActivity tasksActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = AddEditTaskModule.class)
    abstract AddEditTaskActivity addEditTaskActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = StatisticsModule.class)
    abstract StatisticsActivity statisticsActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = TaskDetailPresenterModule.class)
    abstract TaskDetailActivity taskDetailActivity();*/

    /*@ServerScoped
    @ContributesAndroidInjector(modules = MoScalesServerModule.class)
    abstract MoScalesServer moScalesServer();*/

    @ActivityScoped
    @ContributesAndroidInjector
    abstract MainActivity mainActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract MainActivity2 mainActivity2();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract MainFragment mainFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract DemoFragment demoFragment();


}
