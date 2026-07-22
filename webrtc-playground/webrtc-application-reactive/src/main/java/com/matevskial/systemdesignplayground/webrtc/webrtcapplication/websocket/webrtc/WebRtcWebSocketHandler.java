package com.matevskial.systemdesignplayground.webrtc.webrtcapplication.websocket.webrtc;

import com.matevskial.systemdesignplayground.webrtc.webrtcapplication.sdp.SdpMessageType;
import com.matevskial.systemdesignplayground.webrtc.webrtcapplication.websocket.common.WebSocketInternalErrorMessage;
import com.matevskial.systemdesignplayground.webrtc.webrtcapplication.websocket.common.WebSocketMessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
class WebRtcWebSocketHandler implements WebSocketHandler {

    private final Map<String, Sinks.Many<com.matevskial.systemdesignplayground.webrtc.webrtcapplication.websocket.common.WebSocketMessage>> sinks = new ConcurrentHashMap<>();
    private final Map<String, String> userIdToSessionId = new ConcurrentHashMap<>();
    private final Map<String, String> sessionIdToUserId = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        WebSocketMessageConverter converter = WebSocketMessageConverter.build(session);

        Sinks.Many<com.matevskial.systemdesignplayground.webrtc.webrtcapplication.websocket.common.WebSocketMessage> mySink = Sinks.many().multicast().onBackpressureBuffer();
        sinks.put(session.getId(), mySink);

        Flux<WebSocketMessage> responses = session
                .receive()
                .map((webSocketMessage) -> converter.convertTextPayload(webSocketMessage, WebRtcMessageRequest.class))
                .map(request -> {
                    if (request instanceof WebSocketInternalErrorMessage) {
                        return request;
                    } else if (request instanceof WebRtcMessageRequest webRtcMessageRequest) {
                        try {
                            if (webRtcMessageRequest.isJoin()) {
                                return handleJoin(webRtcMessageRequest, session);
                            } else if (webRtcMessageRequest.isSdp()) {
                                return handleSdp(webRtcMessageRequest, session);
                            } else {
                                return WebRtcMessageResponse.error("unsupported command");
                            }
                        } catch (Exception e) {
                            return WebRtcMessageResponse.error(e.getMessage());
                        }
                    } else {
                        return WebRtcMessageResponse.error("unsupported message");
                    }
                })
                .doOnNext(webRtcMessageResponse -> {
                    if (webRtcMessageResponse instanceof WebSocketInternalErrorMessage internalError) {
                        log.error("WebRtcWebSocketHandler internal error:", internalError);
                    }
                })
                .map(converter::convert);

        return session
                .send(mySink.asFlux().map(converter::convert).mergeWith(responses))
                .doFinally(signalType -> cleanWebsocketConnection(session));
    }

    private WebRtcMessageResponse handleSdp(WebRtcMessageRequest request, WebSocketSession session) {
        String thisUser = sessionIdToUserId.get(session.getId());
        if (request.isSdp(SdpMessageType.SEND_SDP_OFFER)) {
            WebRtcMessageRequest requestToUser = WebRtcMessageRequest.sendSdpOffer(thisUser, request.body().sdp());
            return sendToUser(request.body().targetUserId(), requestToUser);
        } else if (request.isSdp(SdpMessageType.SEND_SDP_ANSWER_OFFER)) {
            WebRtcMessageRequest requestToUser = WebRtcMessageRequest.sendSdpAnswerOffer(request.body().sdp());
            return sendToUser(request.body().callerUserId(), requestToUser);
        } else if (request.isSdp(SdpMessageType.SEND_SDP_ICE_CANDIDATE)) {
            WebRtcMessageRequest requestToUser = WebRtcMessageRequest.sendSdpIceCandidate(request.body().sdp());
            return sendToUser(request.body().targetUserId(), requestToUser);
        } else {
            return WebRtcMessageResponse.error("unsupported sdp command");
        }
    }

    private void cleanWebsocketConnection(WebSocketSession session) {
        try {
            String userId = sessionIdToUserId.get(session.getId());
            sessionIdToUserId.remove(session.getId());
            userIdToSessionId.remove(userId);
            sinks.remove(session.getId());
        } catch (Exception e) {
            // do nothing
        }
    }

    private WebRtcMessageResponse sendToUser(String targetUserId, WebRtcMessageRequest request) {
        String otherUserId = targetUserId;
        String otherUserSessionId = userIdToSessionId.get(otherUserId);
        Sinks.Many<com.matevskial.systemdesignplayground.webrtc.webrtcapplication.websocket.common.WebSocketMessage> otherUserSink = sinks.get(otherUserSessionId);
        otherUserSink.tryEmitNext(request);
        return WebRtcMessageResponse.successAck();
    }

    private WebRtcMessageResponse handleJoin(WebRtcMessageRequest request, WebSocketSession session) {
        String userId = request.body().userId();
        userIdToSessionId.put(userId, session.getId());
        sessionIdToUserId.put(session.getId(), userId);

        for (Map.Entry<String, String> entry : userIdToSessionId.entrySet()) {
            System.out.println("userId: " + entry.getKey());
        }

        return WebRtcMessageResponse.joined();
    }
}
