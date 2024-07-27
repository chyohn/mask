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
 * 支持根据数据的长度选择对应的处理器，另见方法说明：{@link #addHandler(IMaskHandler, int...)}
 *
 * @author  qiang.shao
 * @since 1.0.0
 */
public interface IMaskHandlerWithSizeSelector<T extends IMaskHandlerWithSizeSelector<T>> extends
    IMaskSeparableHandler<T> {


    /**
     * <pre>
     * 指定字符串长度或者有分隔符分隔后的字符串数组长度对应的处理器列表，每个数组或字符串根据其长度使用对应一个处理器。如果没有找到对应的，就使用默认处理器
     * 最好与{@link #setDefaultHandler(IMaskHandler)}搭配使用，超出长度的情况下会使用默认处理器
     *
     * 比如组合中有2组处理器列表，如下：
     * 3 有处理器[h1]
     * 5 有处理器[h2]
     * 1. 令分隔后的数组长度为size：
     *      如果size = 6，则使用第2组处理器列表: 5[h2]。因此使用处理器h2处理分隔后的数组
     *      如果size = 5，则使用第2组处理器类别：5[h2]
     *      如果size = 4，则使用第1组处理器类别：3[h1]
     *      如果size = 3，则使用第1组处理器类别：3[h1]
     *      如果size = 1，则使用null，如果defaultHandler!=null，则使用defaultHandler处理这个字符串，否则不做脱敏处理。
     * 2. 如果没有指定分隔符，size为字符串长度，处理方式同上
     * </pre>
     *
     * @param handler 该长度下的处理器，数据长度 &gt;= size可以使用该处理器
     * @param size    字符串长度或者指定分隔符分隔后的字符串数组长度
     * @return this
     */
    T addHandler(IMaskHandler handler, int... size);

    /**
     * 配置默认处理器，如果字符串长度或字符数组长度没有指定处理器，则使用默认处理器。如果默认处理器也没有提供，则不做脱敏处理
     *
     * @param defaultHandler 默认脱敏处理器
     * @return this
     */
    T setDefaultHandler(IMaskHandler defaultHandler);
}
