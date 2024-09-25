<script setup lang="ts">
import type { ResultBinding } from "@/types/PatternInstance";
import { computed } from "vue";

const props = defineProps<{
  sparql: string;
  bindings: ResultBinding[];
}>();

/**
 * Splits the SPARQL into lines and individual tokens within each line. If the token corresponds to a new entity identifier,
 * mark its position in the line so that it can be replaced by a span with tooltip when rendering.
 * Lines not containing new entities are again joined into a single token to simplify the rendering.
 */
const toRender = computed(() => {
  const bindingPositions: any = {};
  const tokens = [];
  const lines = props.sparql.split("\n");
  for (let i = 0; i < lines.length; i++) {
    let leadingSpacesCount = 0;
    while (lines[i].charAt(leadingSpacesCount) === " ") {
      leadingSpacesCount++;
    }
    lines[i] = lines[i].trim();
    let lineTokens = lines[i].split(" ");
    if (leadingSpacesCount > 0) {
      lineTokens.unshift(" ".repeat(leadingSpacesCount - 1));
    }
    for (let j = 0; j < lineTokens.length; j++) {
      for (const binding of props.bindings) {
        if (lineTokens[j] === `<${binding.value}>`) {
          bindingPositions[i] = {};
          bindingPositions[i][j] = {
            present: true,
            binding
          };
        }
      }
    }
    if (i < lines.length - 1) {
      lineTokens.push("\n");
    }
    if (!bindingPositions[i]) {
      lineTokens = [lineTokens.join(" ")];
    }
    tokens.push(lineTokens);
  }
  return {
    bindingPositions,
    tokens
  };
});

function bindingPresent(
  line: number,
  column: number,
  bindingPositions: { [key: number]: { [key: number]: { present: boolean; binding: ResultBinding } } }
) {
  return bindingPositions[line] && bindingPositions[line][column] && bindingPositions[line][column].present;
}
</script>

<template>
  <template v-for="(line, lineIndex) in toRender.tokens" :key="lineIndex">
    <template v-for="(token, index) in line" :key="index">
      <template v-if="index > 0">{{ " " }}</template>
      <template v-if="bindingPresent(lineIndex, index, toRender.bindingPositions)">
        <v-tooltip :text="toRender.bindingPositions[lineIndex][index].binding.name">
          <template v-slot:activator="{ props }">
            <span class="font-weight-bold" v-bind="props">{{ token }}</span>
          </template>
        </v-tooltip>
      </template>
      <template v-else>
        {{ token }}
      </template>
    </template>
  </template>
</template>

<style scoped></style>
