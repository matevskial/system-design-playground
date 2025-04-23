package com.matevskial.systemdesignplayground.urlshortener.core;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Base62UrlShortener implements UrlShortener {

    private static final String BASE_62_DIGITS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final IdGenerator idGenerator;

    @Override
    public String shorten(String url) {
        long id = idGenerator.generateId();
        return convertToBase62(id);
    }

    private String convertToBase62(long number) {
        if (number < 0) {
            return "0";
        }
        StringBuilder result = new StringBuilder();
        long current = number;
        do {
            result.append(BASE_62_DIGITS.charAt((int) (current % 62)));
            current /= 62;
        } while (current > 0);
        System.out.println("result: " + result);
        return result.reverse().toString();
    }
}
