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

package io.github.chyohn.mask.json;


import io.github.chyohn.mask.IMaskHandler;
import io.github.chyohn.mask.handler.MaskGroupHandler;
import io.github.chyohn.mask.handler.MaskHandlerWithSizeSelector;
import io.github.chyohn.mask.handler.MaskIgnore;
import io.github.chyohn.mask.handler.MaskItemAppendOuter;
import io.github.chyohn.mask.handler.MaskItemInner;
import io.github.chyohn.mask.handler.MaskItemLetter;
import io.github.chyohn.mask.handler.MaskItemNumber;
import io.github.chyohn.mask.handler.MaskItemOuter;

/**
 * {@link IMaskHandler} 实现类的短名称配置枚举，以便在json输出类型时使用短名称描述类名，比如下面的json片段
 * <pre>
 *     {"@class":"MASK_INNER", ....}
 * </pre>
 *
 * 短名称输出有如下好处：
 * <pre>
 *     1. 使json更短
 *     2. json字符串中指定的类型（’@class‘字段值）与{@link IMaskHandler}的实现类解耦，两者之间不再强绑定
 * </pre>
 *
 * 短名称JSON输出详见{@link MaskHandlerJSONParser#toJSON(IMaskHandler)}方法
 *
 * @author  qiang.shao
 * @since 1.0.0
 */
enum MaskHandlerMiniNameEnum {

    MASK_INTERFACE(IMaskHandler.class),
    MASK_GROUP(MaskGroupHandler.class),
    MASK_WITH_SIZE(MaskHandlerWithSizeSelector.class),
    MASK_APPENDER(MaskItemAppendOuter.class),
    MASK_INNER(MaskItemInner.class),
    MASK_OUTER(MaskItemOuter.class),
    MASK_LETTER(MaskItemLetter.class),
    MASK_NUMBER(MaskItemNumber.class),
    MASK_IGNORE(MaskIgnore.class);

    private final Class<? extends IMaskHandler> maskClass;

    MaskHandlerMiniNameEnum(Class<? extends IMaskHandler> maskClass) {
        this.maskClass = maskClass;
    }

    public String getMiniName() {
        return name();
    }

    public String getMaskClassName() {
        return maskClass.getName();
    }

    static MaskHandlerMiniNameEnum getInstance(String miniName) {

        for (MaskHandlerMiniNameEnum value : values()) {
            if (value.getMiniName().equals(miniName)) {
                return value;
            }
        }
        return null;
    }

    static MaskHandlerMiniNameEnum getInstance(Class<?> clazz) {
        if (!IMaskHandler.class.isAssignableFrom(clazz)) {
            return null;
        }
        for (MaskHandlerMiniNameEnum value : values()) {
            if (value.maskClass == clazz) {
                return value;
            }
        }
        return null;
    }

}
