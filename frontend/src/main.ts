import { createApp } from "vue"
// Vuetify
import "vuetify/styles"
import { createVuetify } from "vuetify"
import * as components from "vuetify/components"
import * as directives from "vuetify/directives"
import { aliases, mdi } from "vuetify/iconsets/mdi-svg"

import App from "./App.vue"
import router from "./router"
import { createPinia } from "pinia"

const vuetify = createVuetify({
  components,
  directives,
  icons: {
    defaultSet: "mdi",
    aliases,
    sets: {
      mdi
    }
  }
})
const pinia = createPinia()

const app = createApp(App)

app.use(router)
app.use(vuetify)
app.use(pinia)

app.mount("#app")
