<script setup lang="ts">
import type { PatternInstance } from "@/types/PatternInstance";
import { computed } from "vue";
import type { PatternMatchStatistics } from "@/types/PatternMatchStatistics";
import type { LoadedTransformationInput } from "@/types/LoadedTransformationInput";
import { downloadOntologyFile } from "@/api/OntologyStorageApi";
import { mdiDownload } from "@mdi/js";

const props = defineProps<{
  matches: PatternInstance[];
  transformationInput: LoadedTransformationInput;
}>();

const stats = computed(() => {
  const result: PatternMatchStatistics = {
    totalMatchCount: 0,
    matchCount: new Map<string, number>()
  };
  props.transformationInput.patterns.forEach((pattern) => {
    result.matchCount.set(pattern.name, 0);
  });

  for (const match of props.matches) {
    result.totalMatchCount++;
    result.matchCount.set(match.patternName, result.matchCount.get(match.patternName)! + 1);
  }
  return result;
});
</script>

<template>
  <v-expansion-panels class="mb-3">
    <v-expansion-panel>
      <v-expansion-panel-title class="font-weight-bold">Statistics</v-expansion-panel-title>
      <v-expansion-panel-text>
        <div class="font-weight-bold">
          Ontology:
          <v-btn
            @click="downloadOntologyFile"
            color="primary"
            variant="tonal"
            title="Download the ontology file"
            class="ml-1"
          >
            <v-icon left>{{ mdiDownload }}</v-icon>
            {{ transformationInput.ontology }}
          </v-btn>
        </div>
        <v-table>
          <thead>
            <tr>
              <th>Pattern</th>
              <th># of matches</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="[pattern, count] in stats.matchCount" :key="pattern">
              <td>{{ pattern }}</td>
              <td>{{ count }}</td>
            </tr>
            <tr>
              <td class="font-weight-bold">All</td>
              <td class="font-weight-bold">{{ stats.totalMatchCount }}</td>
            </tr>
          </tbody>
        </v-table>
      </v-expansion-panel-text>
    </v-expansion-panel>
  </v-expansion-panels>
</template>
