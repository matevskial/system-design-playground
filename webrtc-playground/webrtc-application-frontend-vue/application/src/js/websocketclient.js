class WebsocketClient {
  constructor(options = {}) {
    // Configuration
    this.url = options.url || "ws://127.0.0.1:8080";
    this.autoReconnect = options.autoReconnect !== false;
    this.reconnectInterval = options.reconnectInterval || 5000;
    this.maxReconnectAttempts = options.maxReconnectAttempts || 10;
    this.debug = options.debug || false;

    // State
    this.socket = null;
    this.enabled = false;
    this.reconnectAttempts = 0;
    this.reconnectTimeout = null;
    this.isAuthenticated = false;

    // Auth
    this.password = options.password || "";

    // Callbacks
    this.onConnected = options.onConnected || (() => {});
    this.onDisconnected = options.onDisconnected || (() => {});
    this.onError = options.onError || (() => {});
    this.onMessage = options.onMessage || (() => {});
    this.onReconnecting = options.onReconnecting || (() => {});
    this.onAuthenticated = options.onAuthenticated || (() => {});
    this.onAuthFailed = options.onAuthFailed || (() => {});

    this.messageHandlers = [];
  }

  log(...args) {
    if (this.debug) {
      console.log("[Websocket]", ...args);
    }
  }

  setOnConnected(onConnected) {
    this.onConnected = onConnected || (() => {});
  }

  connect() {
    if (this.socket && (this.socket.readyState === WebSocket.CONNECTING || this.socket.readyState === WebSocket.OPEN)) {
      this.log("Already connected or connecting");
      return;
    }

    this.enabled = true;
    this.reconnectAttempts = 0;
    this._connect();
  }

  _connect() {
    try {
      this.log(`Connecting to ${this.url}...`);
      this.socket = new WebSocket(this.url);

      this.socket.onopen = this._handleOpen.bind(this);
      this.socket.onclose = this._handleClose.bind(this);
      this.socket.onerror = this._handleError.bind(this);
      this.socket.onmessage = this._handleMessage.bind(this);
    } catch (error) {
      this.log("Connection error:", error);
      this._scheduleReconnect();
    }
  }

  disconnect() {
    this.enabled = false;
    if (this.reconnectTimeout) {
      clearTimeout(this.reconnectTimeout);
      this.reconnectTimeout = null;
    }

    if (this.socket) {
      this.log("Disconnecting...");
      try {
        this.socket.close(1000, "Disconnect requested");
      } catch (error) {
        this.log("Error during disconnect:", error);
      }
      this.socket = null;
      this.isAuthenticated = false;
    }
  }

  addMessageHandler(messageHandler) {
    if (!messageHandler) {
      return;
    }

    this.messageHandlers.push(messageHandler);
  }

  _handleOpen() {
    this.log("Connected to Live Streaming Toolbox WebSocket");
    this.reconnectAttempts = 0;
    this.onConnected();
  }

  async _handleMessage(event) {
    try {
      const data = JSON.parse(event.data);
      for (let handler of this.messageHandlers) {
        handler(data);
      }
    } catch (error) {
      this.log("Error processing message:", error, event.data);
    }
  }

  _handleClose(event) {
    this.isAuthenticated = false;
    this.log(`WebSocket closed: ${event.code} ${event.reason}`);
    this.onDisconnected(event);

    if (this.enabled && this.autoReconnect) {
      this._scheduleReconnect();
    }
  }

  _handleError(error) {
    this.log("WebSocket error:", error);
  }

  _scheduleReconnect() {
    if (!this.enabled || !this.autoReconnect) return;

    this.reconnectAttempts++;

    const delay = this.reconnectInterval;
    this.log(`Scheduling reconnect attempt ${this.reconnectAttempts} in ${delay}ms`);
    this.onReconnecting(this.reconnectAttempts);

    this.reconnectTimeout = setTimeout(() => {
      this.reconnectTimeout = null;
      this._connect();
    }, delay);
  }

  _sendMessage(message) {
    if (!this.socket || this.socket.readyState !== WebSocket.OPEN) {
      if (this.enabled && (!this.socket || this.socket.readyState !== WebSocket.CONNECTING)) {
        this._connect();
      }
      return false;
    }

    try {
      const payload = typeof message === "string" ? message : JSON.stringify(message);
      this.log("Sending message:", message);
      this.socket.send(payload);
      return true;
    } catch (error) {
      this.log("Error sending message:", error);
      return false;
    }
  }

  sendMessage(message) {
    try {
      return this._sendMessage(message);
    } catch (error) {
      this.log("Error sending to live streaming toolbox websocket:", error);
      return false;
    }
  }
}

export default WebsocketClient;
