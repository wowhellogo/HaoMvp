/**
 * Copyright 2018 bingoogolapple
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

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * 作者:林国定 邮件:lingguoding@gmail.com
 * 创建时间:2017/11/19
 * 描述:
 */
public class GsonUtil {
    private Gson mGson;

    private GsonUtil() {
        mGson = new Gson();
    }

    private static class SingletonHolder {
        private static final GsonUtil INSTANCE = new GsonUtil();
    }

    public static GsonUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public Gson getGson() {
        return mGson;
    }

    public static String toJson(Object src) {
        return getInstance().getGson().toJson(src);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return getInstance().getGson().fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return getInstance().getGson().fromJson(json, typeOfT);
    }
}
