<script setup lang="ts">
import { ref } from "vue"
import type { PatternInstance, ResultBinding } from "@/types/PatternInstance"
import type { PatternInstanceTransformation } from "@/types/PatternInstanceTransformation"
import { mdiMenuDown } from "@mdi/js"

const props = defineProps<{
  matches: PatternInstance[]
  onTransform: (applyDeletes: boolean, instances: PatternInstanceTransformation[]) => void
}>()

const selected = ref<PatternInstance[]>([])

const headers = [
  {
    title: "Pattern Name",
    value: "patternName"
  },
  {
    title: "Matching Bindings",
    key: "bindings",
    value: "match.bindings"
  },
  {
    title: "Transformation SPARQL",
    key: "transformationSparql",
    value: (item: PatternInstance) => ({
      insert: item.sparqlInsert,
      del: item.sparqlDelete
    })
  },
  {
    title: "New entities",
    value: "newEntities"
  }
]

function valueToString(binding: ResultBinding) {
  if (binding.datatype === "http://www.w3.org/2000/01/rdf-schema#Resource") {
    return `<${binding.value}>`
  } else {
    return `${binding.value}^^${binding.datatype}`
  }
}

function applyTransformation(applyDeletes: boolean) {
  const instances = selected.value.map(
    (v: PatternInstance) =>
      ({
        id: v.id
      }) as PatternInstanceTransformation
  )
  props.onTransform(applyDeletes, instances)
}
</script>

<template>
  <div class="mb-3">
    <v-menu :location="'bottom'">
      <template v-slot:activator="{ props }">
        <v-btn id="apply-transformation-top" color="primary" v-bind="props" :disabled="selected.length === 0">
          Apply transformation
          <v-icon dark end size="medium">{{ mdiMenuDown }}</v-icon>
        </v-btn>
      </template>
      <v-list>
        <v-list-item @click="() => applyTransformation(false)">
          <v-list-item-title>Apply only inserts</v-list-item-title>
        </v-list-item>
        <v-list-item @click="() => applyTransformation(true)">
          <v-list-item-title>Apply deletes and inserts</v-list-item-title>
        </v-list-item>
      </v-list>
    </v-menu>
  </div>
  <v-data-table :headers="headers" :items="props.matches" show-select v-model="selected" return-object>
    <template v-slot:item.bindings="{ value }">
      <ul class="mt-1 mb-1">
        <li v-for="binding in value">
          <span class="font-weight-bold">{{ binding.name }}</span
          >:
          {{ valueToString(binding) }}
        </li>
      </ul>
    </template>
    <template v-slot:item.transformationSparql="{ value }">
      <pre class="mb-2">{{ value.del }}</pre>
      <pre>{{ value.insert }}</pre>
    </template>
    <template v-slot:item.newEntities="{ value }">
      <ul class="mt-1 mb-1">
        <li v-for="entity in value">
          <span class="font-weight-bold">{{ entity.variableName }}</span
          >: <{{ entity.identifier }}>
          <ul v-if="entity.label.length > 0" class="ml-4">
            <li>{{ entity.label }}</li>
          </ul>
        </li>
      </ul>
    </template>
  </v-data-table>
  <div class="mt-3">
    <v-menu :location="'top'">
      <template v-slot:activator="{ props }">
        <v-btn id="apply-transformation-bottom" color="primary" v-bind="props" :disabled="selected.length === 0">
          Apply transformation
          <v-icon dark end size="medium">{{ mdiMenuDown }}</v-icon>
        </v-btn>
      </template>
      <v-list>
        <v-list-item @click="() => applyTransformation(false)">
          <v-list-item-title>Apply only inserts</v-list-item-title>
        </v-list-item>
        <v-list-item @click="() => applyTransformation(true)">
          <v-list-item-title>Apply deletes and inserts</v-list-item-title>
        </v-list-item>
      </v-list>
    </v-menu>
  </div>
</template>
