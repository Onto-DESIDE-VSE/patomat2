<script setup lang="ts">
import type { NewEntity } from "@/types/PatternInstance";
import { computed } from "vue";

const props = defineProps<{
  sparql: string;
  newEntities: NewEntity[];
}>();

/**
 * Splits the SPARQL into lines and individual tokens within each line. If the token corresponds to a new entity identifier,
 * mark its position in the line so that it can be replaced by a span with tooltip when rendering.
 * Lines not containing new entities are again joined into a single token to simplify the rendering.
 */
const toRender = computed(() => {
  const newEntityPositions: any = {};
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
      for (const ne of props.newEntities) {
        if (lineTokens[j] === `<${ne.identifier}>`) {
          newEntityPositions[i] = {};
          newEntityPositions[i][j] = {
            present: true,
            newEntity: ne
          };
        }
      }
    }
    if (i < lines.length - 1) {
      lineTokens.push("\n");
    }
    if (!newEntityPositions[i]) {
      lineTokens = [lineTokens.join(" ")];
    }
    tokens.push(lineTokens);
  }
  return {
    newEntityPositions,
    tokens
  };
});

function newEntityPresent(
  line: number,
  column: number,
  newEntityPositions: { [key: number]: { [key: number]: { present: boolean; newEntity: NewEntity } } }
) {
  return newEntityPositions[line] && newEntityPositions[line][column] && newEntityPositions[line][column].present;
}
</script>

<template>
  <template v-for="(line, lineIndex) in toRender.tokens" :key="lineIndex">
    <template v-for="(token, index) in line" :key="index">
      <template v-if="index > 0">{{ " " }}</template>
      <template v-if="newEntityPresent(lineIndex, index, toRender.newEntityPositions)">
        <v-tooltip :text="toRender.newEntityPositions[lineIndex][index].newEntity.variableName">
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
