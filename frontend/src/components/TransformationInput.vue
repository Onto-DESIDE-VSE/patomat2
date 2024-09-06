<script setup lang="ts">
import { ref } from "vue";
import OntologyImportFiles from "@/components/OntologyImportFiles.vue";
import OntologyImportUrls from "@/components/OntologyImportUrls.vue";

const activeTab = ref<"files" | "urls">("files");
</script>

<template>
  <v-tabs v-model="activeTab" grow>
    <v-tab value="files">Load from files</v-tab>
    <v-tab value="urls">Load from URLs</v-tab>
  </v-tabs>
  <v-tabs-window v-model="activeTab">
    <v-tabs-window-item value="files">
      <OntologyImportFiles></OntologyImportFiles>
    </v-tabs-window-item>
    <v-tabs-window-item value="urls">
      <OntologyImportUrls></OntologyImportUrls>
    </v-tabs-window-item>
  </v-tabs-window>
</template>

<style scoped>
.v-window {
  padding-top: 1rem;
}
</style>
