package com.hcommerce.heecommerce.common.utils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TypeConversionUtils {

    // TODO : 현재 String 리스트 -> Integer 리스트 변환하는데, 추후에 제네릭을 이용해서 다양한 타입을 다양한 타입으로 변환할 수 있게 만들기
    public static List<Integer> convertStringsToIntegers(List<String> strings) {
        List<Integer> integers = new ArrayList<>();

        for (int i = 0; i < strings.size(); i++) {
            integers.add(Integer.valueOf(strings.get(i)));
        }

        return integers;
    }

    /**
     * createMapByTwoList 는 2개의 리스트를 이용해서 Map을 만드는 함수 이다.
     */
    public static <K, V> Map<K, V> convertTwoListToMap(List<K> keys, List<V> values) {
        Map<K, V> map = new ConcurrentHashMap<>();

        for (int i = 0; i < keys.size(); i++) {
            K key = keys.get(i);

            V value = values.get(i);

            map.put(key, value);
        }

        return map;
    }

    public static UUID convertBinaryToUuid(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long mostSignificantBits = buffer.getLong();
        long leastSignificantBits = buffer.getLong();
        return new UUID(mostSignificantBits, leastSignificantBits);
    }

    public static byte[] convertUuidToBinary(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }
}
