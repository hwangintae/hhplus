package org.hhplus.ecommerce.docs.cart;

import org.hhplus.ecommerce.cart.controller.CartController;
import org.hhplus.ecommerce.cash.controller.CashController;
import org.hhplus.ecommerce.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.hhplus.ecommerce.docs.ApiDocsUtil.getDocumentRequest;
import static org.hhplus.ecommerce.docs.ApiDocsUtil.getDocumentResponse;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CartControllerDocsTest extends RestDocsSupport {

    @Override
    protected Object initController() {
        return new CartController();
    }

    @Test
    @DisplayName("장바구니 목록 조회")
    public void getCart() throws Exception {
        // given

        // expected
        mockMvc.perform(get("/api/cart")
                        .param("userId", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("cart-get",
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
                                fieldWithPath("data[].status").type(JsonFieldType.STRING).description("재고 여부")
                        )
                ));
    }
}
