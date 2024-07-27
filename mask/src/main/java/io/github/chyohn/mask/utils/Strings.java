/*
 * Copyright 2012-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.chyohn.mask.utils;

/**
 * @author qiang.shao
 * @since 1.0.0
 */
public abstract class Strings {

    /**
     * 重复显示指定字符串，并指定重复次数
     *
     * @param src   字符串
     * @param count 重复次数
     * @return 重复拼接后的字符串
     */
    public static String repeat(String src, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count should not be negative");
        }
        int len = src.length();
        if (len == 0 || count == 0) {
            return "";
        }
        if (count == 1) {
            return src;
        }

        if (Integer.MAX_VALUE / count < len) {
            throw new OutOfMemoryError("Repeating " + len + " bytes String " + count +
                    " times will produce a String exceeding maximum size.");
        }

        StringBuilder sb = new StringBuilder(len * count);
        for (int i = 0; i < count; i++) {
            sb.append(src);
        }
        return sb.toString();
    }

}
