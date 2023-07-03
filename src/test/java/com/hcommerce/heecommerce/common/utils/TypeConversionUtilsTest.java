package com.hcommerce.heecommerce.common.utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("TypeConversionUtils")
class TypeConversionUtilsTest {

    @Nested
    @DisplayName("convertBinaryToUuid")
    class Describe_ConvertBinaryToUuid {
        @Test
        @DisplayName("returns uuid")
        void It_returns_UUID() {
            // Given
            byte[] bytes = {-74, -92, -70, -108, -32, 97, 72, -128, -24, -120, 2, -7, 70, -55, 74, 73};

            // When
            UUID actualUuid = TypeConversionUtils.convertBinaryToUuid(bytes);

            // Then
            UUID expectedUuid = UUID.fromString("b6a4ba94-e061-4880-e888-02f946c94a49");

            assertEquals(expectedUuid, actualUuid);
        }
    }

    @Nested
    @DisplayName("convertUuidToBinary")
    class Describe_ConvertUuidToBinary {
        @Test
        @DisplayName("returns binaryArray")
        void It_returns_binaryArray() {
            // Given
            UUID uuid = UUID.fromString("b6a4ba94-e061-4880-e888-02f946c94a49");

            // When
            byte[] actualBytes = TypeConversionUtils.convertUuidToBinary(uuid);

            // Then
            byte[] expectedBytes = {-74, -92, -70, -108, -32, 97, 72, -128, -24, -120, 2, -7, 70, -55, 74, 73};

            assertArrayEquals(expectedBytes, actualBytes);
        }
    }
}
