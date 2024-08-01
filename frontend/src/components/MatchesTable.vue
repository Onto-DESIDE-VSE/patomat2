<script setup lang="ts">
import { ref } from "vue";
import _ from "lodash";
import type { NewEntity, PatternInstance, ResultBinding } from "@/types/PatternInstance";
import type { PatternInstanceTransformation } from "@/types/PatternInstanceTransformation";
import EditableLabel from "@/components/EditableLabel.vue";
import { mdiMenuDown } from "@mdi/js";

const props = defineProps<{
  matches: PatternInstance[];
  onInstanceChange: (instance: PatternInstance) => void;
  onTransform: (applyDeletes: boolean, instances: PatternInstanceTransformation[]) => void;
}>();

const selected = ref<PatternInstance[]>([]);
// keys are pattern instance ids, value are mapping of variable names to new labels
const newEntityLabels = ref<Map<number, { [key: string]: string }>>(new Map());

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
    key: "newEntities",
    value: (item: PatternInstance) => ({
      id: item.id,
      newEntities: item.newEntities
    })
  }
];

function valueToString(binding: ResultBinding) {
  if (binding.datatype === "http://www.w3.org/2000/01/rdf-schema#Resource") {
    return `<${binding.value}>`;
  } else {
    return `${binding.value}^^${binding.datatype}`;
  }
}

function onNewEntityLabelChanged(patternInstanceId: number, ne: NewEntity) {
  const instance = props.matches.find((inst) => inst.id === patternInstanceId);
  if (instance) {
    const change = _.cloneDeep(instance);
    const newLabel: any = {};
    newLabel[ne.variableName] = ne.label;
    newEntityLabels.value.set(
      patternInstanceId,
      Object.assign({}, newEntityLabels.value.get(patternInstanceId), newLabel)
    );
    const index = change.newEntities.findIndex((entity: NewEntity) => entity.variableName === ne.variableName);
    change.newEntities.splice(index, 1, ne);
    props.onInstanceChange(change);
  }
}

function applyTransformation(applyDeletes: boolean) {
  const instances = selected.value.map(
    (v: PatternInstance) =>
      ({
        id: v.id,
        newEntityLabels: newEntityLabels.value.has(v.id) ? newEntityLabels.value.get(v.id) : undefined
      }) as PatternInstanceTransformation
  );
  props.onTransform(applyDeletes, instances);
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
        <li v-for="entity in value.newEntities">
          <span class="font-weight-bold">{{ entity.variableName }}</span
          >: <{{ entity.identifier }}>
          <ul v-if="entity.label?.length > 0" class="ml-4">
            <li>
              <EditableLabel
                :patternInstanceId="value.id"
                :entity="entity"
                :onSave="onNewEntityLabelChanged"
              ></EditableLabel>
            </li>
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
