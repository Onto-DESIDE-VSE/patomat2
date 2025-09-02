<script setup lang="ts">
import type { ResultBinding } from "@/types/PatternInstance";
import { computed } from "vue";
import { mdiInformation } from "@mdi/js";
import { splitBindingParts } from "@/util/Utils";
import { getBindingColor } from "@/util/BindingColors";

const props = defineProps<{
  binding: ResultBinding;
}>();

const bindingParts = computed(() => splitBindingParts(props.binding));
const bindingColor = computed(() => getBindingColor(props.binding.name));
</script>

<template>
  <v-row align="center" class="text-grey-darken-3" no-gutters>
    <span :class="['font-weight-bold mr-1', bindingColor]">{{ props.binding.name }}:</span>

    <span>
      <span>{{ bindingParts.prefix }}{{ bindingParts.base }}</span>
      <strong :class="bindingColor">{{ bindingParts.localName }}</strong>
      <span>{{ bindingParts.suffix }}</span>
    </span>
    <v-tooltip v-if="binding.basedOnBlankNode" text="This value is based on a blank node">
      <template v-slot:activator="{ props }">
        <v-icon :class="['ml-1', bindingColor]" v-bind="props">{{ mdiInformation }}</v-icon>
      </template>
    </v-tooltip>
  </v-row>
</template>
