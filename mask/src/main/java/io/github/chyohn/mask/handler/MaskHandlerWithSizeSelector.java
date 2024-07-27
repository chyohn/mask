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

import io.github.chyohn.mask.IMaskHandler;
import io.github.chyohn.mask.IMaskHandlerWithSizeSelector;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author  qiang.shao
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class MaskHandlerWithSizeSelector extends AbstractMaskSeparableHandler<MaskHandlerWithSizeSelector> implements
        IMaskHandlerWithSizeSelector<MaskHandlerWithSizeSelector> {


    /**
     * <pre>
     * 1. 字符串分隔后，数组长度不定，而不同长度的数组脱敏方式可能不一样，因此使用一个map存储不同数组长度的处理器.
     * 2. key: 规格，value：处理器。
     * </pre>
     */
    private Map<Integer, IMaskHandler> handlerOfSize = new HashMap<>();
    private IMaskHandler defaultHandler;

    public MaskHandlerWithSizeSelector(String separator) {
        this.setSeparator(separator);
    }


    @Override
    public MaskHandlerWithSizeSelector addHandler(IMaskHandler handler, int... size) {
        for (int i : size) {
            handlerOfSize.put(i, handler);
        }
        return this;
    }

    @Override
    public MaskHandlerWithSizeSelector setDefaultHandler(IMaskHandler defaultHandler) {
        this.defaultHandler = defaultHandler;
        return this;
    }

    /**
     * <pre>
     * 获取能够匹配size个数的处理器。比如组合中有2组处理器列表，如下：
     * 3 有处理器【h1】
     * 5 有处理器【h2】
     * 如果size = 6，则返回第2组处理器列表: 5【h2】
     * 如果size = 3，则返回第1组处理器类别：3【h1】
     * 如果size = 1，则返回defaultHandler
     * </pre>
     *
     * @param size 长度
     * @return 返回匹配长度的处理器，如果没有与之匹配的自返回默认脱敏处理器
     */
    private IMaskHandler getMatchHandler(int size) {
        Integer[] sizes = handlerOfSize.keySet().stream().sorted().toArray(Integer[]::new);

        for (int i = sizes.length - 1; i >= 0; i--) {
            if (sizes[i] <= size) {
                return handlerOfSize.get(sizes[i]);
            }
        }
        return defaultHandler;
    }


    @Override
    protected String doHandle(String src) {
        IMaskHandler handler = getMatchHandler(src.length());
        return handler == null ? src : handler.handle(src);
    }

    @Override
    protected String[] doHandle(String originalSrc, String[] separatedStrs) {
        IMaskHandler handler = getMatchHandler(separatedStrs.length);
        if (handler == null) {
            return separatedStrs;
        }
        return new String[]{handler.handle(originalSrc)};
    }

}
