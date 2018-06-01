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

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * 作者:林国定 邮件:lingguoding@gmail.com
 * 创建时间:16/1/2 上午12:23
 * 描述:使用 RxJava 来实现 EventBus
 */
public class RxBus {
    private Relay<Object> mBus;

    private RxBus() {
        mBus = PublishRelay.create().toSerialized();
    }

    private static class SingletonHolder {
        private static final RxBus INSTANCE = new RxBus();
    }

    public static RxBus getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private Relay<Object> getBus() {
        return mBus;
    }

    public static boolean hasObservers() {
        return getInstance().getBus().hasObservers();
    }

    public static void send(Object obj) {
        if (hasObservers()) {
            getInstance().getBus().accept(obj);
        }
    }

    public static Observable<Object> toObservable() {
        return getInstance().getBus();
    }

    public static <T> Observable<T> toObservable(Class<T> clazz) {
        return toObservable().ofType(clazz).observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Observable<T> toObservableAndBindToLifecycle(Class<T> clazz, LifecycleProvider lifecycleProvider) {
        return toObservable(clazz).compose(lifecycleProvider.bindToLifecycle());
    }

    public static <T> Observable<T> toObservableAndBindUntilStop(Class<T> clazz, LifecycleProvider lifecycleProvider) {
        Object event = lifecycleProvider instanceof RxAppCompatActivity ? ActivityEvent.STOP : FragmentEvent.STOP;
        return toObservable(clazz).compose(lifecycleProvider.bindUntilEvent(event));
    }
}