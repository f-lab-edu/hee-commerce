package com.hcommerce.heecommerce.order.model;

import lombok.Builder;
import lombok.Getter;


/**
 * TossPaymentsApproveResultForStorage 는 토스페이먼츠 응답 결과 중 DB에 저장할 데이터들을 모아놓은 클래스이다.
 * TODO : requestedAt도 추후 필요할지 고민해보기
 * approvedAt : ISO 8601 형식 : yyyy-MM-dd'T'HH:mm:ss±hh:mm (e.g. 2022-01-01T00:00:00+09:00)
 */
@Getter
public class TossPaymentsApproveResultForStorage {

    private final String approvedAt;

    @Builder
    public TossPaymentsApproveResultForStorage(String approvedAt) {
        this.approvedAt = approvedAt;
    }
}
