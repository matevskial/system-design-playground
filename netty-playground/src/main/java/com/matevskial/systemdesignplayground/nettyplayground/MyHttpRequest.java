package com.matevskial.systemdesignplayground.nettyplayground;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MyHttpRequest {
    String contentType;
    byte[] data;
}
