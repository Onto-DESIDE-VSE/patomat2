<script setup lang="ts">
import type { ResultBinding } from "@/types/PatternInstance";
import { computed } from "vue";
import { mdiInformation, mdiLabel } from "@mdi/js";
import { splitBindingParts } from "@/util/Utils";
import { getBindingColor } from "@/util/BindingColors";

const props = defineProps<{
  binding: ResultBinding;
  tooltipWithLabel?: boolean;
}>();

const bindingParts = computed(() => splitBindingParts(props.binding));
const bindingColor = computed(() => getBindingColor(props.binding.name));
const bindingLabel = computed(() => (props.binding.label ? props.binding.label.trim() : ""));
const labelTooltipDisabled = computed(() => !props.tooltipWithLabel || bindingLabel.value === "");
</script>

<template>
  <v-row align="center" class="text-grey-darken-3" no-gutters>
    <span :class="['font-weight-bold mr-1', bindingColor]">{{ props.binding.name }}:</span>

    <v-tooltip :disabled="labelTooltipDisabled">
      <template v-slot:activator="{ props: activatorProps }">
        <span v-bind="activatorProps" class="text-decoration-hover-underline">
          <span>{{ bindingParts.prefix }}{{ bindingParts.base }}</span>
          <strong :class="bindingColor">{{ bindingParts.localName }}</strong>
          <span>{{ bindingParts.suffix }}</span>
          <v-icon v-if="!labelTooltipDisabled" size="small" :class="['ml-1 mb-1', bindingColor]" v-bind="props">{{
            mdiLabel
          }}</v-icon>
        </span>
      </template>

      <span class="d-flex align-center">
        <v-icon size="small" :class="['mr-1', bindingColor]">{{ mdiLabel }}</v-icon>
        {{ bindingLabel }}
      </span>
    </v-tooltip>
    <v-tooltip v-if="binding.basedOnBlankNode" text="This value is based on a blank node">
      <template v-slot:activator="{ props }">
        <v-icon :class="['ml-1', bindingColor]" v-bind="props">{{ mdiInformation }}</v-icon>
      </template>
    </v-tooltip>
  </v-row>
</template>
