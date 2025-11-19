<script setup lang="ts">
import { onMounted, ref } from "vue";
import { getPredefinedPatterns } from "@/api/PatternApi";
import type { PatternInfo } from "@/types/LoadedTransformationInput";

const props = defineProps<{
  selectedPatterns: string[];
  onPatternsSelected: (patterns: readonly any[]) => void;
}>();

const availablePatterns = ref<PatternInfo[]>([]);

onMounted(async () => {
  availablePatterns.value = await getPredefinedPatterns();
});
</script>

<template>
  <v-select
    clearable
    multiple
    label="Sample patterns"
    :items="availablePatterns"
    item-title="name"
    item-value="url"
    @update:modelValue="props.onPatternsSelected"
    :model-value="props.selectedPatterns"
    hint="Select one of these predefined patterns to use them for transformation"
    persistent-hint
  ></v-select>
</template>
