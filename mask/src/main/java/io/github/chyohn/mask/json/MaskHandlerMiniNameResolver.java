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

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.ClassNameIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.github.chyohn.mask.IMaskHandler;

import java.io.IOException;

/**
 * <p>
 * 只对{@link IMaskHandler}及其实现类输出短名称，其它类型都见输出为null。
 * <p>
 * 需要配合{@link MaskHandlerJSONParser.UnMaskHandlerTypeHandler}一起使用，否则会出异常
 *
 * @author qiang.shao
 * @since 1.0.0
 */
class MaskHandlerMiniNameResolver  extends ClassNameIdResolver {

    private final static String NONE_MASK_HANDLER_TYPE_ID = "null";

    public MaskHandlerMiniNameResolver(TypeFactory typeFactory) {
        super(SimpleType.constructUnsafe(Object.class), typeFactory, LaissezFaireSubTypeValidator.instance);
    }

    /**
     * 对象的ID值
     *
     * @param value 需要序列化为JSON的对象
     * @return 如果value是MaskHandler类型，则输出短名称，否则返回null
     */
    @Override
    public String idFromValue(Object value) {
        String id = getTypeId(value.getClass());
        if (id != null) {
            return id;
        }
        if (value instanceof IMaskHandler) {
            throw new IllegalStateException(
                String.format("脱敏处理类[%s]不建议使用类全名称做Config JSON输出，\n请在枚举类[%s]中定义短名称",
                    value.getClass().getName(), MaskHandlerMiniNameEnum.class));
        }
        return NONE_MASK_HANDLER_TYPE_ID;
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        return idFromValue(value);
    }

    /**
     * 根据typeId获取对应的{@link JavaType}对象，如果id为{@link #NONE_MASK_HANDLER_TYPE_ID}将返回null，
     * <p>
     * 对于返回null，后续处理需要结合{@link MaskHandlerJSONParser.UnMaskHandlerTypeHandler}
     *
     * @param context
     * @param id      type id，为{@link #NONE_MASK_HANDLER_TYPE_ID}或者{@link IMaskHandler}及实现类的短名称
     * @return 返回type id对应的JavaType对象
     * @throws IOException
     */
    @Override
    public JavaType typeFromId(DatabindContext context, String id) throws IOException {
        if (id == null || id.equals(NONE_MASK_HANDLER_TYPE_ID)) {
            return null;
        }

        id = resolveTypeId(id);
        return super.typeFromId(context, id);
    }

    private String resolveTypeId(String typeId) {
        int index = typeId.indexOf("[L");
        if (index < 0) {
            // 普通对象
            MaskHandlerMiniNameEnum miniNameEnum = MaskHandlerMiniNameEnum.getInstance(typeId);
            return miniNameEnum == null ? typeId : miniNameEnum.getMaskClassName();
        }
        // 数组处理
        String miniName = typeId.substring(index + 2, typeId.length() - 1);
        MaskHandlerMiniNameEnum miniNameEnum = MaskHandlerMiniNameEnum.getInstance(miniName);
        return miniNameEnum == null ? typeId : typeId.replace(miniName, miniNameEnum.getMaskClassName());
    }

    private String getTypeId(Class<?> clazz) {
        if (!clazz.isArray()) {
            MaskHandlerMiniNameEnum miniNameEnum = MaskHandlerMiniNameEnum.getInstance(clazz);
            return miniNameEnum == null ? null : miniNameEnum.getMiniName();
        }
        // 数组处理
        String arrayName = clazz.getName();
        while (clazz.isArray()) {
            clazz = clazz.getComponentType();
        }
        MaskHandlerMiniNameEnum miniNameEnum = MaskHandlerMiniNameEnum.getInstance(clazz);
        return miniNameEnum == null ? null : arrayName.replace(clazz.getName(), miniNameEnum.getMiniName());
    }

    @Override
    public Id getMechanism() {
        return super.getMechanism();
    }
}
