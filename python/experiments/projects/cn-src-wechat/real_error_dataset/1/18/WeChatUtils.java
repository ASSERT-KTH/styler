/*
 * Copyright (c) 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.javaer.wechat.sdk.util;

import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * 微信工具类.
 *
 * @author zhangpeng
 */
public class WeChatUtils {
    private WeChatUtils() {}

    /**
     * 生成长度为 32 的 uuid.
     *
     * @return uuid32 String
     */
    @NotNull
    public static String uuid32() {
        final StringBuilder sb = new StringBuilder(UUID.randomUUID().toString());
        sb.deleteCharAt(8);
        sb.deleteCharAt(12);
        sb.deleteCharAt(16);
        sb.deleteCharAt(20);
        return sb.toString();
    }

    /**
     * 拼接多个 path.
     *
     * @param firstPath firstPath
     * @param secondPath secondPath
     *
     * @return 拼接后的 path.
     */
    public static String joinPath(final String firstPath, final String secondPath) {
        Validate.notEmpty(firstPath);
        Validate.notEmpty(secondPath);

        final String tmp1 = firstPath.endsWith("/") ? firstPath.substring(0, firstPath.length() - 1) : firstPath;
        final String tmp2 = secondPath.startsWith("/") ? (tmp1 + secondPath) : (tmp1 + "/" + secondPath);
        return tmp2.endsWith("/") ? tmp2.substring(0, tmp2.length() - 1) : tmp2;
    }

    /**
     * url 编码.
     *
     * @param urlStr 原始 url
     *
     * @return 编码后的 url
     */
    public static String urlEncode(final String urlStr) {
        try {
            return URLEncoder.encode(urlStr, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
