<script setup lang="ts">
import { computed, ref } from "vue";
import _ from "lodash";
import type { NewEntity, PatternInstance } from "@/types/PatternInstance";
import type { PatternInstanceTransformation } from "@/types/PatternInstanceTransformation";
import EditableLabel from "@/components/match/EditableLabel.vue";
import TransformationExecutionDropdown from "@/components/match/TransformationExecutionDropdown.vue";
import SparqlWithVariables from "@/components/match/SparqlWithVariables.vue";
import BindingValue from "@/components/match/BindingValue.vue";
import { mdiInformation } from "@mdi/js";
import Constants from "@/constants/Constants";
import { valueToString } from "@/util/Utils";
import { reactive } from "vue";

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
const pagination = ref({
  page: 0,
  itemsPerPage: 5
});

// Computed property to calculate total page count based on items and items per page
const pageCount = computed(() => Math.ceil(props.matches.length / pagination.value.itemsPerPage));
const nextPage = () => {
  pagination.page += 1;
};

const previousPage = () => {
  pagination.page = Math.max(1, pagination.page - 1);
};

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
      variables: item.newEntities
        .map((ne) => ({ name: ne.variableName, value: ne.identifier, datatype: Constants.RDFS_RESOURCE }))
        .concat(item.match.bindings)
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
  <v-row justify="end" class="ma-2">
    <div class="items-wrap">
       
      <div class="pagination-controls">Items per page:</div>
      <v-select v-model="pagination.itemsPerPage" :items="[5, 10, 15, 20]" class="pa-2 pagination-select">
        {{ pagination.itemsPerPage }}}}
      </v-select>
    </div>
    <div class="pagination-controls">Page {{ pagination.page }} of {{ pageCount }}</div>

    <div class="pagination-controls">
      <button @click="previousPage" :disabled="pagination.page === 1" class="pa-2">previous</button>
    </div>
    <div class="pagination-controls">
      <button @click="nextPage" :disabled="pagination.page >= pageCount" class="pa-2">next</button>
    </div>
  </v-row>
  <v-pagination v-model="pagination.page" :length="pageCount" total-visible="5"></v-pagination>
  <v-data-table
    :headers="headers"
    :items="props.matches"
    :items-per-page="pagination.itemsPerPage"
    :page.sync="pagination.page"
    :items-per-page-options="[5, 10, 15, 20]"
    show-select
    v-model="selected"
    select-strategy="all"
    return-object
    :footer-props="{
      itemsPerPageOptions: [5, 10, 15, 20]
    }"
  >
    <template v-slot:[`item.bindings`]="{ value }">
      <ul class="mt-1 mb-1">
        <li v-for="binding in value" :key="binding.id" class="mb-1">
          <BindingValue :binding="binding"></BindingValue>
        </li>
      </ul>
    </template>
    <template v-slot:[`item.transformationSparql`]="{ value }">
      <div v-if="value.del" class="mt-1 mb-1 sparql">
        <SparqlWithVariables :sparql="value.del" :bindings="value.variables"></SparqlWithVariables>
      </div>
      <div class="mb-1 mt-1 sparql">
        <SparqlWithVariables :sparql="value.insert" :bindings="value.variables"></SparqlWithVariables>
      </div>
      <v-row align="center" no-gutters class="mb-1 font-italic" v-if="!value.del">
        <v-icon v-bind="props" class="mr-1">{{ mdiInformation }}</v-icon>
        Instance containing blank node-based binding. Cannot delete.
      </v-row>
    </template>
    <template v-slot:[`item.newEntities`]="{ value }">
      <ul class="mt-1 mb-1">
        <li v-for="entity in value.newEntities" v-bind:key="entity.variableName">
          <span class="font-weight-bold">{{ entity.variableName }}</span
          >: {{ valueToString({ value: entity.identifier, datatype: Constants.RDFS_RESOURCE }) }}
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
.pagination-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  list-style: none;
  gap: 20px;
  margin-bottom: 10px;
  padding: 0;
}
.pagination-wrapper {
  align-items: center;
  gap: 10px;
}
.items-wrap {
  display: flex;
  flex-direction: row;
}
</style>
