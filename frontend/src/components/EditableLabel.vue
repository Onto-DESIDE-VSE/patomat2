<script setup lang="ts">
import { ref } from "vue";
import { mdiPencil, mdiCancel, mdiContentSave, mdiLabelOutline, mdiLabel, mdiLabelMultiple } from "@mdi/js";
import type { NewEntity } from "@/types/PatternInstance";

const LABEL_TYPES = {
  "http://www.w3.org/2000/01/rdf-schema#label": {
    property: "http://www.w3.org/2000/01/rdf-schema#label",
    propertyPrefixed: "rdfs:label",
    icon: mdiLabelOutline
  },
  "http://www.w3.org/2004/02/skos/core#prefLabel": {
    property: "http://www.w3.org/2004/02/skos/core#prefLabel",
    propertyPrefixed: "skos:prefLabel",
    icon: mdiLabel
  },
  "http://www.w3.org/2004/02/skos/core#altLabel": {
    property: "http://www.w3.org/2004/02/skos/core#altLabel",
    propertyPrefixed: "skos:altLabel",
    icon: mdiLabelMultiple
  }
};

const props = defineProps<{
  patternInstanceId: number;
  entity: NewEntity;
  onSave: (patternInstanceId: number, entity: NewEntity) => void;
}>();

const editedLabel = ref("");
const editing = ref(-1);

function cancelEdit() {
  editedLabel.value = "";
  editing.value = -1;
}

function save() {
  const labels = props.entity.labels.slice();
  labels[editing.value].value = editedLabel.value;
  const update = Object.assign({}, props.entity, { labels });
  props.onSave(props.patternInstanceId, update);
  editing.value = -1;
}

function onEdit(index: number) {
  editedLabel.value = props.entity.labels[index].value;
  editing.value = index;
}
</script>

<template>
  <div v-for="(label, index) in props.entity.labels" :key="label.value">
    <template v-if="editing === index">
      <v-text-field
        label="Label"
        v-model="editedLabel"
        density="compact"
        hide-details="auto"
        single-line
        variant="solo"
        @keydown.enter="save"
        @keydown.esc="editing = -1"
      >
        <template v-slot:append>
          <v-btn variant="tonal" size="x-small" icon="true" @click="save" :disabled="label.value.trim().length === 0">
            <v-icon>{{ mdiContentSave }}</v-icon>
          </v-btn>
          <v-btn class="ml-1" variant="tonal" size="x-small" icon="true" @click="cancelEdit">
            <v-icon>{{ mdiCancel }}</v-icon>
          </v-btn>
        </template>
      </v-text-field>
    </template>
    <template v-else>
      <span class="editable-label">
        <v-tooltip :text="LABEL_TYPES[label.property as keyof typeof LABEL_TYPES].propertyPrefixed">
          <template v-slot:activator="{ props }">
            <v-icon v-bind="props">{{ LABEL_TYPES[label.property as keyof typeof LABEL_TYPES].icon }}</v-icon>
          </template>
        </v-tooltip>
        {{ label.value }}
        <v-btn variant="tonal" size="x-small" icon="true" @click="onEdit(index)">
          <v-icon>{{ mdiPencil }}</v-icon>
        </v-btn>
      </span>
    </template>
  </div>
</template>

<style scoped>
.editable-label {
  display: inline-block;
  padding-top: 8px;
  min-height: 42px;
}
</style>
