package com.matevskial.systemdesignplayground.urlshortener.framework.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryParameters {

    private static final QueryParameterValue DEFAULT_BOOLEAN_VALUE = QueryParameterValue.booleanValue(false);

    private Map<String, QueryParameterValue> queryParameters;

    // TODO: maybe replace with more performant implementation instead of using split and substring?
    /**
     * Even though netty cuts the part that begins with # from the url, I still treat path as if
     * the part that begins with # was not cut. We do this  just so we don't rely on netty's behavior for it
     */
    public QueryParameters(String path) {
        int firstSharpIndex = -1;
        int firstQuestionMarkIndex = -1;
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == '?' && firstQuestionMarkIndex == -1) {
                firstQuestionMarkIndex = i;
            }
            if (path.charAt(i) == '#' && firstSharpIndex == -1) {
                firstSharpIndex = i;
            }
        }

        int endOfQueryParametersSubstring = firstSharpIndex != -1
                ? firstSharpIndex
                : path.length();

        if (firstQuestionMarkIndex != -1 && firstQuestionMarkIndex < endOfQueryParametersSubstring - 1) {
            String queryParametersStr = path.substring(firstQuestionMarkIndex + 1, endOfQueryParametersSubstring);
            String[] queryParametersRaw = queryParametersStr.split("&");
            if (queryParametersRaw.length > 0) {
                queryParameters = new HashMap<>();
                for (String queryParameter : queryParametersRaw) {
                    String[] keyValueParts = queryParameter.split("=");
                    if (keyValueParts.length == 2) {
                        queryParameters.put(keyValueParts[0], QueryParameterValue.stringValue(keyValueParts[1]));
                    } else if (keyValueParts.length == 1) {
                        queryParameters.put(keyValueParts[0], QueryParameterValue.booleanValue(true));
                    }
                }
            }
        }
    }

    public QueryParameterValue getSingleValue(String key) {
        if (queryParameters != null) {
            return queryParameters.getOrDefault(key, DEFAULT_BOOLEAN_VALUE);
        }
        return DEFAULT_BOOLEAN_VALUE;
    }

    // TODO: make correct implementation.
    //  https://stackoverflow.com/questions/6243051/how-to-pass-an-array-within-a-query-string
    public List<QueryParameterValue> getValues(String key) {
        if (queryParameters != null) {
            return List.of(queryParameters.getOrDefault(key, DEFAULT_BOOLEAN_VALUE));
        }
        return List.of(DEFAULT_BOOLEAN_VALUE);
    }
}
