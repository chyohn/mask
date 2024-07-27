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
 * 对分隔后的字符串数组做脱敏处理
 *
 * @author  qiang.shao
 * @since 1.0.0
 */
public interface IMaskItemHandler<T extends IMaskItemHandler<T>> extends IMaskSeparableHandler<T> {

    /**
     * 设置模糊字符串个数
     *
     * @param maskLength 模糊字符长度，&lt;=0: 与源字符个数一致。大于0：不管源字符串多长，使用固定数量的模糊字符串模糊数据
     * @return this
     */
    T setMaskLength(int maskLength);

    /**
     * 设置模糊字符串，用于替换要脱敏的区域
     *
     * @param maskStr 模糊字符串
     * @return this
     */
    T setMaskStr(String maskStr);

    /**
     * 设置模糊字符串和模糊字符串个数
     *
     * @param maskStr    模糊字符串
     * @param maskLength 模糊字符长度，&lt;=0: 与源字符个数一致。大于0：不管源字符串多长，使用固定数量的模糊字符串模糊数据
     * @return this
     */
    T setMaskStr(String maskStr, int maskLength);
}
