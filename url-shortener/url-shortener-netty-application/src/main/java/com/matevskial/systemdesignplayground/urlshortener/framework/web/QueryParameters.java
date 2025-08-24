package com.matevskial.systemdesignplayground.urlshortener.framework.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryParameters {

    private static final QueryParameterValue DEFAULT_BOOLEAN_VALUE = QueryParameterValue.booleanValue(false);

    private Map<String, QueryParameterValue> queryParameters;

    // TODO: maybe replace with more performant implementation instead of using split and substring?
    public QueryParameters(String path) {
        int beginIndexOfQueryParameters = path.indexOf('?');
        if (beginIndexOfQueryParameters != -1 && beginIndexOfQueryParameters < path.length() - 1) {
            String queryParametersStr = path.substring(beginIndexOfQueryParameters + 1);
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
