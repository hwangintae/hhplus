package org.hhplus.ecommerce.docs.item;

import org.hhplus.ecommerce.docs.RestDocsSupport;
import org.hhplus.ecommerce.item.controller.ItemController;
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

public class ItemControllerDocsTest extends RestDocsSupport {

    @Override
    protected Object initController() {
        return new ItemController();
    }


    @Test
    @DisplayName("상품 조회")
    public void getItem() throws Exception {
        // given

        // expected
        mockMvc.perform(get("/api/item")
                        .param("itemId", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("item-get",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        queryParameters(
                                parameterWithName("itemId").description("상품 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("상품 명"),
                                fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("가격"),
                                fieldWithPath("data.quantity").type(JsonFieldType.NUMBER).description("재고")
                        )
                ));
    }

    @Test
    @DisplayName("상품 목록 조회")
    public void getItems() throws Exception {
        // given

        // expected
        mockMvc.perform(get("/api/items"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("items-get",
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지"),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING).description("상품 명"),
                                fieldWithPath("data[].price").type(JsonFieldType.NUMBER).description("가격"),
                                fieldWithPath("data[].quantity").type(JsonFieldType.NUMBER).description("재고")
                        )
                ));
    }
}
