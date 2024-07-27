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

import io.github.chyohn.mask.IMaskGroupHandler;
import io.github.chyohn.mask.IMaskHandler;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * 脱敏处理方式组合器：可以组合和嵌套多种处理方式
 *
 * @author  qiang.shao
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class MaskGroupHandler extends AbstractMaskSeparableHandler<MaskGroupHandler> implements
        IMaskGroupHandler<MaskGroupHandler> {


    /**
     * <pre>
     * 1. 字符串分隔后，数组长度不定，而不同长度的数组脱敏方式可能不一样，因此使用一个map存储不同数组长度的处理器列表.
     * 2. 每个数组元素小标与对应的处理器数组小标一致。
     * 3. key: 规格，value：处理器。
     * </pre>
     */
    private Map<Integer, IMaskHandler[]> handlersOfSize = new HashMap<>();

    /**
     *
     */
    private IMaskHandler defaultHandler;

    public MaskGroupHandler(String separator) {
        this.setSeparator(separator);
    }

    /**
     * <pre>
     * 添加处理器列表
     * 参见：{@link #addHandler(int, IMaskHandler...)}
     * </pre>
     *
     * @param handlers 某个长度的处理器，数组长队对应脱敏目标的长度
     * @return 返回当前分组脱敏处理器
     */
    @Override
    public MaskGroupHandler addHandler(IMaskHandler... handlers) {
        int size = handlers.length;
        return addHandler(size, handlers);
    }

    /**
     * <pre>
     * 指定分隔后字符串数组长度的处理器列表，每个数组元素对应一个处理器。如果没有找到对应的，就使用默认处理器
     * 最好与{@link #setDefaultHandler(IMaskHandler)}搭配使用，超出长度的情况下会使用默认处理器
     * 见{@link #getMatchHandlers(int)}
     * 见{@link #addHandler(IMaskHandler...)}
     * </pre>
     *
     * @param size     分隔后字符串数组长度
     * @param handlers 该长度下的处理器
     * @return  返回当前分组脱敏处理器
     */
    @Override
    public MaskGroupHandler addHandler(int size, IMaskHandler... handlers) {
        handlersOfSize.put(size, handlers);
        return this;
    }

    /**
     * 配置默认处理器，如果没有指定处理器，则使用默认处理器。如果默认处理器也没有提供，则不做脱敏处理
     *
     * @param defaultHandler 默认的脱敏处理器
     * @return 返回当前分组脱敏处理器
     */
    @Override
    public MaskGroupHandler setDefaultHandler(IMaskHandler defaultHandler) {
        this.defaultHandler = defaultHandler;
        return this;
    }

    /**
     * <pre>
     * 获取能够匹配size个数的处理器列表。比如组合中有2组处理器列表，如下：
     * 3 有处理器[h1,h2,h3]
     * 5 有处理器[h4,h5,h6,h7,h8]
     * 如果size = 6，则返回第2组处理器列表: 5[h4,h5,h6,h7,h8]
     * 如果size = 3，则返回第1组处理器类别：3[h1,h2,h3]
     * 如果size = 1，则返回null
     * </pre>
     *
     * @param size 长度
     * @return  返回匹配长度的处理器列表
     */
    private IMaskHandler[] getMatchHandlers(int size) {
        Integer[] sizes = handlersOfSize.keySet().stream().sorted().toArray(Integer[]::new);

        for (int i = sizes.length - 1; i >= 0; i--) {
            if (sizes[i] <= size) {
                return handlersOfSize.get(sizes[i]);
            }
        }
        return null;
    }


    @Override
    protected String doHandle(String src) {
        IMaskHandler[] handlers = getMatchHandlers(1);
        if (handlers != null && handlers.length > 0) {
            return handlers[0].handle(src);
        }
        return defaultHandler == null ? src : defaultHandler.handle(src);
    }

    @Override
    protected String[] doHandle(String originalSrc, String[] separatedStrs) {
        String[] result = new String[separatedStrs.length];
        IMaskHandler[] handlers = getMatchHandlers(separatedStrs.length);
        for (int i = 0; i < separatedStrs.length; i++) {
            String str = separatedStrs[i];
            if (handlers != null && handlers.length > i && handlers[i] != null) {
                result[i] = handlers[i].handle(str);
                continue;
            }
            result[i] = defaultHandler == null ? str : defaultHandler.handle(str);
        }
        return result;
    }

}
