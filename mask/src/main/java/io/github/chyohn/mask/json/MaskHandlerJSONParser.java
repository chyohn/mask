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

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTypeResolverBuilder;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.github.chyohn.mask.IMaskHandler;

import java.io.IOException;

/**
 * 实现在{@link IMaskHandler}对象与JSON字符串之间相互转换
 *
 * @author  qiang.shao
 * @since 1.0.0
 */
public class MaskHandlerJSONParser {

    private static ObjectMapper OBJ_MAPPER;

    static {
        ObjectMapper objectMapper = (new ObjectMapper())
            .setSerializationInclusion(Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .setVisibility(PropertyAccessor.ALL, Visibility.ANY)
            .registerModule(new Jdk8Module());
        setObjectMapper(objectMapper);
    }

    /**
     * {@link IMaskHandler} 转为JSON
     *
     * @param obj handler
     * @return json result string
     */
    public static String toJSON(IMaskHandler obj) {
        try {
            if (obj == null) {
                return null;
            }
            return OBJ_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new JacksonIOException(e);
        }
    }

    /**
     * 把JSON转为{@link IMaskHandler} 对象
     *
     * @param json the json of handler
     * @return handler
     */
    public static IMaskHandler fromJSON(String json) {
        try {
            return OBJ_MAPPER.readValue(json, IMaskHandler.class);
        } catch (IOException e) {
            throw new JacksonIOException(e);
        }
    }

    /**
     * 设置ObjectMapper
     *
     * @param objectMapper jackson mapper
     */
    public static void setObjectMapper(ObjectMapper objectMapper) {
        // 初始化脱敏MaskHandler相关的json配置
        TypeResolverBuilder<?> builder = DefaultTypeResolverBuilder
            .construct(DefaultTyping.NON_FINAL, LaissezFaireSubTypeValidator.instance);
        builder = builder.inclusion(JsonTypeInfo.As.PROPERTY);
        // 配置脱敏处理器短名称生成器
        builder = builder.init(JsonTypeInfo.Id.CLASS, new MaskHandlerMiniNameResolver(objectMapper.getTypeFactory()));
        objectMapper.setDefaultTyping(builder);
        // 配置非脱敏处理器类型JavaType处理器
        objectMapper.addHandler(new UnMaskHandlerTypeHandler());

        OBJ_MAPPER = objectMapper;
    }

    /**
     * 对于非{@link IMaskHandler}的类型，JavaType统一返回它对应的baseType
     */
    static class UnMaskHandlerTypeHandler extends DeserializationProblemHandler {

        @Override
        public JavaType handleUnknownTypeId(DeserializationContext ctxt, JavaType baseType, String subTypeId,
            TypeIdResolver idResolver, String failureMsg) throws IOException {
            return baseType;
        }
    }

}
