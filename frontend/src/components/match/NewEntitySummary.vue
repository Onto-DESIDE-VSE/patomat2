<script setup lang="ts">
import type { NewEntity } from "@/types/PatternInstance";
import { computed } from "vue";

const props = defineProps<{ entity: NewEntity }>();

const labels = computed(() => props.entity.labels.map((l) => l.value).join(" | "));
</script>

<template>
  <span class="font-weight-bold">&lt;{{ props.entity.identifier }}&gt;</span> ({{ labels }})
</template>
