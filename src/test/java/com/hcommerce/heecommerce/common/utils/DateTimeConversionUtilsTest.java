package com.hcommerce.heecommerce.common.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("DateTimeConversionUtils")
class DateTimeConversionUtilsTest {

    @Nested
    @DisplayName("convertIsoDateTimeToInstant")
    class Describe_ConvertIsoDateTimeToInstant {
        @Test
        @DisplayName("converts IsoDateTime to Instant")
        void converts_IsoDateTime_To_Instant() {

            // Given
            String isoDateTime = "2023-07-08T12:34:56+00:00";

            // When
            Instant instant = DateTimeConversionUtils.convertIsoDateTimeToInstant(isoDateTime);

            // Then
            Instant expectedInstant = OffsetDateTime.parse(isoDateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant();

            assertEquals(expectedInstant, instant);
        }
    }
}
