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

import io.github.chyohn.mask.IMaskSeparableHandler;
import lombok.Getter;

/**
 * 带分隔符的脱敏处理器
 *
 * @author  qiang.shao
 * @since 1.0.0
 */
@Getter
public abstract class AbstractMaskSeparableHandler<T extends AbstractMaskSeparableHandler<T>> implements
    IMaskSeparableHandler<T> {

    private String separator; // token分隔符
    private int separateLimit; // 分隔token最大数量
    private String outputDelimiter; // 输出结果链接字符

    @Override
    public T setSeparator(String separator) {
        this.separator = separator;
        return (T) this;
    }

    @Override
    public T setSeparator(String separator, int limit) {
        this.setSeparator(separator);
        this.setSeparateLimit(limit);
        return (T) this;
    }

    @Override
    public T setSeparateLimit(int separateLimit) {
        this.separateLimit = separateLimit;
        return (T) this;
    }

    @Override
    public T setOutputDelimiter(String outputDelimiter) {
        this.outputDelimiter = outputDelimiter;
        return (T) this;
    }

    @Override
    public final String handle(String src) {
        if (separator == null) {
            return doHandle(src);
        }

        String[] tokens = separate(src);
        String[] maskedStrs = doHandle(src, tokens);
        if (outputDelimiter == null) {
            outputDelimiter = separator;
        }
        return String.join(outputDelimiter, maskedStrs);
    }

    private String[] separate(String src) {
        String regex = this.separator;
        if (regex.length() == 1 && ".$|()[{^?*+\\".indexOf(regex) == 0) {
            regex = new StringBuilder("\\").append(regex).toString();
        }
        if (separateLimit > 0) {
            return src.split(regex, separateLimit);
        }
        return src.split(regex);
    }

    /**
     * 脱敏没有分隔的字符串
     *
     * @param src 源字符串
     * @return 返回脱敏后的字符串
     */
    protected abstract String doHandle(String src);

    /**
     * 对分隔后的字符串数组做脱敏
     *
     * @param originalSrc 分隔前的字符串
     * @param separatedStrs 源字符串分隔后的字符串数组
     * @return 返回脱敏后的字符串数组
     */
    protected abstract String[] doHandle(String originalSrc, String[] separatedStrs);
}
