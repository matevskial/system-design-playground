package com.matevskial.systemdesignplayground.nettyplayground;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MyHttpResponse {

    int statusCode;
    byte[] body;
    String contentType;
}
