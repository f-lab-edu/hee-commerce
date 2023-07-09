package com.hcommerce.heecommerce.common.utils;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeConversionUtils {

    /**
     * convertIsoDateTimeToInstant 는 ISO 8601 형식의 날짜를 Instant 타입으로 변환하는 함수이다.
     * 이 함수가 필요한 이유는 토스페이먼츠에서 다루는 날짜 타입과 우리의 날짜 타입이 다르기 때문이다.
     * 토스페이먼츠에서는 ISO 8601 형식으로 다루는데, 우리는 DB에 저장할 때 Instant 로 다룬다.
     */
    public static Instant convertIsoDateTimeToInstant(String isoDateTime) {
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(isoDateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return offsetDateTime.toInstant();
    }
}
