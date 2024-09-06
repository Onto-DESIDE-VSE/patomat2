<script setup lang="ts">
import { ref } from "vue";
import { mdiCheckboxBlankOutline, mdiCheckboxOutline, mdiPencil } from "@mdi/js";
import type { EntityLabel, NewEntity } from "@/types/PatternInstance";
import LabelEditDialog from "@/components/match/LabelEditDialog.vue";
import Constants from "@/constants/Constants";

declare type LabelProperties = keyof typeof Constants.LABEL_TYPES;

const props = defineProps<{
  patternInstanceId: number;
  entity: NewEntity;
  onSave: (patternInstanceId: number, entity: NewEntity) => void;
}>();

const editedLabel = ref<EntityLabel | null>(null);
const editing = ref(-1);

function cancelEdit() {
  editedLabel.value = null;
  editing.value = -1;
}

function save(newValue: EntityLabel) {
  const labels = props.entity.labels.slice();
  if (newValue.property === Constants.SKOS_PREF_LABEL) {
    labels.forEach((l) => (l.property = Constants.SKOS_ALT_LABEL));
  }
  labels[editing.value].value = newValue.value;
  labels[editing.value].property = newValue.property;
  labels[editing.value].apply = newValue.apply;
  const update = Object.assign({}, props.entity, { labels });
  props.onSave(props.patternInstanceId, update);
  editing.value = -1;
  editedLabel.value = null;
}

function onEdit(index: number) {
  editedLabel.value = props.entity.labels[index];
  editing.value = index;
}

function onToggleApply(index: number) {
  const labels = props.entity.labels.slice();
  labels[index].apply = !props.entity.labels[index].apply;
  const update = Object.assign({}, props.entity, { labels });
  props.onSave(props.patternInstanceId, update);
}
</script>

<template>
  <LabelEditDialog :label="editedLabel" :on-cancel="cancelEdit" :on-submit="save"></LabelEditDialog>
  <div v-for="(label, index) in props.entity.labels" :key="label.value">
    <span class="editable-label">
      <v-tooltip :text="label.apply ? 'This label will be applied' : 'This label will not be applied'">
        <template v-slot:activator="{ props }">
          <v-btn variant="text" size="x-small" icon="true" v-bind="props" @click="onToggleApply(index)">
            <v-icon>{{ label.apply ? mdiCheckboxOutline : mdiCheckboxBlankOutline }}</v-icon>
          </v-btn>
        </template>
      </v-tooltip>
      <v-tooltip :text="Constants.LABEL_TYPES[label.property as LabelProperties].propertyPrefixed">
        <template v-slot:activator="{ props }">
          <v-icon v-bind="props">{{ Constants.LABEL_TYPES[label.property as LabelProperties].icon }}</v-icon>
        </template>
      </v-tooltip>
      {{ label.value }}
      <v-btn variant="tonal" size="x-small" icon="true" @click="onEdit(index)">
        <v-icon>{{ mdiPencil }}</v-icon>
      </v-btn>
    </span>
  </div>
</template>

<style scoped>
.editable-label {
  display: inline-block;
  padding-top: 8px;
  min-height: 42px;
}
</style>
