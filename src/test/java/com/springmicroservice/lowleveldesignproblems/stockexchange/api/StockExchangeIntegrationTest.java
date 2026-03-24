package com.springmicroservice.lowleveldesignproblems.stockexchange.api;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StockExchangeIntegrationTest {

    private static final long ASYNC_TIMEOUT_MS = 5000L;
    private static final long POLL_MS = 50L;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void placeOrder_returns201WithOrderBody() throws Exception {
        mockMvc.perform(post("/api/exchange/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {"userId":"user-a","stockId":"RELIANCE","orderType":"BUY","quantity":5,"price":2500.0}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value("user-a"))
                .andExpect(jsonPath("$.stockSymbol").value("RELIANCE"))
                .andExpect(jsonPath("$.orderType").value("BUY"))
                .andExpect(jsonPath("$.quantity").value(5))
                .andExpect(jsonPath("$.filledQuantity").value(0))
                .andExpect(jsonPath("$.remainingQuantity").value(5))
                .andExpect(jsonPath("$.price").value(2500.0));
    }

    @Test
    void placeOrder_missingUserId_returns400() throws Exception {
        mockMvc.perform(post("/api/exchange/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {"stockId":"RELIANCE","orderType":"BUY","quantity":5,"price":100.0}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrder_afterPlace_returns200() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/exchange/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {"userId":"user-b","stockId":"TCS","orderType":"SELL","quantity":3,"price":3200.0}
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        String orderId = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/api/exchange/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.userId").value("user-b"));
    }

    @Test
    void getOrder_unknown_returns404() throws Exception {
        mockMvc.perform(get("/api/exchange/orders/{orderId}", "non-existent-order-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelOrder_returns204() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/exchange/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {"userId":"user-c","stockId":"INFY","orderType":"BUY","quantity":2,"price":1500.0}
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        String orderId = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(delete("/api/exchange/orders/{orderId}", orderId).param("userId", "user-c"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/exchange/orders/{orderId}", orderId)).andExpect(status().isNotFound());
    }

    @Test
    void cancelOrder_wrongUser_returns403() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/exchange/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {"userId":"owner-1","stockId":"WIPRO","orderType":"BUY","quantity":1,"price":400.0}
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        String orderId = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(delete("/api/exchange/orders/{orderId}", orderId).param("userId", "intruder"))
                .andExpect(status().isForbidden());
    }

    @Test
    void putOrder_pathIdMismatch_returns400() throws Exception {
        mockMvc.perform(put("/api/exchange/orders/order-aaa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {"orderId":"order-bbb","userId":"u","quantity":5,"price":1.0}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void matching_buyAndSellSamePrice_persistsTrade() throws Exception {
        mockMvc.perform(post("/api/exchange/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {"userId":"buyer-1","stockId":"MATCH1","orderType":"BUY","quantity":10,"price":99.5}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/exchange/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {"userId":"seller-1","stockId":"MATCH1","orderType":"SELL","quantity":10,"price":99.5}
                                """))
                .andExpect(status().isCreated());

        waitUntilTradeVisible("MATCH1", 10);

        mockMvc.perform(get("/api/exchange/trades").param("stockId", "MATCH1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].quantity").value(10))
                .andExpect(jsonPath("$[0].price").value(99.5))
                .andExpect(jsonPath("$[0].buyerOrderId").exists())
                .andExpect(jsonPath("$[0].sellerOrderId").exists());
    }

    @Test
    void getTrades_missingQueryParams_returns400() throws Exception {
        mockMvc.perform(get("/api/exchange/trades")).andExpect(status().isBadRequest());
    }

    @Test
    void getTrade_unknown_returns404() throws Exception {
        mockMvc.perform(get("/api/exchange/trades/{tradeId}", "unknown-trade")).andExpect(status().isNotFound());
    }

    @Test
    void getTrades_byOrderId_afterMatch_returnsTrades() throws Exception {
        MvcResult buyResult = mockMvc.perform(post("/api/exchange/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {"userId":"u-buy","stockId":"MATCH2","orderType":"BUY","quantity":4,"price":50.0}
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        String buyerOrderId = JsonPath.read(buyResult.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(post("/api/exchange/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {"userId":"u-sell","stockId":"MATCH2","orderType":"SELL","quantity":4,"price":50.0}
                                """))
                .andExpect(status().isCreated());

        waitUntilTradeVisible("MATCH2", 4);

        mockMvc.perform(get("/api/exchange/trades").param("orderId", buyerOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].buyerOrderId").value(buyerOrderId));
    }

    private void waitUntilTradeVisible(String stockId, int expectedQuantity) throws Exception {
        long deadline = System.currentTimeMillis() + ASYNC_TIMEOUT_MS;
        while (System.currentTimeMillis() < deadline) {
            MvcResult r = mockMvc.perform(get("/api/exchange/trades").param("stockId", stockId))
                    .andReturn();
            if (r.getResponse().getStatus() == 200) {
                String body = r.getResponse().getContentAsString();
                if (body != null && body.startsWith("[") && body.length() > 2) {
                    Integer qty = JsonPath.read(body, "$[0].quantity");
                    if (qty != null && qty == expectedQuantity) {
                        return;
                    }
                }
            }
            Thread.sleep(POLL_MS);
        }
        fail("Timed out waiting for trade on stock " + stockId);
    }
}
