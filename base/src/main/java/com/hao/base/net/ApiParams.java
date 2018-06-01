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

package com.hao.base.net;

import java.util.HashMap;

/**
 * 作者:林国定 邮件:lingguoding@gmail.com
 * 创建时间:16/1/16 上午12:56
 * 描述:
 */
public class ApiParams extends HashMap<String, Object> {

    public ApiParams() {
    }

    public ApiParams(String key, Object value) {
        put(key, value);
    }

    public ApiParams with(String key, Object value) {
        put(key, value);
        return this;
    }
}