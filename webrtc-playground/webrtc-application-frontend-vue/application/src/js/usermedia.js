export function setupUserMediaDevices(onStream, onError, options = {}) {

  // the commented code below exists for reference purposes
  // const userMediaOptionsWithConstraints = {
  //   video: {
  //     width: { ideal: 1280 },
  //     height: { ideal: 720 },
  //     frameRate: { ideal: 30 },
  //   },
  //   audio: true,
  // };

  const userMediaOptions = {
    audio: options.audio || true,
    video: options.video || true
  };
  window.navigator.mediaDevices.getUserMedia(userMediaOptions).then(stream => {
    if (onStream) {
      onStream(stream);
    }
  }).catch(err => {
    if (onError) {
      onError(err)
    }
  });
}
