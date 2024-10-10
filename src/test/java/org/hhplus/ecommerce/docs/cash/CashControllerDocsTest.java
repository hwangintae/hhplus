package org.hhplus.ecommerce.docs.cash;

import com.sun.net.httpserver.HttpContext;
import org.hhplus.ecommerce.cash.service.AddCashRequest;
import org.hhplus.ecommerce.docs.RestDocsSupport;
import org.hhplus.ecommerce.cash.controller.CashController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

import static org.hhplus.ecommerce.docs.ApiDocsUtil.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CashControllerDocsTest extends RestDocsSupport {

    @Override
    protected Object initController() {
        return new CashController();
    }

    @Test
    @DisplayName("잔액 조회")
    public void getCash() throws Exception {
        // given

        // expected
        mockMvc.perform(get("/api/cash")
                        .param("userId", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("cash-get",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        queryParameters(
                                parameterWithName("userId").description("사용자 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지"),
                                fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                                fieldWithPath("data.amount").type(JsonFieldType.NUMBER).description("잔액")
                        )
                ));
    }

    @Test
    @DisplayName("충전")
    public void addCash() throws Exception {
        // given
        AddCashRequest request = AddCashRequest.builder()
                .userId(2L)
                .amount(new BigDecimal(1_000))
                .build();

        // expected
        mockMvc.perform(post("/api/cash")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("cash-add",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                                fieldWithPath("amount").type(JsonFieldType.NUMBER).description("충전 금액")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지"),
                                fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                                fieldWithPath("data.amount").type(JsonFieldType.NUMBER).description("잔액")
                        )
                ));
    }
}
