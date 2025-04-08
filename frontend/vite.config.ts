import { fileURLToPath, URL } from "node:url";

import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import vueDevTools from "vite-plugin-vue-devtools";
import Markdown from "unplugin-vue-markdown/vite";
import { VitePWA } from "vite-plugin-pwa";

// https://vitejs.dev/config/
export default defineConfig({
  base: "./",
  plugins: [
    vue({
      include: [/\.vue$/, /\.md$/] // <-- allows Vue to compile Markdown files
    }),
    vueDevTools(),
    Markdown({}),
    VitePWA({
      registerType: "autoUpdate",
      manifestFilename: "manifest.json",
      manifest: {
        name: "PatOMat2",
        short_name: "PatOMat2",
        display: "browser",
        background_color: "#ffffff",
        theme_color: "#000000",
        icons: [
          {
            src: "assets/favicon/favicon-32x32.png",
            sizes: "32x32",
            type: "image/png"
          },
          {
            src: "assets/favicon/favicon-96x96.png",
            sizes: "96x96",
            type: "image/png"
          },
          {
            src: "assets/favicon/favicon-144x144.png",
            sizes: "144x144",
            type: "image/png"
          },
          {
            src: "assets/favicon/favicon-192x192.png",
            sizes: "192x192",
            type: "image/png"
          }
        ]
      }
    })
  ],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url))
    }
  }
});
