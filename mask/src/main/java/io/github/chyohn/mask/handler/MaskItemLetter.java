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

import lombok.NoArgsConstructor;

import java.util.Arrays;

/**
 * 只模糊字符串中的字母
 *
 * @author  qiang.shao
 * @since 1.0.0
 */
@NoArgsConstructor
public class MaskItemLetter extends AbstractMaskItemHandler<MaskItemLetter> {

    @Override
    protected String doHandle(String src) {
        if (maskLength < 1) {
            return src.replaceAll("[a-zA-Z]", maskStr);
        }
        String[] masks = new String[maskLength];
        Arrays.fill(masks, maskStr);
        String allMaskStr = String.join("", masks);
        return src.replaceAll("[a-zA-Z]+", allMaskStr);
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
