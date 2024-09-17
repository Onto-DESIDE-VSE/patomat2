<script setup lang="ts">
import type { ResultBinding } from "@/types/PatternInstance";
import { computed } from "vue";
import { mdiInformation } from "@mdi/js";

const props = defineProps<{
  binding: ResultBinding;
}>();

function valueToString(binding: ResultBinding) {
  if (binding.datatype === "http://www.w3.org/2000/01/rdf-schema#Resource") {
    return `<${binding.value}>`;
  } else {
    return `${binding.value}^^${binding.datatype}`;
  }
}

const strValue = computed(() => valueToString(props.binding));
</script>

<template>
  <span class="font-weight-bold">{{ props.binding.name }}</span
  >:
  {{ strValue }}
  <v-tooltip v-if="binding.basedOnBlankNode" text="This value is based on a blank node">
    <template v-slot:activator="{ props }">
      <v-icon v-bind="props">{{ mdiInformation }}</v-icon>
    </template>
  </v-tooltip>
</template>
<style scoped></style>
