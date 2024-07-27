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

package io.github.chyohn.mask.demo;

import io.github.chyohn.mask.IMaskHandler;
import io.github.chyohn.mask.MaskHandlerFactory;

public class MaskTest {

    public static void main(String[] args) {

        IMaskHandler handler = MaskHandlerFactory.reserveOuter(3, 4);
        String masked = handler.handle("13755556666");
        // 137****6666
        System.out.println(masked);

        // 邮箱脱敏
        // 1. @左边部分只保留前1个字符，@右边部分全部保留
        handler = MaskHandlerFactory.group("@")
                .addHandler(MaskHandlerFactory.reserveLeft(1)); // @左边部分只保留前1个字符
        System.out.println(handler.handle("abcdef@sina.com")); // a*****@sina.com

        handler = MaskHandlerFactory.maskNumber();
        System.out.println(handler.handle("他的年龄35岁")); // 他的年龄**岁

        handler = MaskHandlerFactory.maskLetter();
        System.out.println(handler.handle("35到45岁是人生的golden年龄")); // 35到45岁是人生的******年龄

        name();
        email();
    }

    static void name() {

        // 保留用户姓氏
        IMaskHandler handler = MaskHandlerFactory.sizeSelector()
                .addHandler(MaskHandlerFactory.reserveLeft(1), 2, 3) // 名字长度为2和3
                .addHandler(MaskHandlerFactory.reserveLeft(2), 4); // 名字长度为4以上的
        System.out.println(handler.handle("李白")); // 李*
        System.out.println(handler.handle("王昌龄")); // 王**
        System.out.println(handler.handle("司马相如")); // 司马**
        System.out.println(handler.handle("司马相如弟弟")); // 司马****
    }


    static void email() {

        // 1. @左边部分只保留前1个字符且脱敏字符固定长度为3，@右边部分全部也只保留.后面的内容，且脱敏部分固定为1
        // @左边部分只保留前1个字符且只展示3个脱敏字符
        IMaskHandler leftHandler = MaskHandlerFactory.reserveLeft(1).setMaskLength(3);
        // @右边部分处理器
        IMaskHandler rightHandler = MaskHandlerFactory.group(".") // 以.做分割
                .addHandler(MaskHandlerFactory.maskAll().setMaskLength(1)); // .分割后第一部分全脱敏，且只展示1个指定的脱敏字符: ^_^
        IMaskHandler handler = MaskHandlerFactory.group("@") // 以@分割
                .addHandler(leftHandler, rightHandler); // 第一部分使用左边左处理器，第二部分使用右处理器
        System.out.println(handler.handle("abcdefddddddd@sina.com")); // a***@^_^.com


        // 保留左边1位字符
        IMaskHandler left = MaskHandlerFactory.reserveLeft(1);
        // @右边部分.分割后第一部分全脱敏，且只展示1个指定的脱敏字符: ^_^
        IMaskHandler right = MaskHandlerFactory.maskLeft(1).setSeparator(".").setMaskStr("^_^");
        // 使用‘@’分隔后的字符串数组，第一个字符串用left脱敏，第二个字符串用right脱敏
        IMaskHandler group = MaskHandlerFactory.group("@").addHandler(left, right);
        System.out.println(group.handle("abcdefddddddd@sina.com")); // a***@^_^.com

        String json = group.toConfig();
        handler = MaskHandlerFactory.fromConfig(json);
        System.out.println(handler.handle("abcdefddddddd@sina.com")); // a***@^_^.com
    }

}
