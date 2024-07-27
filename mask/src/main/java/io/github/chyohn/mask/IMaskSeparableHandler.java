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

package io.github.chyohn.mask;

/**
 * <pre>
 * 支持带分隔符的脱敏处理器。
 * 1. 如果指定了分隔符，则按分隔后的字符串数组的元素为单位进行处理。
 * 2. 如果未指定分隔符，则按整个字符串做处理
 *
 * 比如对字符串”qiang.shao“全部内容脱敏
 * 1. 如果按"."分隔后脱敏，结果为"*.*"
 * 2. 如果不指定分隔符，结果为”**********“
 * </pre>
 *
 * @author qiang.shao
 * @since 1.0.0
 */
public interface IMaskSeparableHandler<T extends IMaskSeparableHandler<T>> extends IMaskHandler {

    /**
     * 设置分隔符，如果分隔符为<code>separator==null || separator=""</code>，则以字符为单位进行处理，否则按整个字符串做处理
     * @param separator 分隔符
     * @return this
     */
    T setSeparator(String separator);

    /**
     *
     * 设置分隔符
     * @param separator 分隔符
     * @param limit 最大长度限制
     * @return this
     */
    T setSeparator(String separator, int limit);

    /**
     * 分隔后的字符串数组最大长度。
     * 1. separateLimit &lt;= 0时，不限制结果数组长度
     * 2. separateLimit &gt;= 1时，结果字符串数组长度不超过separateLimit
     *
     * @param separateLimit 最大长度限制
     * @return this
     */
    T setSeparateLimit(int separateLimit);

    /**
     * 设置输出内容的连接字符串，默认使用separator
     * @param outputDelimiter 输出内容的连接字符串
     * @return this
     */
    T setOutputDelimiter(String outputDelimiter);
}
