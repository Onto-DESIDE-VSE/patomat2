<script setup lang="ts">
import type { ResultBinding } from "@/types/PatternInstance";
import { computed } from "vue";
import { mdiInformation } from "@mdi/js";
import { valueToString } from "@/util/Utils";

const props = defineProps<{
  binding: ResultBinding;
}>();

const strValue = computed(() => valueToString(props.binding));
</script>

<template>
  <v-row align="center" no-gutters>
    <span class="font-weight-bold">{{ props.binding.name }}</span
    >:
    {{ strValue }}
    <v-tooltip v-if="binding.basedOnBlankNode" text="This value is based on a blank node">
      <template v-slot:activator="{ props }">
        <v-icon v-bind="props">{{ mdiInformation }}</v-icon>
      </template>
    </v-tooltip>
  </v-row>
</template>
<style scoped></style>
