<script setup lang="ts">
import { RouterLink, RouterView } from "vue-router";
import Constants from "./constants/Constants";
import { mdiFileImport, mdiInformation, mdiTableCog } from "@mdi/js";
import Message from "@/components/Messages.vue";

const navItems = {
  import: {
    title: "Load",
    path: "load",
    icon: mdiFileImport
  },
  patternMatches: {
    title: "Pattern Matches",
    path: "matches",
    icon: mdiTableCog
  },
  about: {
    title: "About",
    path: "about",
    icon: mdiInformation
  }
};
</script>

<template>
  <v-app>
    <v-toolbar app>
      <v-toolbar-title>
        <RouterLink to="/" class="v-btn v-theme--light v-btn--variant-text" style="cursor: pointer">
          {{ Constants.APP_NAME }}
        </RouterLink>
      </v-toolbar-title>
      <v-spacer></v-spacer>
      <v-toolbar-items class="hidden-xs-only">
        <v-btn flat v-for="item in navItems" :key="item.title" :to="item.path">
          <v-icon left dark>{{ item.icon }}</v-icon>
          {{ item.title }}
        </v-btn>
      </v-toolbar-items>
    </v-toolbar>

    <v-main class="main-container">
      <RouterView />
      <Message />
    </v-main>
  </v-app>
</template>

<style scoped>
.main-container {
  padding-left: 15px;
  padding-right: 15px;
  padding-top: 15px;
  margin-bottom: 15px;
}
</style>
