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
 * 1. 支持根据数据的不同部分采用不同的脱敏方式，即支持组合多个处理器对数据的不同部分进行脱敏，
 * <p>
 * 2. 支持根据数据长度使用相应的处理器列表
 * <p>
 * 另见方法说明：{@link #addHandler(int, IMaskHandler...)}
 *
 * @author  qiang.shao
 * @since 1.0.0
 */
public interface IMaskGroupHandler<T extends IMaskGroupHandler<T>> extends IMaskSeparableHandler<T> {

    /**
     * <pre>
     * 添加处理器列表，每一次添加的handler个数，对应着能处理多长以上的字符串数组。每个handler的位置对应字符串数组中的字符串位置，每个handler只处理对应位置的字符串。
     * 详细说明可参见：{@link #addHandler(int, IMaskHandler...)}
     * </pre>
     *
     * @param handlers 处理器列表，每个处理器只处理字符串数组中对应位置的字符串
     * @return this
     */
    T addHandler(IMaskHandler... handlers);

    /**
     * <pre>
     * 指定分隔后字符串数组长度，以及数组元素对应的处理器。如果在脱敏时没有找到对应的处理器，就使用默认处理器
     * 最好与{@link #setDefaultHandler(IMaskHandler)}搭配使用，超出长度的情况下会使用默认处理器
     *
     * 比如组合中有2组处理器列表，如下：
     * size 3 有处理器[h1,h2,h3]
     * size 5 有处理器[h4,h5,h6,h7,h8]
     * 1. 令分隔后的数组长度为size：
     *      如果size = 6，则使用第2组处理器列表: 5[h4,h5,h6,h7,h8]。数组中第一个字符串用h4处理，第二个用h5……第五个用h8，
     *          第6个用defaultHandler，如果没有配置defaultHandler，则第6个字符串不做脱敏
     *      如果size = 3，则使用第1组处理器类别：3[h1,h2,h3]
     *      如果size = 1，则使用null，如果defaultHandler!=null，则使用defaultHandler处理这个字符串，否则不做脱敏处理。
     * 2. 如果没有指定分隔符，size=1， 处理器选择逻辑同上
     * </pre>
     *
     * @param size     分隔后字符串数组长度
     * @param handlers 该长度下的处理器，数据长度 &gt;= size可以使用该处理器列表
     * @return this
     */
    T addHandler(int size, IMaskHandler... handlers);

    /**
     * 配置默认处理器，如果没有指定处理器，则使用默认处理器。如果默认处理器也没有提供，则不做脱敏处理
     *
     * @param defaultHandler 默认脱敏处理器
     * @return this
     */
    T setDefaultHandler(IMaskHandler defaultHandler);
}
