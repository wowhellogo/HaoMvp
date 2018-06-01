package com.hao.base.view;

/**
 * @Desc
 * @Author linguoding
 * @Email lingguodingg@gmail.com
 * @Date 2018/5/8
 */

public interface BasePresenter<T> {
    /**
     * Binds presenter with a view when resumed. The Presenter will perform initialization here.
     *
     * @param view the view associated with this presenter
     */
    void takeView(T view);

    /**
     * Drops the reference to the view when destroyed
     */
    void dropView();
}
