<script setup lang="ts">
import type { PatternInstance } from "@/types/PatternInstance";
import { computed } from "vue";
import type { PatternMatchStatistics } from "@/types/PatternMatchStatistics";

const props = defineProps<{
  matches: PatternInstance[];
}>();

const stats = computed(() => {
  const result: PatternMatchStatistics = {
    totalMatchCount: 0,
    matchCount: new Map<string, number>()
  };

  for (const match of props.matches) {
    result.totalMatchCount++;
    result.matchCount.set(
      match.patternName,
      result.matchCount.has(match.patternName) ? result.matchCount.get(match.patternName)! + 1 : 1
    );
  }
  return result;
});
</script>

<template>
  <v-expansion-panels class="mb-3">
    <v-expansion-panel>
      <v-expansion-panel-title class="font-weight-bold">Statistics</v-expansion-panel-title>
      <v-expansion-panel-text>
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
