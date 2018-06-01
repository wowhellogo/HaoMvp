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

import java.util.Collection;

/**
 * 作者:林国定 邮件:lingguoding@gmail.com
 * 创建时间:2017/11/19
 * 描述:
 */
public class CollectionUtil {
    private CollectionUtil() {
    }

    /**
     * 列表是否为空
     *
     * @param args 多参数列表
     * @return 是否为空
     */
    public static boolean isEmpty(Collection collection, Collection... args) {
        if (collection == null || collection.isEmpty()) {
            return true;
        }
        for (Collection arg : args) {
            if (arg == null || arg.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 列表是否不为空
     *
     * @param args 多参数列表
     * @return 是否不为空
     */
    public static boolean isNotEmpty(Collection collection, Collection... args) {
        return !isEmpty(collection, args);
    }
}
