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

import io.github.chyohn.mask.utils.Strings;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 从指定区间内部做脱敏
 * <p>
 * 注意；保留长度大于等于数据长度时全部脱敏
 *
 * @author qiang.shao
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class MaskItemInner extends AbstractMaskItemHandler<MaskItemInner> {

    private int leftSize;
    private int rightSize;


    public MaskItemInner(int leftSize, int rightSize) {
        this.leftSize = leftSize;
        this.rightSize = rightSize;
    }


    @Override
    protected String doHandle(String src) {
        return maskInner(src, leftSize, rightSize, maskStr, maskLength);
    }

    @Override
    protected String[] doHandle(String originalSrc, String[] separatedStrs) {
        return maskInner(separatedStrs, leftSize, rightSize, maskStr, maskLength);
    }

    /**
     * 模糊中间
     *
     * @param src        被脱敏字符串
     * @param leftSize   左保留长度
     * @param rightSize  右保留长度
     * @param maskStr    模糊字符
     * @param maskLength 模糊字符长度，&lt;=0: 与源字符个数一致。大于0：不管源字符串多长，使用固定数量的模糊字符串模糊数据
     * @return 脱敏结果
     */
    private static String maskInner(String src, int leftSize, int rightSize, String maskStr, int maskLength) {
        if (leftSize < 0 || rightSize < 0) {
            throw new IllegalArgumentException("leftSize和rightSize不能小于0");
        }

        int reverseSize = leftSize + rightSize;
        if (reverseSize >= src.length()) {
            int maskSize = maskLength <= 0 ? src.length() : maskLength;
            return Strings.repeat(maskStr, maskSize);
        }

        // 保留左边
        StringBuilder result = new StringBuilder(src.substring(0, leftSize));
        // 模糊中间
        int maskSize = maskLength <= 0 ? src.length() - reverseSize : maskLength;
        result.append(Strings.repeat(maskStr, maskSize));
        // 保留右边
        result.append(src.substring(src.length() - rightSize));

        return result.toString();
    }

    /**
     * 模糊中间
     *
     * @param src        被脱敏字符数组
     * @param leftSize   左保留长度
     * @param rightSize  右保留长度
     * @param maskStr    模糊字符
     * @param maskLength 模糊字符长度，&lt;=0: 与源字符个数一致。大于0：不管源字符串多长，使用固定数量的模糊字符串模糊数据
     * @return 脱敏后数组
     */
    public static String[] maskInner(String[] src, int leftSize, int rightSize, String maskStr, int maskLength) {
        if (leftSize < 0 || rightSize < 0) {
            throw new IllegalArgumentException("leftSize和rightSize不能小于0");
        }

        int reverseSize = leftSize + rightSize;
        if (reverseSize >= src.length) {
            int maskSize = maskLength <= 0 ? src.length : maskLength;
            String[] result = new String[maskSize];
            for (int i = 0; i < maskSize; i++) {
                result[i] = maskStr;
            }
            return result;
        }

        int maskSize = maskLength <= 0 ? src.length - reverseSize : maskLength;
        int resultSize = maskSize + reverseSize;
        String[] result = new String[resultSize];

        // 保留左边
        System.arraycopy(src, 0, result, 0, leftSize);
        // 模糊中间
        for (int i = 0; i < maskSize; i++) {
            result[leftSize + i] = maskStr;
        }
        // 保留右边
        int rightStartIndex = leftSize + maskSize;
        for (int i = src.length - rightSize; i < src.length; i++) {
            result[rightStartIndex++] = src[i];
        }

        return result;
    }

}
