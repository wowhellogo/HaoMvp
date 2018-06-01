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

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.orhanobut.hawk.Hawk;

/**
 * 作者:林国定 邮件:lingguoding@gmail.com
 * 创建时间:15/9/20 下午11:30
 * 描述:
 */
public class SPUtil {
    private SharedPreferences mSp;

    private SPUtil() {
        mSp = PreferenceManager.getDefaultSharedPreferences(AppManager.getApp());
    }

    private static class SingletonHolder {
        private static final SPUtil INSTANCE = new SPUtil();
    }

    public static SPUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public static void clear() {
        getInstance().mSp.edit().clear().apply();
    }

    public static void putString(String key, String value) {
        getInstance().mSp.edit().putString(key, value).apply();
    }

    public static String getString(String key) {
        return getInstance().mSp.getString(key, "");
    }

    public static void putInt(String key, int value) {
        getInstance().mSp.edit().putInt(key, value).apply();
    }

    public static int getInt(String key) {
        return getInstance().mSp.getInt(key, 0);
    }

    public static void putBoolean(String key, Boolean value) {
        getInstance().mSp.edit().putBoolean(key, value).apply();
    }

    public static void putLong(String key, long value) {
        getInstance().mSp.edit().putLong(key, value).apply();
    }

    public static long getLong(String key) {
        return getInstance().mSp.getLong(key, 0);
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return getInstance().mSp.getBoolean(key, defValue);
    }

    public static void remove(String key) {
        getInstance().mSp.edit().remove(key).apply();
    }

    public static boolean hasKey(String key) {
        return getInstance().mSp.contains(key);
    }

}