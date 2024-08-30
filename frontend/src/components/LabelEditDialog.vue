<script setup lang="ts">
import type { EntityLabel } from "@/types/PatternInstance";
import { ref, watch } from "vue";
import Constants from "@/constants/Constants";

const props = defineProps<{
  label: EntityLabel | null;
  onSubmit: (label: EntityLabel) => void;
  onCancel: () => void;
}>();

const internalOpen = ref(props.label !== null);
const label = ref<string | null>(props.label != null ? props.label.value : null);
const property = ref<string | null>(props.label != null ? props.label.property : null);

watch(
  () => props.label,
  (entityLabel) => {
    if (entityLabel !== null) {
      internalOpen.value = true;
      label.value = entityLabel.value;
      property.value = entityLabel.property;
    }
  }
);

function onClose() {
  props.onCancel();
  internalOpen.value = false;
}
function onSave() {
  props.onSubmit({ value: label.value!, property: property.value! });
  internalOpen.value = false;
}
</script>

<template>
  <v-dialog width="1080" v-model="internalOpen" persistent @keydown.esc="onClose()" id="edit-new-entity-label">
    <v-card title="Edit New Entity Label">
      <v-card-text>
        <v-form>
          <v-text-field v-model="label" label="Label"></v-text-field>

          <v-select
            label="Property"
            v-model="property"
            :items="Object.values(Constants.LABEL_TYPES)"
            item-title="propertyPrefixed"
            item-value="property"
          ></v-select>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" @click="onSave()" :disabled="label?.trim().length === 0">Save</v-btn>
        <v-btn @click="onClose()">Cancel</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<style scoped></style>
