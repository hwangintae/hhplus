package org.hhplus.ecommerce.docs.point;

import org.hhplus.ecommerce.docs.ApiDocsUtil;
import org.hhplus.ecommerce.docs.RestDocsSupport;
import org.hhplus.ecommerce.point.controller.PointController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.RequestDocumentation;

import static org.hhplus.ecommerce.docs.ApiDocsUtil.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PointControllerDocsTest extends RestDocsSupport {

    @Override
    protected Object initController() {
        return new PointController();
    }

    @Test
    @DisplayName("rest docs 잘 되는지 테스트")
    public void testPingPong() throws Exception {
        // given

        // expected
        mockMvc.perform(get("/api/point/ping")
                        .param("text", "hello"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("point-ping",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        queryParameters(
                                parameterWithName("text").description("텍스트")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("결과 상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지"),
                                fieldWithPath("data").type(JsonFieldType.STRING).description("data")
                        )
                ));
    }
}
