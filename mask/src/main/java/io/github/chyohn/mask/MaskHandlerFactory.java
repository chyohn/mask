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

import io.github.chyohn.mask.json.MaskHandlerJSONParser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import io.github.chyohn.mask.handler.AbstractMaskItemHandler;
import io.github.chyohn.mask.handler.AbstractMaskSeparableHandler;
import io.github.chyohn.mask.handler.MaskGroupHandler;
import io.github.chyohn.mask.handler.MaskHandlerWithSizeSelector;
import io.github.chyohn.mask.handler.MaskIgnore;
import io.github.chyohn.mask.handler.MaskItemAppendOuter;
import io.github.chyohn.mask.handler.MaskItemInner;
import io.github.chyohn.mask.handler.MaskItemLetter;
import io.github.chyohn.mask.handler.MaskItemNumber;
import io.github.chyohn.mask.handler.MaskItemOuter;

/**
 * 提供创建{@link IMaskHandler}脱敏接口对象的工厂方法
 * <p>
 * 使用方法：
 * <pre>
 * 一、使用该类提供的工厂方法创建脱敏处理器
 * 核心方法为 {@link #maskInner(int, int)} 和 {@link #maskOuter(int, int)}。所有工厂方法都在这两个方法上扩展
 *
 * 根据脱敏的位置有以下工厂方法：
 * 1. 脱敏（mask）指定位置的字符串：有5个方法（左边、中间、右边、两边固定长度、两边可控长度）
 * 2. 保留（reserve）指定位置的字符串：也有5个方法（左边、中间、右边、两边固定长度、两边可控长度）
 * 3. 拼接（append）脱敏字符串到指定字符串位置：有4个方法（左边、右边、两边固定脱敏字符串长度、两边可控脱敏字符串长度长度）
 * 4. 只脱敏字符串中的数字：{@link #maskNumber()}和{@link #maskNumber(String)}
 * 5. 只脱敏字符串中的字母：{@link #maskLetter()}和{@link #maskLetter(String)}
 * 6. 对字符串不脱敏：{@link #ignore()}
 * 7. 对字符串全部脱敏：输出与原字符串长度相同{@link #maskAll()}和输出固定长度的脱敏字符串{@link #maskAll(int)}
 * 8. 对字符串全部隐藏：{@link #hideAll()}
 *
 * 二、脱敏处理器组合：
 * 1. 分组脱敏，即对一个字符串不同部分使用不同的脱敏方式，工厂方法有{@link #group(String)}和{@link #group(String, int)}
 * 2. 根据长度选择脱敏处理器，工厂方法{@link #sizeSelector()} {@link #sizeSelector(String)} 和 {@link #sizeSelector(String, int)}
 *
 * 三、配置：
 * 1. 使用分隔符对不同部分做脱敏处理见 {@link AbstractMaskSeparableHandler}。如果不指定分隔符，则对整个字符串使用MaskHandler做脱敏处理
 * 2. 更改脱敏替换符和替换符长度见{@link AbstractMaskItemHandler}，
 *      替换符默认为<code>*</code>；
 *      长度默认为-1，即等于被替换字符串长度
 *
 * 四、示例：
 * 对邮箱做脱敏，需求如下：
 * 1. ‘@’左边的字符串只保留1个字符，使用默认替换符，输出替换字符串长度与被脱敏的字符串长度一致
 * 2. ‘@’右边的字符串只保留域名'.'后面的字符串，使用"^_^"做替换符，长度为1（即不管被脱敏的字符串有多长，只使用一个替换符）
 * 代码如下：
 * {@code
 * IMaskHandler left = MaskHandlerFactory.reserveLeft(1);  // 保留左边1位字符
 * IMaskHandler right = MaskHandlerFactory.reserveRight(1).setSeparator(".").setMaskStr("^_^", 1); // 保留‘.’右边最后一个字符串
 * IMaskHandler group = MaskHandlerFactory.group("@").addHandler(left, right); // 使用‘@’分隔后的字符串数组，第一个字符串用left脱敏，第二个字符串用right脱敏
 * String masked = group.handle("qiang.shao@cc.com"); // 输出：q*********@^_^.com
 *
 * // String config = JSONUtil.toJSONStringGeneric(group); // 可以输出邮箱脱敏配置
 * // IMaskHandler handler = JSONUtil.parseObjectGeneric(config, MaskHandler.class); // 另外可以把字符串配置转化为MaskHandler
 * }
 * </pre>
 *
 * @author qiang.shao
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class MaskHandlerFactory {

    /**
     * 使用json创建Handler
     *
     * @param configJson 由{@link IMaskHandler#toConfig()}生成的JSON串, 或json中需要带上具体的java类型
     * @return 由config JSON生成的脱敏对象
     */
    public static IMaskHandler fromConfig(String configJson) {
        return MaskHandlerJSONParser.fromJSON(configJson);
    }


    /**
     * 组合多个处理器对数据的不同部分进行脱敏
     *
     * @param separator 分隔字符串
     * @return 返回组合器
     */
    public static IMaskGroupHandler<?> group(String separator) {
        return new MaskGroupHandler(separator);
    }

    /**
     * 组合多个处理器对数据的不同部分进行脱敏
     *
     * @param separator 分隔字符串
     * @param limit     限制分隔后的字符串最大数量
     * @return 返回组合器
     */
    public static IMaskGroupHandler<?> group(String separator, int limit) {
        return group(separator).setSeparateLimit(limit);
    }

    /**
     * 根据数据长度选择对应的脱敏处理器
     *
     * @return 选择器
     */
    public static IMaskHandlerWithSizeSelector<?> sizeSelector() {
        return new MaskHandlerWithSizeSelector();
    }

    /**
     * 根据数据长度选择对应的脱敏处理器
     *
     * @param separator 分隔字符串
     * @return 选择器
     */
    public static IMaskHandlerWithSizeSelector<?> sizeSelector(String separator) {
        return sizeSelector().setSeparator(separator);
    }

    /**
     * 根据数据长度选择对应的脱敏处理器
     *
     * @param separator 分隔字符串
     * @param limit     限制分隔后的字符串最大数量
     * @return 选择器
     */
    public static IMaskHandlerWithSizeSelector<?> sizeSelector(String separator, int limit) {
        return sizeSelector().setSeparator(separator, limit);
    }

    /**
     * 示例：
     * <pre>
     *     ---------------------------------
     *     |  字符串    |       脱敏输出     |
     *     ---------------------------------
     *     |    abcd   |     abcd          |
     *     ---------------------------------
     * </pre>
     *
     * @return 返回一个处理器，不需要做任何脱敏处理
     */
    public static IMaskHandler ignore() {
        return new MaskIgnore();
    }

    /**
     * 示例：
     * <pre>
     *     ---------------------------------
     *     |  字符串    |       脱敏输出     |
     *     ---------------------------------
     *     |    abcd   |     ****          |
     *     ---------------------------------
     * </pre>
     *
     * @return 创建一个对所有字符做脱敏的处理器
     */
    public static IMaskItemHandler<?> maskAll() {
        return maskInner(0, 0);
    }

    /**
     * 示例：
     * <pre>
     *     --------------------------------------------
     *     | maskSize |  字符串    |       脱敏输出     |
     *     --------------------------------------------
     *     | 1        |    abcd   |     *             |
     *     --------------------------------------------
     *     |          |    a      |     **            |
     *     | 2        |--------------------------------
     *     |          |    abcd   |     **            |
     *     --------------------------------------------
     * </pre>
     *
     * @param maskSize 指定脱敏字符长度
     * @return 创建一个对所有字符做脱敏的处理器
     */
    public static IMaskItemHandler<?> maskAll(int maskSize) {
        return maskAll().setMaskLength(maskSize);
    }

    /**
     * 隐藏所有数据
     * <p>
     * 示例：
     * <pre>
     *     ---------------------------------
     *     |  字符串    |       脱敏输出     |
     *     ---------------------------------
     *     |    a      |                   |
     *     ---------------------------------
     *     |    abcd   |                   |
     *     ---------------------------------
     * </pre>
     *
     * @return 返回一个脱敏处理器
     */
    public static IMaskItemHandler<?> hideAll() {
        return maskAll(1).setMaskStr("");
    }

    /**
     * 模糊中间
     * <p>
     * 示例：
     * <pre>
     *     --------------------------------------------------------
     *     | leftSize | rightSize |  字符串    |       脱敏输出     |
     *     --------------------------------------------------------
     *     |          |           |    abcd   |        a*cd       |
     *     |    1     |     2     |--------------------------------
     *     |          |           |    abc   |         ***        |
     *     --------------------------------------------------------
     * </pre>
     *
     * @param leftSize  左保留长度
     * @param rightSize 右保留长度
     * @return 返回一个脱敏处理器
     */
    public static IMaskItemHandler<?> maskInner(int leftSize, int rightSize) {
        return new MaskItemInner(leftSize, rightSize);
    }

    /**
     * 模糊两边
     * <p>
     * 示例：
     * <pre>
     *     --------------------------------------------------------
     *     | leftSize | rightSize |  字符串    |       脱敏输出     |
     *     --------------------------------------------------------
     *     |          |           |    abcd   |        *b**       |
     *     |    1     |     2     |--------------------------------
     *     |          |           |    abc    |        ***        |
     *     --------------------------------------------------------
     * </pre>
     *
     * @param leftSize  左模糊长度
     * @param rightSize 右模糊长度
     * @return 返回一个脱敏处理器
     */
    public static IMaskItemHandler<?> maskOuter(int leftSize, int rightSize) {
        return new MaskItemOuter(leftSize, rightSize);
    }

    /**
     * 模糊两边
     * <p>
     * 示例：
     * <pre>
     *     --------------------------------------------
     *     |   size   |  字符串    |       脱敏输出     |
     *     --------------------------------------------
     *     | 1        |    abcd   |      *bc*         |
     *     --------------------------------------------
     *     |          |    abc    |      ***          |
     *     | 2        |--------------------------------
     *     |          |  abcdef   |      **cd**       |
     *     --------------------------------------------
     * </pre>
     *
     * @param size 左右各模糊长度
     * @return 返回一个脱敏处理器
     */
    public static IMaskItemHandler<?> maskMargin(int size) {
        return maskOuter(size, size);
    }

    /**
     * 模糊左部分字符串
     * <p>
     * 示例：
     * <pre>
     *     --------------------------------------------
     *     |   size   |  字符串    |       脱敏输出     |
     *     --------------------------------------------
     *     | 1        |    abcd   |      *bcd         |
     *     --------------------------------------------
     *     |          |    ab     |      **           |
     *     | 2        |--------------------------------
     *     |          |  abcdef   |      **cdef       |
     *     --------------------------------------------
     * </pre>
     *
     * @param size 模糊长度
     * @return 返回一个脱敏处理器
     */
    public static IMaskItemHandler<?> maskLeft(int size) {
        return maskOuter(size, 0);
    }

    /**
     * 模糊右部分字符
     * <p>
     * 示例：
     * <pre>
     *     --------------------------------------------
     *     |   size   |  字符串    |       脱敏输出     |
     *     --------------------------------------------
     *     | 1        |    abcd   |      abc*         |
     *     --------------------------------------------
     *     |          |    ab     |       **          |
     *     | 2        |--------------------------------
     *     |          |  abcdef   |      abcd**       |
     *     --------------------------------------------
     * </pre>
     *
     * @param size 右模糊长度
     * @return 返回一个脱敏处理器
     */
    public static IMaskItemHandler<?> maskRight(int size) {
        return maskOuter(0, size);
    }

    /**
     * 左右拼接模糊字符串
     * <p>
     * 示例：
     * <pre>
     *     --------------------------------------------------------
     *     | leftSize | rightSize |  字符串    |       脱敏输出     |
     *     --------------------------------------------------------
     *     |          |           |     b     |        *b**       |
     *     |    1     |     2     |--------------------------------
     *     |          |           |    abc   |        *abc**      |
     *     --------------------------------------------------------
     * </pre>
     *
     * @param leftSize  左边拼接模糊字符串长度
     * @param rightSize 右边拼接模糊字符串长度
     * @return 返回一个脱敏处理器
     */
    public static IMaskItemHandler<?> maskAppend(int leftSize, int rightSize) {
        return new MaskItemAppendOuter(leftSize, rightSize);
    }

    /**
     * 左右拼接模糊字符串
     * <p>
     * 示例：
     * <pre>
     *     --------------------------------------------
     *     |   size   |  字符串    |       脱敏输出     |
     *     --------------------------------------------
     *     | 1        |    b      |       *b*         |
     *     --------------------------------------------
     *     |          |    ab     |     **ab**        |
     *     | 2        |--------------------------------
     *     |          |    b      |      **b**        |
     *     --------------------------------------------
     * </pre>
     *
     * @param size 左右拼接模糊字符串长度
     * @return 返回一个脱敏处理器
     */
    public static IMaskItemHandler<?> maskMarginAppend(int size) {
        return maskAppend(size, size);
    }

    /**
     * 左拼接模糊字符串
     * <p>
     * 示例：
     * <pre>
     *     --------------------------------------------
     *     |   size   |  字符串    |       脱敏输出     |
     *     --------------------------------------------
     *     | 1        |    b      |       *b          |
     *     --------------------------------------------
     *     |          |    ab     |     **ab          |
     *     | 2        |--------------------------------
     *     |          |    b      |      **b          |
     *     --------------------------------------------
     * </pre>
     *
     * @param size 左拼接模糊字符串长度
     * @return 返回一个脱敏处理器
     */
    public static IMaskItemHandler<?> maskLeftAppend(int size) {
        return maskAppend(size, 0);
    }

    /**
     * 右拼接模糊字符串
     * <p>
     * 示例：
     * <pre>
     *     --------------------------------------------
     *     |   size   |  字符串    |       脱敏输出     |
     *     --------------------------------------------
     *     | 1        |    b      |        b*         |
     *     --------------------------------------------
     *     |          |    ab     |       ab**        |
     *     | 2        |--------------------------------
     *     |          |    b      |        b**        |
     *     --------------------------------------------
     * </pre>
     *
     * @param size 右拼接模糊字符串长度
     * @return 返回一个脱敏处理器
     */
    public static IMaskItemHandler<?> maskRightAppend(int size) {
        return maskAppend(0, size);
    }

    /**
     * 保留两边
     * <p>
     * 示例：
     * <pre>
     *     --------------------------------------------------------
     *     | leftSize | rightSize |  字符串    |       脱敏输出     |
     *     --------------------------------------------------------
     *     |          |           |    ab     |       **          |
     *     |          |           |--------------------------------
     *     |    1     |     2     |    abcd   |       a*cd        |
     *     |          |           |--------------------------------
     *     |          |           |    abcde  |       a**de       |
     *     --------------------------------------------------------
     * </pre>
     *
     * @param leftSize  左保留长度
     * @param rightSize 右保留长度
     * @return 返回一个脱敏处理器
     */
    public static IMaskItemHandler<?> reserveOuter(int leftSize, int rightSize) {
        return maskInner(leftSize, rightSize);
    }

    /**
     * 保留中间
     * <p>
     * 示例：
     * <pre>
     *     --------------------------------------------------------
     *     | leftSize | rightSize |  字符串    |       脱敏输出     |
     *     --------------------------------------------------------
     *     |          |           |    ab     |        **         |
     *     |          |           |--------------------------------
     *     |    1     |     2     |    abc    |         ***       |
     *     |          |           |--------------------------------
     *     |          |           |    abcd   |         *b**      |
     *     --------------------------------------------------------
     * </pre>
     *
     * @param leftSize  左模糊长度
     * @param rightSize 右模糊长度
     * @return 返回一个脱敏处理器
     */
    public static IMaskItemHandler<?> reserveInner(int leftSize, int rightSize) {
        return maskOuter(leftSize, rightSize);
    }

    /**
     * 保留两边相同长度
     * <p>
     * 示例：
     * <pre>
     *     --------------------------------------------
     *     |   size   |  字符串    |       脱敏输出     |
     *     --------------------------------------------
     *     | 1        |    abcd   |      a**d         |
     *     --------------------------------------------
     *     |          |    ab     |       **          |
     *     | 2        |--------------------------------
     *     |          |  abcdef   |      ab**ef       |
     *     --------------------------------------------
     * </pre>
     *
     * @param size 左右各保留长度
     * @return 返回一个脱敏处理器
     */
    public static IMaskItemHandler<?> reserveMargin(int size) {
        return reserveOuter(size, size);
    }

    /**
     * 保留左部分字符串
     * <p>
     * 示例：
     * <pre>
     *     --------------------------------------------
     *     |   size   |  字符串    |       脱敏输出     |
     *     --------------------------------------------
     *     | 1        |    abcd   |      a***         |
     *     --------------------------------------------
     *     |          |    ab     |       **          |
     *     | 2        |--------------------------------
     *     |          |  abcdef   |      ab****       |
     *     --------------------------------------------
     * </pre>
     *
     * @param size 保留长度
     * @return 返回一个脱敏处理器
     */
    public static IMaskItemHandler<?> reserveLeft(int size) {
        return reserveOuter(size, 0);
    }

    /**
     * 保留右部分字符串
     * <p>
     * 示例：
     * <pre>
     *     --------------------------------------------
     *     |   size   |  字符串    |       脱敏输出     |
     *     --------------------------------------------
     *     | 1        |    abcd   |      ***d         |
     *     --------------------------------------------
     *     |          |    ab     |       **          |
     *     | 2        |--------------------------------
     *     |          |  abcdef   |      ****ef       |
     *     --------------------------------------------
     * </pre>
     *
     * @param size 保留长度
     * @return 返回一个脱敏处理器
     */
    public static IMaskItemHandler<?> reserveRight(int size) {
        return reserveOuter(0, size);
    }

    /**
     * 只对字符串中的数字脱敏
     * <p>
     * 示例：
     * <pre>
     *     ---------------------------------
     *     |  字符串    |       脱敏输出     |
     *     ---------------------------------
     *     |  a        |         a         |
     *     ---------------------------------
     *     |  a123     |         a***      |
     *     ---------------------------------
     *     |  a123b1   |         a***b*    |
     *     ---------------------------------
     * </pre>
     *
     * @return 返回一个脱敏处理器
     */
    public static IMaskItemHandler<?> maskNumber() {
        return new MaskItemNumber();
    }

    /**
     * 只对字符串中的数字脱敏
     * <p>
     * 示例：
     * <pre>
     *     --------------------------------------------
     *     | maskStr  |  字符串    |       脱敏输出     |
     *     --------------------------------------------
     *     | -        |    a123   |      a---         |
     *     --------------------------------------------
     *     |          |    ab     |       ab          |
     *     | X        |--------------------------------
     *     |          |  1-bcd-23 |      X-bcd-XX     |
     *     --------------------------------------------
     * </pre>
     *
     * @param maskStr 使用替换字符替换字符串中的数字
     * @return 返回一个脱敏处理器
     */
    public static IMaskItemHandler<?> maskNumber(String maskStr) {
        return maskNumber().setMaskStr(maskStr);
    }

    /**
     * 只对字符串中的字母脱敏
     * <p>
     * 示例：
     * <pre>
     *     ---------------------------------
     *     |  字符串    |       脱敏输出     |
     *     ---------------------------------
     *     |  123      |         123       |
     *     ---------------------------------
     *     |  a123     |         *123      |
     *     ---------------------------------
     *     |  a123b1   |         *123*1    |
     *     ---------------------------------
     * </pre>
     *
     * @return 返回一个脱敏处理器
     */
    public static IMaskItemHandler<?> maskLetter() {
        return new MaskItemLetter();
    }

    /**
     * 只对字符串中的字母脱敏
     * <p>
     * 示例：
     * <pre>
     *     --------------------------------------------
     *     | maskStr  |  字符串    |       脱敏输出     |
     *     --------------------------------------------
     *     | -        |    1abc   |      1---         |
     *     --------------------------------------------
     *     |          |    12     |       12          |
     *     | X        |--------------------------------
     *     |          |  1-bcd-23 |      1-XXX-23     |
     *     --------------------------------------------
     * </pre>
     *
     * @param maskStr 使用替换字符替换字符串中的字母
     * @return 返回一个脱敏处理器
     */
    public static IMaskItemHandler<?> maskLetter(String maskStr) {
        return maskLetter().setMaskStr(maskStr);
    }

}
