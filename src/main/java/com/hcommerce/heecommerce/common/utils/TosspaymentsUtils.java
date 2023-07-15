package com.hcommerce.heecommerce.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcommerce.heecommerce.order.OrderApproveForm;
import com.hcommerce.heecommerce.payment.TosspaymentsException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;

public class TosspaymentsUtils {

    public static final String TOSS_PAYMENT_CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";

    // 참고 : 승인 처리 예외 https://docs.tosspayments.com/reference/error-codes#%EA%B2%B0%EC%A0%9C-%EC%8A%B9%EC%9D%B8
    private static final Set<String> ROLLBACK_NEEDED_TOSSPAYMENTS_EXCEPTION_SETS = new HashSet<>(
        Arrays.asList(
            "EXCEED_MAX_CARD_INSTALLMENT_PLAN", // 설정 가능한 최대 할부 개월 수를 초과했습니다.
            "NOT_ALLOWED_POINT_USE", // 포인트 사용이 불가한 카드로 카드 포인트 결제에 실패했습니다.
            "INVALID_REJECT_CARD", // 카드 사용이 거절되었습니다. 카드사 문의가 필요합니다.
            "BELOW_MINIMUM_AMOUNT", // 신용카드는 결제금액이 100원 이상, 계좌는 200원이상부터 결제가 가능합니다.
            "INVALID_CARD_EXPIRATION", // 카드 정보를 다시 확인해주세요. (유효기간)
            "INVALID_STOPPED_CARD", // 정지된 카드 입니다.
            "EXCEED_MAX_DAILY_PAYMENT_COUNT", // 하루 결제 가능 횟수를 초과했습니다.
            "NOT_SUPPORTED_INSTALLMENT_PLAN_CARD_OR_MERCHANT", // 할부가 지원되지 않는 카드 또는 가맹점 입니다.
            "INVALID_CARD_INSTALLMENT_PLAN", // 할부 개월 정보가 잘못되었습니다.
            "NOT_SUPPORTED_MONTHLY_INSTALLMENT_PLAN", // 할부가 지원되지 않는 카드입니다.
            "EXCEED_MAX_PAYMENT_AMOUNT", // 하루 결제 가능 금액을 초과했습니다.
            "INVALID_CARD_LOST_OR_STOLEN", // 분실 혹은 도난 카드입니다.
            "RESTRICTED_TRANSFER_ACCOUNT", // 계좌는 등록 후 12시간 뒤부터 결제할 수 있습니다. 관련 정책은 해당 은행으로 문의해주세요.
            "EXCEED_MAX_ONE_DAY_WITHDRAW_AMOUNT", // 1일 출금 한도를 초과했습니다.
            "EXCEED_MAX_ONE_TIME_WITHDRAW_AMOUNT", // 1회 출금 한도를 초과했습니다.
            "EXCEED_MAX_AMOUNT", // 거래금액 한도를 초과했습니다.
            "INVALID_ACCOUNT_INFO_RE_REGISTER", // 유효하지 않은 계좌입니다. 계좌 재등록 후 시도해주세요.
            "NOT_AVAILABLE_PAYMENT", // 결제가 불가능한 시간대입니다
            "REJECT_ACCOUNT_PAYMENT", // 잔액부족으로 결제에 실패했습니다.
            "REJECT_CARD_PAYMENT", // 한도초과 혹은 잔액부족으로 결제에 실패했습니다.
            "REJECT_CARD_COMPANY", // 결제 승인이 거절되었습니다.
            "REJECT_TOSSPAY_INVALID_ACCOUNT", // 선택하신 출금 계좌가 출금이체 등록이 되어 있지 않아요. 계좌를 다시 등록해 주세요
            "EXCEED_MAX_AUTH_COUNT", // 최대 인증 횟수를 초과했습니다. 카드사로 문의해주세요.
            "EXCEED_MAX_ONE_DAY_AMOUNT", // 일일 한도를 초과했습니다.
            "NOT_AVAILABLE_BANK", // 은행 서비스 시간이 아닙니다.
            "INVALID_PASSWORD", // 결제 비밀번호가 일치하지 않습니다.
            ""
        )
    );

    private static final Set<String> RETRY_NEEDED_TOSSPAYMENTS_EXCEPTION_SETS = new HashSet<>(
        Arrays.asList(
            "PROVIDER_ERROR", // 일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.
            "INVALID_CARD_NUMBER", // 카드번호를 다시 확인해주세요.
            "CARD_PROCESSING_ERROR", // 카드사에서 오류가 발생했습니다.
            "FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING", // 결제가 완료되지 않았어요. 다시 시도해주세요.
            "FAILED_INTERNAL_SYSTEM_PROCESSING", // 내부 시스템 처리 작업이 실패했습니다. 잠시 후 다시 시도해주세요.
            "UNKNOWN_PAYMENT_ERROR" // 결제에 실패했어요. 같은 문제가 반복된다면 은행이나 카드사로 문의해주세요.
        )
    );

    private static final Set<String> ROLLBACK_NOT_NEEDED_TOSSPAYMENTS_EXCEPTION_SETS = new HashSet<>(
        Arrays.asList(
            "ALREADY_PROCESSED_PAYMENT", // 이미 처리된 결제 입니다.
            "INVALID_REQUEST", // 잘못된 요청입니다.
            "INVALID_API_KEY", // 잘못된 시크릿키 연동 정보 입니다.
            "NOT_FOUND_TERMINAL_ID", // 단말기번호(Terminal Id)가 없습니다. 토스페이먼츠로 문의 바랍니다.
            "INVALID_AUTHORIZE_AUTH", // 유효하지 않은 인증 방식입니다.
            "INVALID_UNREGISTERED_SUBMALL", // 등록되지 않은 서브몰입니다. 서브몰이 없는 가맹점이라면 안심클릭이나 ISP 결제가 필요합니다.
            "NOT_REGISTERED_BUSINESS", // 등록되지 않은 사업자 번호입니다.
            "UNAPPROVED_ORDER_ID", // 아직 승인되지 않은 주문번호입니다.
            "UNAUTHORIZED_KEY", // 인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다.
            "FORBIDDEN_REQUEST", // 허용되지 않은 요청입니다.
            "INCORRECT_BASIC_AUTH_FORMAT", // 잘못된 요청입니다. ':' 를 포함해 인코딩해주세요.
            "NOT_FOUND_PAYMENT", // 존재하지 않는 결제 정보 입니다.
            "NOT_FOUND_PAYMENT_SESSION" // 결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다.
        )
    );

    /**
     * isRollbackNeededExceptionCode 는 rollback이 필요한 예외인지를 판단하는 함수이다.
     */
    public static boolean isRollbackNeededExceptionCode(String code) {
        return ROLLBACK_NEEDED_TOSSPAYMENTS_EXCEPTION_SETS.contains(code);
    }

    /**
     * isRetryNeededExceptionCode 는 사용자가 재시도 할 수 있는 예외인지를 판단하는 함수이다.
     */
    public static boolean isRetryNeededExceptionCode(String code) {
        return RETRY_NEEDED_TOSSPAYMENTS_EXCEPTION_SETS.contains(code);
    }

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
