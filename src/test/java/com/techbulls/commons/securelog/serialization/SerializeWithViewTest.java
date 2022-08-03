/*
 *    Copyright 2022 TechBulls SoftTech
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.techbulls.commons.securelog.serialization;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;
import org.junit.Test;

import static com.techbulls.commons.securelog.serialization.TestUtils.*;

public class SerializeWithViewTest {
    @Test
    public void testSafeToStringWithJsonViews() throws JsonProcessingException {
        Order order = new Order();
        order.setClientOrderId("ORD1234");
        order.setAmount(1200);
        order.setOrderId("XE5678");
        order.setStatus("PENDING");
        order.setId(100l);

        String json = SecureJson.toJson(order, false, Views.Input.class);

        System.out.println("Json: " + json);

        JsonNode root = TestUtils.asJsonNode(json);

        assertContainsNodeWithText(root, "clientOrderId", order.getClientOrderId());
        assertContainsNodeWithText(root, "amount", order.getAmount().toString());
        assertContainsNodeWithText(root, "id", order.getId().toString());
        assertNodeDoesNotExist(root, "orderId");
        assertNodeDoesNotExist(root, "status");

        json = SecureJson.toJson(order, false, Views.Output.class);

        System.out.println("Json: " + json);

        root = TestUtils.asJsonNode(json);

        assertContainsNodeWithText(root, "clientOrderId", order.getClientOrderId());
        assertContainsNodeWithText(root, "amount", order.getAmount().toString());
        assertContainsNodeWithText(root, "orderId", order.getOrderId());
        assertContainsNodeWithText(root, "status", order.getStatus());
        assertContainsNodeWithText(root, "id", order.getId().toString());
    }

    interface Views {
        interface Input {

        }

        interface Output extends Input {

        }
    }

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private static class Order {
        @JsonView(Views.Input.class)
        private String clientOrderId;

        @JsonView(Views.Input.class)
        private Integer amount;

        @JsonView(Views.Output.class)
        private String orderId;

        @JsonView(Views.Output.class)
        private String status;

        // Not included in any of the views
        private Long id;
    }
}
