<script setup lang="ts">
import { computed, ref } from "vue";
import _ from "lodash";
import type { NewEntity, PatternInstance, ResultBinding } from "@/types/PatternInstance";
import type { PatternInstanceTransformation } from "@/types/PatternInstanceTransformation";
import EditableLabel from "@/components/match/EditableLabel.vue";
import TransformationExecutionDropdown from "@/components/match/TransformationExecutionDropdown.vue";
import SparqlWithVariables from "@/components/match/SparqlWithVariables.vue";
import Binding from "@/components/match/Binding.vue";
import { mdiInformation } from "@mdi/js";

const props = defineProps<{
  matches: PatternInstance[];
  onInstanceChange: (instance: PatternInstance) => void;
  onTransform: (applyDeletes: boolean, instances: PatternInstanceTransformation[]) => void;
}>();

const selected = ref<PatternInstance[]>([]);
// keys are pattern instance ids, value are mapping of variable names to new labels
const newEntityLabels = ref<Map<number, { [key: string]: string }>>(new Map());

const patternNames = computed(() => ["All", ...new Set(props.matches.map((m: PatternInstance) => m.patternName))]);
const search = ref<string[]>(["All"]);

function filterByPatternName(value: string) {
  return search.value.includes("All") || search.value.includes(value);
}

const headers = [
  {
    title: "Pattern Name",
    value: "patternName",
    filter: filterByPatternName
  },
  {
    title: "Matching Bindings",
    key: "bindings",
    value: "match.bindings",
    filterable: false
  },
  {
    title: "Transformation SPARQL",
    key: "transformationSparql",
    value: (item: PatternInstance) => ({
      insert: item.sparqlInsert,
      del: item.sparqlDelete,
      newEntities: item.newEntities
    }),
    filterable: false
  },
  {
    title: "New entities",
    key: "newEntities",
    value: (item: PatternInstance) => ({
      id: item.id,
      newEntities: item.newEntities
    }),
    filterable: false
  }
];

function onNewEntityLabelChanged(patternInstanceId: number, ne: NewEntity) {
  const instance = props.matches.find((inst) => inst.id === patternInstanceId);
  if (instance) {
    const change = _.cloneDeep(instance);
    const newLabel: any = {};
    newLabel[ne.variableName] = ne.labels;
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
  <TransformationExecutionDropdown
    menu-position="bottom"
    :execute-inserts="() => applyTransformation(false)"
    :execute-inserts-and-deletes="() => applyTransformation(true)"
    :disabled="selected.length === 0"
  ></TransformationExecutionDropdown>
  <v-row class="align-center">
    <v-col cols="2">
      <v-select clearable label="Select pattern" :items="patternNames" v-model="search" multiple></v-select>
    </v-col>
  </v-row>
  <v-data-table
    :headers="headers"
    :items="props.matches"
    show-select
    v-model="selected"
    select-strategy="all"
    return-object
  >
    <template v-slot:item.bindings="{ value }">
      <ul class="mt-1 mb-1">
        <li v-for="binding in value" :key="binding.id" class="mb-1">
          <Binding :binding="binding"></Binding>
        </li>
      </ul>
    </template>
    <template v-slot:item.transformationSparql="{ value }">
      <div v-if="value.del" class="mt-1 mb-1 sparql">{{ value.del }}</div>
      <div class="mb-1 mt-1 sparql">
        <SparqlWithVariables :sparql="value.insert" :new-entities="value.newEntities"></SparqlWithVariables>
      </div>
      <v-row align="center" no-gutters class="mb-1 font-italic" v-if="!value.del">
        <v-icon v-bind="props" class="mr-1">{{ mdiInformation }}</v-icon>
        Instance containing blank node-based binding. Cannot delete.
      </v-row>
    </template>
    <template v-slot:item.newEntities="{ value }">
      <ul class="mt-1 mb-1">
        <li v-for="entity in value.newEntities">
          <span class="font-weight-bold">{{ entity.variableName }}</span
          >: <{{ entity.identifier }}>
          <div v-if="entity.labels?.length > 0" class="ml-4">
            <EditableLabel
              :patternInstanceId="value.id"
              :entity="entity"
              :onSave="onNewEntityLabelChanged"
            ></EditableLabel>
          </div>
        </li>
      </ul>
    </template>
  </v-data-table>
  <TransformationExecutionDropdown
    menu-position="top"
    :execute-inserts="() => applyTransformation(false)"
    :execute-inserts-and-deletes="() => applyTransformation(true)"
    :disabled="selected.length === 0"
  ></TransformationExecutionDropdown>
</template>

<style scoped>
.sparql {
  font-family: monospace, monospace;
  font-size: 90%;
  white-space: pre;
  word-break: normal;
  overflow: auto;
  padding: 0;
}
</style>
