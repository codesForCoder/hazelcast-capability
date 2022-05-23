package com.aniket.movie.constant;

import java.util.Arrays;

public enum  DataSource {
    DB("DB"),LOCAL_CACHE("LOCAL_CACHE") ,REMOTE_CACHE("REMOTE_CACHE") ;

    public String getValue() {
        return value;
    }

    private String value;

    private DataSource(String value) {
        this.value = value;
    }

    public static DataSource fromValue(String value) {
        for (DataSource category : values()) {
            if (category.value.equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException(
                "Unknown enum type " + value + ", Allowed values are " + Arrays.toString(values()));
    }
}
