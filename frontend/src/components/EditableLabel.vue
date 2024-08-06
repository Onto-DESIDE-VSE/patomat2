<script setup lang="ts">
import { ref } from "vue";
import { mdiPencil, mdiCancel, mdiContentSave, mdiLabel } from "@mdi/js";
import type { NewEntity } from "@/types/PatternInstance";

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
  labels[editing.value] = editedLabel.value;
  const update = Object.assign({}, props.entity, { labels });
  props.onSave(props.patternInstanceId, update);
  editing.value = -1;
}

function onEdit(index: number) {
  editedLabel.value = props.entity.labels[index];
  editing.value = index;
}
</script>

<template>
  <div v-for="(label, index) in props.entity.labels" :key="label">
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
          <v-btn variant="tonal" size="x-small" icon="true" @click="save" :disabled="label.trim().length === 0">
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
        <v-icon>{{ mdiLabel }}</v-icon>
        {{ label }}
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
