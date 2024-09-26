<script setup lang="ts">
import LoadedTransformationInputInfo from "@/components/importing/LoadedTransformationInputInfo.vue";
import TransformationExamples from "@/components/importing/TransformationExamples.vue";
import OntologyImport from "@/components/importing/OntologyImport.vue";
</script>

<template>
  <h3 class="text-h3 mb-6">Load Ontology and Transformation Patterns</h3>
  <LoadedTransformationInputInfo></LoadedTransformationInputInfo>
  <OntologyImport></OntologyImport>
  <TransformationExamples></TransformationExamples>
</template>
