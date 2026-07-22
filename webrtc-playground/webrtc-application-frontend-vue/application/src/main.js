import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import Vueform from '@vueform/vueform';
import vueformConfig from '@/vueform.config'

const app = createApp(App)

/* Global component registrations */

/* note: it is needed to set up vueform like this because otherwise using vueform component fails
   with error `TypeError: can't define property "emits": Object is not extensible`
*/
app.use(Vueform, vueformConfig);

app.use(router)
app.mount('#app')
