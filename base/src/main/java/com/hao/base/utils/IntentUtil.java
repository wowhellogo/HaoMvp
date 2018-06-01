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

import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * 作者:林国定 邮件:lingguoding@gmail.com
 * 创建时间:16/3/27 下午4:22
 * 描述:
 */
public class IntentUtil {

    private IntentUtil() {
    }

    /**
     * 获取安装apk文件的意图
     *
     * @param apkFile apk文件
     * @return
     */
    public Intent getInstallApkIntent(File apkFile) {
        Intent installApkIntent = new Intent();
        installApkIntent.setAction(Intent.ACTION_VIEW);
        installApkIntent.addCategory(Intent.CATEGORY_DEFAULT);
        installApkIntent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        return installApkIntent;
    }

    /**
     * 获取打电话的意图
     *
     * @param phone 电话号码
     * @return
     */
    public Intent getCallUpIntent(String phone) {
        return new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
    }
}
