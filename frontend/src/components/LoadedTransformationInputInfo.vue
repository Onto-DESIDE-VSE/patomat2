<script setup lang="ts">
import { ref } from "vue";
import { mdiDownload } from "@mdi/js";
import Constants from "../constants/Constants";
import { LoadedTransformationInput } from "../types/LoadedTransformationInput";
import { downloadAttachment } from "../util/Utils";

const loadedData = ref<LoadedTransformationInput>(null);
const getLoadedInput = async () => {
  const resp = await fetch(`${Constants.SERVER_URL}/ontology`, {
    method: "GET",
    credentials: "include"
  });
  if (resp.ok) {
    loadedData.value = await resp.json();
  }
};
getLoadedInput();

const downloadOntologyFile = async () => {
  const resp = await fetch(`${Constants.SERVER_URL}/ontology/content`, {
    method: "GET",
    credentials: "include"
  });
  downloadAttachment(resp);
};
</script>

<template>
  <div v-if="loadedData" class="mb-3">
    <p class="text-h6 mb-3">Ontology and patterns already loaded. Uploading new ones will replace the old ones.</p>
    <RouterLink to="/matches">
      <v-btn color="primary" variant="tonal">Go to Pattern Matches</v-btn>
    </RouterLink>
    |
    <v-btn @click="downloadOntologyFile" color="primary" variant="tonal" title="Download the ontology file">
      <v-icon left>{{ mdiDownload }}</v-icon>
      {{ loadedData.ontologyFile }}
    </v-btn>
    |
    <span class="font-weight-bold"> Transformation pattern files: </span>
    <span v-for="(file, index) in loadedData.patternFiles" :key="file">
      <template v-if="index > 0">, </template>
      {{ file }}
    </span>
  </div>
</template>

<style scoped></style>
