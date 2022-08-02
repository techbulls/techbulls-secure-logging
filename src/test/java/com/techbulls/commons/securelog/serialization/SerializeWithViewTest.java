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

        String json = SecureLogUtils.safeToString(order, false, Views.Input.class);

        System.out.println("Json: " + json);

        JsonNode root = TestUtils.asJsonNode(json);

        assertContainsNodeWithText(root, "clientOrderId", order.getClientOrderId());
        assertContainsNodeWithText(root, "amount", order.getAmount().toString());
        assertContainsNodeWithText(root, "id", order.getId().toString());
        assertNodeDoesNotExist(root, "orderId");
        assertNodeDoesNotExist(root, "status");

        json = SecureLogUtils.safeToString(order, false, Views.Output.class);

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
