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

import android.text.TextUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 作者:林国定 邮件:lingguoding@gmail.com
 * 创建时间:16/1/15 上午1:36
 * 描述:
 */
public class Md5Util {
    private Md5Util() {
    }

    public static String md5(String... strs) {
        if (strs == null || strs.length == 0) {
            throw new RuntimeException("请输入需要加密的字符串!");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            boolean isNeedThrowNotNullException = true;
            for (String str : strs) {
                if (!TextUtils.isEmpty(str)) {
                    isNeedThrowNotNullException = false;
                    md.update(str.getBytes());
                }
            }
            if (isNeedThrowNotNullException) {
                throw new RuntimeException("请输入需要加密的字符串!");
            }
            return new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}