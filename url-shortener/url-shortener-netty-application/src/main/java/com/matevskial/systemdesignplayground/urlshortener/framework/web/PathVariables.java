package com.matevskial.systemdesignplayground.urlshortener.framework.web;

import java.util.HashMap;
import java.util.Map;

public class PathVariables {

    private Map<String, String> pathVariables;

    // TODO: maybe replace with more performant implementation instead of using split and substring?
    public PathVariables(String path, String registeredRequestHandlerPath) {
       String[] pathSegments = path.split("/");
       String[] registeredRequestHandlerPathSegments = registeredRequestHandlerPath.split("/");
       for (int i = 0; i < pathSegments.length; i++) {
           if (registeredRequestHandlerPathSegments[i].startsWith("{") && registeredRequestHandlerPathSegments[i].endsWith("}")) {
               if (pathVariables == null) {
                   pathVariables = new HashMap<>();
               }
               int len = registeredRequestHandlerPathSegments[i].length();
               pathVariables.put(registeredRequestHandlerPathSegments[i].substring(1, len - 1), pathSegments[i]);
           }
       }
    }

    public String getPathVariable(String pathVariable) {
        if (pathVariables != null) {
            return pathVariables.get(pathVariable);
        }
        return null;
    }
}
