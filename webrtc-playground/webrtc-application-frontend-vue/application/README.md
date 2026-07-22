# webrtc-application-frontend-vue


## Project Setup for local development 

### Note on IntelliJ

Configure which version of node and npm to use in settings > Language and frameworks > Node.js

Tested with Node v24.15.0

Should work with newer versions too.

### Set up

```sh
npm ci
```

### Compile and Hot-Reload for Development

```sh
npm run dev
```

### Compile and Minify for Production

```sh
npm run build
```

### Lint with [ESLint](https://eslint.org/)

```sh
npm run lint
```

# Vue notes

- for vueform, follow official guide, and don't forget to add the css import in base.css(or your main css file)(example at the time of writing)
```scss
@import '@vueform/vueform/dist/vueform.css';
```
