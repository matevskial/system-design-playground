export class WebRtc {

  #rtcPeerConnection;
  #sdpSignaler;
  #onRemoteStream;

  constructor(sdpSignaler, onAnswer, onRemoteStream) {
    this.#sdpSignaler = sdpSignaler;
    this.#onRemoteStream = onRemoteStream;
    this.#sdpSignaler.onAnswer = onAnswer;
    this.#sdpSignaler.onAnswerReceived = this.onAnswerReceived.bind(this);
    this.#sdpSignaler.onIceCandidateReceived = this.onIceCandidateReceived.bind(this);
  }

  onCall(targetUserId, localStream) {
    this.#rtcPeerConnection = new RTCPeerConnection();

    this.#rtcPeerConnection.onaddstream = (event => {
      if (this.#onRemoteStream) {
        this.#onRemoteStream(event.stream);
      }
    }).bind(this);

    this.#rtcPeerConnection.onicecandidate = (event => {
      const isIceCandidateGatheringReady = !(!!event.candidate);
      if (!isIceCandidateGatheringReady) {
        this.#sdpSignaler.sendIceCandidate(JSON.stringify(event.candidate), targetUserId);
      }
    }).bind(this);

    this.#rtcPeerConnection.addStream(localStream);

    this.#rtcPeerConnection.createOffer().then((offer => {
      this.#rtcPeerConnection.setLocalDescription(offer);
      this.#sdpSignaler.sendOffer(JSON.stringify(offer), targetUserId);
    }).bind(this));
  }

  onAnswer(offer, callerUserId, localStream) {
    if (this.#rtcPeerConnection) {
      console.log("Call in progress");
      return;
    }

    this.#rtcPeerConnection = new RTCPeerConnection();

    this.#rtcPeerConnection.onaddstream = (event => {
      if (this.#onRemoteStream) {
        this.#onRemoteStream(event.stream);
      }
    }).bind(this);

    this.#rtcPeerConnection.onicecandidate = ((event) => {
      const isIceCandidateGatheringReady = !(!!event.candidate);
      if (!isIceCandidateGatheringReady) {
        this.#sdpSignaler.sendIceCandidate(JSON.stringify(event.candidate), callerUserId);
      }
    }).bind(this);

    this.#rtcPeerConnection.addStream(localStream);
    this.#rtcPeerConnection.setRemoteDescription(new RTCSessionDescription(JSON.parse(offer)));
    this.#rtcPeerConnection.createAnswer().then((answer => {
      this.#rtcPeerConnection.setLocalDescription(answer);
      this.#sdpSignaler.sendAnswer(JSON.stringify(answer), callerUserId);
    }).bind(this));
  }

  onAnswerReceived(offer) {
    if (this.#rtcPeerConnection) {
      this.#rtcPeerConnection.setRemoteDescription(new RTCSessionDescription(JSON.parse(offer)));
    }
  }

  onIceCandidateReceived(candidate) {
    if (this.#rtcPeerConnection) {
      this.#rtcPeerConnection.addIceCandidate(new RTCIceCandidate(JSON.parse(candidate)));
    }
  }
}
