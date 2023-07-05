package com.hcommerce.heecommerce.common.utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("TypeConversionUtils")
class TypeConversionUtilsTest {

    // TODO : 예외 케이스 테스트 추가 필요함(예 : strings의 길이가 0일 때, 숫자인 문자가 아닌 경우 등)
    @Nested
    @DisplayName("convertStringsToIntegers")
    class Describe_ConvertStringsToIntegers {
        @Test
        @DisplayName("returns integerArrayList")
        void It_returns_IntegerArrayList() {
            // Given
            List<String> strings = new ArrayList<>();
            strings.add("1");
            strings.add("2");
            strings.add("3");

            // When
            List<Integer> integers = TypeConversionUtils.convertStringsToIntegers(strings);

            // Then
            List<Integer> expectedIntegers = new ArrayList<>();
            expectedIntegers.add(1);
            expectedIntegers.add(2);
            expectedIntegers.add(3);

            for (int i = 0; i < expectedIntegers.size(); i++) {
                assertEquals(expectedIntegers.get(i), integers.get(i));
            }
        }
    }

    // TODO : 예외 케이스 테스트 추가 필요함(예 : 2개의 배열 중 한개라도 길이가 0일 때, 또는 갯수가 안 맞을 때)
    @Nested
    @DisplayName("convertTwoListToMap")
    class Describe_ConvertTwoListToMap {
        @Test
        @DisplayName("returns map")
        void It_returns_Map() {
            // Given
            List<String> strings = new ArrayList<>();
            strings.add("1");
            strings.add("2");
            strings.add("3");

            List<Integer> integers = new ArrayList<>();
            integers.add(10);
            integers.add(20);
            integers.add(30);

            // When
            Map<String, Integer> map = TypeConversionUtils.convertTwoListToMap(strings, integers);

            // Then
            Map<String, Integer> expectedMap = new ConcurrentHashMap<>();
            expectedMap.put("1", 10);
            expectedMap.put("2", 20);
            expectedMap.put("3", 30);

            for (int i = 0; i < strings.size(); i++) {
                assertEquals(expectedMap.get(strings.get(i)), map.get(strings.get(i)));
            }
        }
    }

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
