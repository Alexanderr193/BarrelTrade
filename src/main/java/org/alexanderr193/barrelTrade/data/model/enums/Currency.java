package org.alexanderr193.barrelTrade.data.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Currency {
    EMERALD, DIAMOND, IRON, COAL, NETHERITE;

    public static List<String> names() {
        return Arrays.stream(values())
                .map(Enum::name)
                .map(String::toLowerCase)
                .sorted()
                .collect(Collectors.toList());
    }
}
