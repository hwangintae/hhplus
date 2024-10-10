package org.hhplus.ecommerce.docs.orders;

import org.hhplus.ecommerce.cash.controller.CashController;
import org.hhplus.ecommerce.cash.service.AddCashRequest;
import org.hhplus.ecommerce.docs.RestDocsSupport;
import org.hhplus.ecommerce.orders.controller.OrdersController;
import org.hhplus.ecommerce.orders.service.OrderRequest;
import org.hhplus.ecommerce.orders.service.OrderRequests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import java.math.BigDecimal;
import java.util.List;

import static org.hhplus.ecommerce.docs.ApiDocsUtil.getDocumentRequest;
import static org.hhplus.ecommerce.docs.ApiDocsUtil.getDocumentResponse;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrdersControllerDocsTest extends RestDocsSupport {

    @Override
    protected Object initController() {
        return new OrdersController();
    }

    @Test
    @DisplayName("상품 주문")
    public void orders() throws Exception {
        // given
        OrderRequests orderRequests = OrderRequests.builder()
                .orderRequests(List.of(
                        OrderRequest.builder()
                                .userId(1L)
                                .itemId(22L)
                                .cnt(33)
                                .build()))
                .build();

        // expected
        mockMvc.perform(post("/api/orders")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequests)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("orders-post",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("orderRequests[].userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                                fieldWithPath("orderRequests[].itemId").type(JsonFieldType.NUMBER).description("상품 ID"),
                                fieldWithPath("orderRequests[].cnt").type(JsonFieldType.NUMBER).description("개수")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지"),
                                fieldWithPath("data").type(JsonFieldType.STRING).description("주문 결과 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("상품 목록 조회")
    public void getOrders() throws Exception {
        // given

        // expected
        mockMvc.perform(get("/api/orders")
                        .param("userId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("orders-get",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        queryParameters(
                                parameterWithName("userId").description("사용자 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지"),
                                fieldWithPath("data[].itemName").type(JsonFieldType.STRING).description("상품 명"),
                                fieldWithPath("data[].itemCnt").type(JsonFieldType.NUMBER).description("상품 개수"),
                                fieldWithPath("data[].status").type(JsonFieldType.STRING).description("주문 상태")
                        )
                ));
    }
}
