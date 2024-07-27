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

package io.github.chyohn.mask.handler;

import io.github.chyohn.mask.IMaskItemHandler;
import lombok.Getter;

/**
 * 带分隔符的脱敏处理器
 * <p>
 * 支持带分隔符的脱敏处理器。
 * <ol>
 *     <li>如果指定了分隔符，则按分隔后的字符串数组的元素为单位进行脱敏。
 *     <li>如果未指定分隔符，则按字符串中的字符为单位做脱敏。
 * </ol>
 * 比如对字符串”qiang.shao“全部内容脱敏
 * <ol>
 *     <li>如果按"."分隔后脱敏，结果为"*.*"
 *     <li>如果不指定分隔符，结果为”**********“
 * </ol>
 *
 * @author  qiang.shao
 * @since 1.0.0
 */
@Getter
public abstract class AbstractMaskItemHandler<T extends AbstractMaskItemHandler<T>>
    extends AbstractMaskSeparableHandler<T> implements IMaskItemHandler<T> {

    protected String maskStr = "*"; // 脱敏替代字符串
    protected int maskLength = -1; // 脱敏替代字符串长度，-1：根据原始字符串长度进行替换


    @Override
    public T setMaskLength(int maskLength) {
        this.maskLength = maskLength;
        return (T) this;
    }

    @Override
    public T setMaskStr(String maskStr) {
        this.maskStr = maskStr;
        return (T) this;
    }

    @Override
    public T setMaskStr(String maskStr, int maskLength) {
        this.setMaskStr(maskStr);
        this.setMaskLength(maskLength);
        return (T) this;
    }

}
