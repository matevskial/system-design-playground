package com.matevskial.systemdesignplayground.urlshortener.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UrlShorteners {

    public static UrlShortener base62Variant() {
        return new Base62UrlShortener();
    }
}
