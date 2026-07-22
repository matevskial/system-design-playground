export class SdpSignalerUsingWebsocket {

  #websocketClient;

  onAnswer;
  onAnswerReceived;
  onIceCandidateReceived;

  constructor(websocketClient) {
    this.#websocketClient = websocketClient;
    this.#websocketClient.addMessageHandler(this.onMessage.bind(this));
  }

  sendOffer(offer, targetUserId) {
    const message = {
      type: 'send_sdp_offer',
      body: {
        sdp: offer,
        targetUserId: targetUserId,
      }
    };
    this.#websocketClient.sendMessage(message);
  }

  sendIceCandidate(candidate, targetUserId) {
    const message = {
      type: 'send_sdp_ice_candidate',
      body: {
        sdp: candidate,
        targetUserId: targetUserId,
      }
    };
    this.#websocketClient.sendMessage(message);
  }

  sendAnswer(answerOffer, callerUserId) {
    const message = {
      type: 'send_sdp_answer_offer',
      body: {
        sdp: answerOffer,
        callerUserId: callerUserId
      }
    };
    this.#websocketClient.sendMessage(message);
  }

  onMessage(message) {
    if (message.type === 'receive_sdp_offer') {
      if (this.onAnswer) {
        this.onAnswer(message.body.sdp, message.body.callerUserId);
      }
    } else if (message.type === 'receive_sdp_answer_offer') {
      if (this.onAnswerReceived) {
        this.onAnswerReceived(message.body.sdp);
      }
    } else if (message.type === 'receive_sdp_ice_candidate') {
      if (this.onIceCandidateReceived) {
        this.onIceCandidateReceived(message.body.sdp);
      }
    }
  }
}
