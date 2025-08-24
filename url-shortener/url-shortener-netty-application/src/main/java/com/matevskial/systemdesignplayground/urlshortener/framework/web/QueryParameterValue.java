package com.matevskial.systemdesignplayground.urlshortener.framework.web;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum QueryParameterValue {

    STRING_VALUE(null, null),
    BOOLEAN_VALUE(null, null);

    private Boolean booleanValue;
    private String stringValue;

    public static QueryParameterValue booleanValue(boolean booleanValue) {
        QueryParameterValue v = BOOLEAN_VALUE;
        v.booleanValue = booleanValue;
        return v;
    }

    public static QueryParameterValue stringValue(String stringValue) {
        QueryParameterValue v = STRING_VALUE;
        v.stringValue = stringValue;
        return v;
    }

    public boolean getBooleanValue() {
        if (this != BOOLEAN_VALUE) {
            throw new IllegalStateException("QueryParameterValue is not boolean value");
        }
        return booleanValue;
    }

    public String getStringValue() {
        if (this != STRING_VALUE) {
            throw new IllegalStateException("QueryParameterValue is not string value");
        }
        return stringValue;
    }
}
