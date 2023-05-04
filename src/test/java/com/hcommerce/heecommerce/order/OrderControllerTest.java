package com.hcommerce.heecommerce.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcommerce.heecommerce.EnableMockMvc;
import com.hcommerce.heecommerce.common.dto.ErrorResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableMockMvc
@SpringBootTest
@AutoConfigureRestDocs
@DisplayName("OrderController")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        session = new MockHttpSession();
    }

    @Nested
    @DisplayName("PATCH /admin/orders/{orderUuid}/order-receipt-complete ")
    class Describe_OrderReceiptComplete_API {
        private final String ORDER_RECEIPT_COMPLETE_URL = "/admin/orders/{orderUuid}/order-receipt-complete";

        private final UUID ORDER_UUID = UUID.randomUUID();

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Nested
        @DisplayName("when login user is admin")
        class Context_When_Login_User_Is_Admin {
            @BeforeEach
            void setUp() {
                session.setAttribute("isAdmin", true);
            }

            @Nested
            @DisplayName("when `orderUuid` exists in DB")
            class Context_With_Exited_OrderUuid {
                @Nested
                @DisplayName("when order quantity < stock quantity")
                class Context_When_Order_Quantity_Is_Less_than_Stock_quantity {
                    @Test
                    @DisplayName("returns 200 ok")
                    void it_returns_200_OK() throws Exception {
                        // when
                        ResultActions resultActions = mockMvc.perform(
                                patch(ORDER_RECEIPT_COMPLETE_URL, ORDER_UUID)
                                        .session(session)
                        );

                        // then
                        resultActions.andExpect(status().isOk())
                                .andExpect(content().string(containsString("주문 접수 완료가 처리되었습니다.")))
                                .andDo(OrderControllerRestDocs.completeOrderReceipt());
                    }
                }

                // TODO : DB 연동 후 테스트 어떻게 수정할지 고민하기
                @Nested
                @DisplayName("when order quantity > stock quantity")
                class Context_When_Order_Quantity_Is_More_than_Stock_quantity {
                    @Test
                    @DisplayName("returns 409 error")
                    void it_returns_409_Error() throws Exception {
                        // given
                        final String ORDER_UUID_OVER_STOCK = "27f43b31-e97f-11ed-93e5-0242ac110002";

                        // when
                        ResultActions resultActions = mockMvc.perform(
                                patch(ORDER_RECEIPT_COMPLETE_URL, ORDER_UUID_OVER_STOCK)
                                        .session(session)
                        );

                        // then
                        resultActions.andExpect(status().isConflict())
                                .andExpect(content().string(containsString("재고 수량 부족으로 주문 접수를 할 수 없습니다.")));
                    }
                }
            }

            @Nested
            @DisplayName("when `orderUuid` does not exist in DB")
            class Context_Without_Exited_OrderUuid {
                @Test
                @DisplayName("returns 404 error")
                void it_returns_404_Error() throws Exception {
                    String TEMP_NOT_EXIST_UUID = "8b455042-e709-11ed-93e5-0242ac110002";

                    // when
                    ResultActions resultActions = mockMvc.perform(
                            patch(ORDER_RECEIPT_COMPLETE_URL, TEMP_NOT_EXIST_UUID)
                                    .session(session)
                    );

                    // then
                    resultActions.andExpect(status().isNotFound())
                            .andExpect(content().string(containsString("해당 주문을 찾을 수 없습니다.")));
                }
            }


        }

        @Nested
        @DisplayName("when login user is not admin")
        class Context_When_Login_User_Is_Not_Admin {
            @Test
            @DisplayName("returns 403 error ")
            void it_returns_403_Error() throws Exception {
                // given
                session.setAttribute("isAdmin", false);

                // when
                ResultActions resultActions = mockMvc.perform(
                        patch(ORDER_RECEIPT_COMPLETE_URL, ORDER_UUID)
                                .session(session)
                );

                // then
                ErrorResponseDto forbiddenErrorResponseDto = new ErrorResponseDto(HttpStatus.FORBIDDEN.name(), "관리자만 이용 가능합니다.");

                resultActions.andExpect(status().isForbidden())
                        .andExpect(content().json(objectMapper.writeValueAsString(forbiddenErrorResponseDto)));
            }
        }

        @Nested
        @DisplayName("when user is not login")
        class Context_When_User_Is_Not_Login {
            @Test
            @DisplayName("returns 401 error ")
            void it_returns_401_Error() throws Exception {
                // when
                ResultActions resultActions = mockMvc.perform(
                        patch(ORDER_RECEIPT_COMPLETE_URL, ORDER_UUID)
                );

                // then
                ErrorResponseDto unauthorizedErrorResponseDto = new ErrorResponseDto(HttpStatus.UNAUTHORIZED.name(), "로그인 후에 이용할 수 있습니다.");

                resultActions.andExpect(status().isUnauthorized())
                        .andExpect(content().json(objectMapper.writeValueAsString(unauthorizedErrorResponseDto)));
            }
        }
    }
}
