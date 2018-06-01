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

package com.hao.base.net;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.orhanobut.logger.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * 作者:林国定 邮件:lingguoding@gmail.com
 * 创建时间:2017/11/21
 * 描述:
 */
final class NetResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    NetResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            return handleNetResult(value.string());
        } finally {
            value.close();
        }
    }

    private T handleNetResult(String result) throws IOException {
        Logger.d(result);
        NetResult netResult;
        try {
            netResult = gson.fromJson(result, NetResult.class);
        } catch (JsonSyntaxException e) {
            if (e.getMessage().contains("Expected BEGIN_OBJECT but was STRING")) {
                // 返回的数据本来就是字符串，直接返回
                return (T) result;
            } else {
                throw e;
            }
        }

        try {
            if (netResult.code == 0) {
                return adapter.read(gson.newJsonReader(new InputStreamReader(new ByteArrayInputStream(gson.toJson(netResult.data).getBytes()))));
            } else {
                throw new ApiException(netResult.msg, netResult.code);
            }
        } catch (IOException e) {
            throw e;
        }
    }

    public static class NetResult {
        public int code;
        public String msg;
        public Object data;
    }
}