<template>
  <div class="webrtclient">
    <h1>WebRTC client</h1>
    <div v-if="!joined" id="joinForm">
      <div>Join to continue</div>
      <Vueform
        :endpoint="false"
        @submit="onJoinFormSubmit"
        validate-on="''"
        v-bind="joinForm"
      />
    </div>
    <div v-if="joined" id="callArea">
      <div v-if="joined" id="callForm">
        <Vueform
          :endpoint="false"
          @submit="onCallFormSubmit"
          validate-on="''"
          v-bind="callForm"
        />
      </div>
      <Button :disabled="!isCallInProgress" type="danger" text="End call" @click="onEndCall" />
      <div class="video" style="width: 50%; height: 50%; border: 1px solid red">
        <video id="localVideo" ref="localVideo" autoplay width="100%" height="100%" />
      </div>

      <div class="video" style="width: 50%; height: 50%; border: 1px solid red">
        <video id="remoteVideo" ref="remoteVideo" autoplay width="100%" height="100%" />
      </div>
    </div>
  </div>
</template>

<script>

import useVuelidate from '@vuelidate/core';
import Button from '@/components/common/Button.vue';
import { setupUserMediaDevices } from '@/js/usermedia.js';
import WebsocketClient from '@/js/websocketclient.js';
import { SdpSignalerUsingWebsocket } from '@/js/sdpsignaler.js';
import { WebRtc } from '@/js/webrtc.js';

export default {
  name: 'WebRtcClientView',
  components: { Button },
  setup() {
    const host = window.location.hostname;
    const port = 8080;
    const websocketClient = new WebsocketClient({
      url: `ws://${host}:${port}/ws/webrtc`,
      debug: true
    });
    websocketClient.connect();

    return {
      v$: useVuelidate(),
      setupUserMediaDevices,
      websocketClient
    };
  },
  data() {
    return {
      userId: null,
      joined: false,
      isJoinInProgress: false,
      joinTimeoutHandle: null,

      isCallInProgress: false,
    };
  },
  computed: {
    isCallFormDisabled() {
      return this.isCallInProgress;
    },
    isJoinFormDisabled() {
      return this.isJoinInProgress;
    },
    joinForm() {
      return {
        displayErrors: false,
        schema: {
          userId: {
            id: 'userId',
            type: 'text',
            label: 'My User ID',
            placeholder: 'My User ID',
            autocomplete: 'off',
            rules: ['required'],
            disabled: this.isJoinFormDisabled,
          },
          submit: {
            type: 'button',
            submits: true,
            buttonLabel: 'Join',
            full: false,
            disabled: this.isJoinFormDisabled
          }
        }
      }
    },
    callForm() {
      return {
        displayErrors: false,
        schema: {
          targetUserId: {
            id: 'targetUserId',
            type: 'text',
            label: 'Target User ID to call',
            placeholder: 'Target User ID to call',
            autocomplete: 'off',
            rules: ['required'],
            disabled: this.isCallFormDisabled,
          },
          channelName: {
            id: 'channelName',
            type: 'text',
            label: 'Channel name',
            placeholder: 'Channel name',
            autocomplete: 'off',
            disabled: true
          },
          submit: {
            type: 'button',
            submits: true,
            buttonLabel: 'Call',
            full: false,
            disabled: this.isCallFormDisabled
          }
        }
      };
    }
  },
  methods: {
    tryJoin(userId) {
      return this.websocketClient.sendMessage({
        type: "join",
        body: {
          userId: userId
        }
      });
    },
    onJoinFormSubmit(form) {
      this.isJoinInProgress = true;
      const userId = form.data.userId;
      this.userId = userId;
      const isOk = this.tryJoin(userId);
      if (!isOk) {
        this.isJoinInProgress = false;
      } else {
        this.joinTimeoutHandle = setTimeout(() => {
          this.isJoinInProgress = false;
          this.joinTimeoutHandle = null;
        }, 10000);
      }
    },
    onCallFormSubmit(form) {
      console.log("form data ", form.data);
      this.setupUserMediaDevices((stream) => {
        this.$refs.localVideo.srcObject = stream;
        this.isCallInProgress = true;
        this.webRtc.onCall(form.data.targetUserId, stream);
      }, (error) => {
        console.log("Error getting user media: ", error);
      });
    },
    onEndCall() {
      this.isCallInProgress = false;
      this.$refs.localVideo.srcObject = null;
    },
    onAnswer(offer, callerUserId) {
      this.setupUserMediaDevices((stream) => {
        this.$refs.localVideo.srcObject = stream;
        this.isCallInProgress = true;
        this.webRtc.onAnswer(offer, callerUserId, stream);
      }, (error) => {
        console.log("Error getting user media: ", error);
      });
    },
    onWebsocketMessage(message) {
      if (message?.type === "joined") {
        if (this.isJoinInProgress) {
          clearTimeout(this.joinTimeoutHandle);
          this.joinTimeoutHandle = null;
          this.isJoinInProgress = false;
          this.joined = true;
        }
      } else if (message?.type === "error") {
        if (this.isJoinInProgress) {
          clearTimeout(this.joinTimeoutHandle);
          this.joinTimeoutHandle = null;
          this.isJoinInProgress = false;
          this.joined = false;
        }
      }
    },
    autoRejoin() {
      if (this.userId && this.joined) {
        this.tryJoin(this.userId);
      }
    },
    onRemoteStream(stream) {
      console.log("on remote stream? - ", stream)
      this.$refs.remoteVideo.srcObject = stream;
    }
  },
  mounted() {
    this.websocketClient.addMessageHandler(this.onWebsocketMessage);
    this.websocketClient.setOnConnected(this.autoRejoin);
    this.sdpSignaler = new SdpSignalerUsingWebsocket(this.websocketClient);
    this.webRtc = new WebRtc(this.sdpSignaler, this.onAnswer, this.onRemoteStream);
  }
}
</script>

<style>
@media (min-width: 1024px) {
  .webrtclient {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    gap: 5px;
  }
}
</style>
