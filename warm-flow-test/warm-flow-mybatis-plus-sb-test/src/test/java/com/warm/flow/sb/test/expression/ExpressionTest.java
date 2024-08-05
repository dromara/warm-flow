/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.warm.flow.sb.test.expression;


import com.warm.flow.core.test.FlowBaseTest;
import com.warm.flow.core.utils.ExpressionUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@SpringBootTest
public class ExpressionTest extends FlowBaseTest {

    /**
     *  @@spel@@|#{@user.eval()}
     */
    @Test
    public void testSpel() {
        Map<String, Object> variable = new HashMap<>();
        variable.put("aa", "yes");
        log.info("spel结果:{}", ExpressionUtil.eval("@@spel@@|#{@user.eval()}", null));
    }
}
