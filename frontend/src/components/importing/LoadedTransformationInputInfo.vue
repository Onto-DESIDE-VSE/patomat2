<script setup lang="ts">
import { onMounted, ref } from "vue";
import { mdiDownload } from "@mdi/js";
import type { LoadedTransformationInput } from "@/types/LoadedTransformationInput";
import { downloadOntologyFile, getLoadedInput } from "@/api/OntologyStorageApi";

const loadedData = ref<LoadedTransformationInput | null>(null);

onMounted(async () => {
  loadedData.value = await getLoadedInput();
});
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
      {{ loadedData.ontology }}
    </v-btn>
    |
    <span class="font-weight-bold"> Transformation patterns: </span>
    <span v-for="(pi, index) in loadedData.patterns" :key="pi.fileName">
      <template v-if="index > 0">, </template>
      {{ pi.name }} ({{ pi.fileName }})
    </span>
  </div>
</template>
