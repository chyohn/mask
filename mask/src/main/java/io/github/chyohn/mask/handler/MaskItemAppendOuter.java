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
 * 对数据左部分和右边部分添加脱敏字符
 *
 * @author  qiang.shao
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class MaskItemAppendOuter extends AbstractMaskItemHandler<MaskItemAppendOuter> {

    private static final int DEFAULT_MASK_LENGTH = 1;
    private int leftSize;
    private int rightSize;

    public MaskItemAppendOuter(int leftSize, int rightSize) {
        this.leftSize = leftSize;
        this.rightSize = rightSize;
    }

    @Override
    protected String doHandle(String src) {
        return Strings.repeat(maskStr, leftSize) + src + Strings.repeat(maskStr, rightSize);
    }

    @Override
    protected String[] doHandle(String originalSrc, String[] separatedStrs) {
        String[] result = new String[separatedStrs.length];
        for (int i = 0; i < separatedStrs.length; i++) {
            result[i] = doHandle(separatedStrs[i]);
        }
        return result;
    }

}
