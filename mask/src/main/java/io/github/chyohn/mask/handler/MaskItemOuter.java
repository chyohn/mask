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
 * 对数据左部分和右边部分做脱敏
 *
 * @author  qiang.shao
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class MaskItemOuter extends AbstractMaskItemHandler<MaskItemOuter> {

    private int leftSize;
    private int rightSize;

    public MaskItemOuter(int leftSize, int rightSize) {
        this.leftSize = leftSize;
        this.rightSize = rightSize;
    }

    @Override
    protected String doHandle(String src) {
        return maskOuter(src, leftSize, rightSize, maskStr, maskLength);
    }

    @Override
    protected String[] doHandle(String originalSrc, String[] separatedStrs) {
        return maskOuter(separatedStrs, leftSize, rightSize, maskStr, maskLength);
    }

    /**
     * 模糊两边
     *
     * @param src
     * @param leftSize   左模糊长度
     * @param rightSize  右模糊长度
     * @param maskStr    模糊字符
     * @param maskLength 模糊字符长度，&lt;=0: 与源字符个数一致。大于0：不管源字符串多长，使用固定数量的模糊字符串模糊数据
     * @return 脱敏结果
     */
    private static String maskOuter(String src, int leftSize, int rightSize, String maskStr, int maskLength) {
        if (leftSize < 0 || rightSize < 0) {
            throw new IllegalArgumentException("leftSize和rightSize不能小于0");
        }

        int maskSize = leftSize + rightSize;
        if (maskSize >= src.length()) {
            maskSize = maskLength <= 0 ? src.length() : maskLength;
            return Strings.repeat(maskStr, maskSize);
        }

        StringBuilder result = new StringBuilder();
        // 左边模糊
        int leftMaskSize = leftSize == 0 || maskLength <= 0 ? leftSize : maskLength;
        result.append(Strings.repeat(maskStr, leftMaskSize));

        // 中间保留
        String reverseStr = src.substring(leftSize, src.length() - rightSize);
        result.append(reverseStr);

        // 右边模糊
        int rightMaskSize = rightSize == 0 || maskLength <= 0 ? rightSize : maskLength;
        result.append(Strings.repeat(maskStr, rightMaskSize));

        return result.toString();
    }

    /**
     * 模糊两边
     *
     * @param src
     * @param leftSize   左模糊长度
     * @param rightSize  右模糊长度
     * @param maskStr    模糊字符
     * @param maskLength 模糊字符长度，&lt;=0: 与源字符个数一致。大于0：不管源字符串多长，使用maskLength数量的模糊字符串模糊数据
     * @return 脱敏结果
     */
    private static String[] maskOuter(String[] src, int leftSize, int rightSize, String maskStr, int maskLength) {
        if (leftSize < 0 || rightSize < 0) {
            throw new IllegalArgumentException("leftSize和rightSize不能小于0");
        }

        int maskSize = leftSize + rightSize;
        if (maskSize >= src.length) {
            maskSize = maskLength <= 0 ? src.length : maskLength;
            String[] result = new String[maskSize];
            for (int i = 0; i < maskSize; i++) {
                result[i] = maskStr;
            }
            return result;
        }

        int reverseSize = src.length - maskSize;
        // 左边模糊
        int leftMaskSize = leftSize == 0 || maskLength <= 0 ? leftSize : maskLength;
        // 右边模糊
        int rightMaskSize = rightSize == 0 || maskLength <= 0 ? rightSize : maskLength;
        int resultSize = reverseSize + leftMaskSize + rightMaskSize;
        String[] result = new String[resultSize];

        // 模糊左边
        for (int i = 0; i < leftMaskSize; i++) {
            result[i] = maskStr;
        }

        // 中间保留
        int reserveIndex = leftSize;
        for (int i = leftMaskSize; i < resultSize - rightMaskSize; i++) {
            result[i] = src[reserveIndex++];
        }

        // 模糊右边
        for (int i = resultSize - rightMaskSize; i < resultSize; i++) {
            result[i] = maskStr;
        }

        return result;
    }

}
