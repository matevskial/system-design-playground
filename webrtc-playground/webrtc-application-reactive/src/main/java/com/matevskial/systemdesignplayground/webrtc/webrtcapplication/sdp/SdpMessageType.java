package com.matevskial.systemdesignplayground.webrtc.webrtcapplication.sdp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SdpMessageType {
    SEND_SDP_OFFER("send_sdp_offer"),
    SEND_SDP_ANSWER_OFFER("send_sdp_answer_offer"),
    SEND_SDP_ICE_CANDIDATE("send_sdp_ice_candidate");

    private final String value;
}
