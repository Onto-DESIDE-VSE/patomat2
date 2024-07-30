<script setup lang="ts">
import { ref } from "vue"
import { mdiPencil, mdiCancel, mdiContentSave } from "@mdi/js"

const props = defineProps<{
  patternInstanceId: number
  entity: {
    identifier: string
    label: string
    variableName: string
  }
  onSave: (patternInstanceId: number, variable: string, label: string) => void
}>()

const label = ref(props.entity.label)
const editing = ref(false)

function cancelEdit() {
  label.value = props.entity.label
  editing.value = false
}

function save() {
  const nameMapping = {}
  nameMapping[props.entity.variableName] = label.value
  props.onSave(props.patternInstanceId, nameMapping)
}
</script>

<template>
  <template v-if="editing">
    <v-text-field
      label="Label"
      v-model="label"
      density="compact"
      single-line
      variant="solo"
      @keydown.enter="save"
      @keydown.esc="editing = false"
    >
      <template v-slot:append>
        <v-btn variant="tonal" size="x-small" icon @click="save" :disabled="label.trim().length === 0">
          <v-icon>{{ mdiContentSave }}</v-icon>
        </v-btn>
        <v-btn class="ml-1" variant="tonal" size="x-small" icon @click="cancelEdit">
          <v-icon>{{ mdiCancel }}</v-icon>
        </v-btn>
      </template>
    </v-text-field>
  </template>
  <template v-else>
    {{ label }}
    <v-btn variant="tonal" size="x-small" icon @click="editing = true">
      <v-icon>{{ mdiPencil }}</v-icon>
    </v-btn>
  </template>
</template>

<style scoped></style>
