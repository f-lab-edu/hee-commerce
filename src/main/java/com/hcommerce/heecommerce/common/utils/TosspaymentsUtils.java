package com.hcommerce.heecommerce.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcommerce.heecommerce.order.dto.OrderApproveForm;
import com.hcommerce.heecommerce.payment.TosspaymentsException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;

public class TosspaymentsUtils {

    public static final String TOSS_PAYMENT_CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";

    /**
     * createHttpRequestForPaymentApprove 는 토스페이먼츠에 결제 승인을 받기 위한 Request 를 만드는 함수 이다.
     */
    public static HttpEntity<String> createHttpRequestForPaymentApprove(OrderApproveForm orderApproveForm) {
        HttpHeaders headers = TosspaymentsUtils.createHttpHeadersForPaymentApprove();

        return HttpUtils.createHttpEntity(headers, orderApproveForm);
    }

    /**
     * createHttpHeadersForPaymentApprove 는 토스페이먼츠에 결제 승인을 받기 위한 Request의 HttpHeader 를 만드는 함수 이다.
     */
    private static HttpHeaders createHttpHeadersForPaymentApprove() {
        final String SECRET_KEY = "test_sk_ADpexMgkW3651LBNOYN8GbR5ozO0"; // TODO : 설정에서

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }

    /**
     * createTosspaymentsExceptionToExceptionMessage 는
     */
    public static TosspaymentsException createTosspaymentsExceptionToExceptionMessage(String inputText) {
        try {
            Matcher matcher = Pattern.compile("\\d+").matcher(inputText);

            if (!matcher.find()) {
                System.out.println("HTTP Status Code not found"); // TODO : 어떻게 처리할까?
            }

            String jsonText = inputText.substring(inputText.indexOf("{"),
                inputText.lastIndexOf("}") + 1);

            ObjectMapper mapper = new ObjectMapper();

            JsonNode rootNode = mapper.readTree(jsonText);

            int httpStatus = Integer.parseInt(matcher.group());

            HttpStatusCode httpStatusCode = HttpStatusCode.valueOf(httpStatus);

            String code = rootNode.get("code").asText();
            String message = rootNode.get("message").asText();

            return TosspaymentsException.builder()
                .tossHttpStatusCode(httpStatusCode)
                .code(code)
                .message(message)
                .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw e;
        }
    }
}
