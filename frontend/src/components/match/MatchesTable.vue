<!--suppress CssUnusedSymbol -->
<script setup lang="ts">
import { computed, ref } from "vue";
import _ from "lodash";
import type { NewEntity, PatternInstance } from "@/types/PatternInstance";
import type { PatternInstanceTransformation } from "@/types/PatternInstanceTransformation";
import MatchesTablePagination from "@/components/match/MatchesTablePagination.vue";
import PatternInstanceStatusToggle from "@/components/match/PatternInstanceStatusToggle.vue";
import EditableLabel from "@/components/match/EditableLabel.vue";
import SparqlWithVariables from "@/components/match/SparqlWithVariables.vue";
import BindingValue from "@/components/match/BindingValue.vue";
import { mdiInformation } from "@mdi/js";
import Constants from "@/constants/Constants";
import { valueToString } from "@/util/Utils";

const itemsPerPage = ref(10);
const page = ref(1);

const startIndex = computed(() => (page.value - 1) * itemsPerPage.value);

const endIndex = computed(() => Math.min(page.value * itemsPerPage.value, props.matches.length));

const paginatedItems = computed(() => props.matches.slice(startIndex.value, endIndex.value));

const props = defineProps<{
  matches: PatternInstance[];
  onInstanceChange: (instance: PatternInstance) => void;
  onTransform: (applyDeletes: boolean, instances: PatternInstanceTransformation[]) => void;
}>();

const selected = computed(() => paginatedItems.value.filter((item) => item.status === true));

// keys are pattern instance ids, value are mapping of variable names to new labels
const newEntityLabels = ref<Map<number, { [key: string]: string }>>(new Map());

const patternNames = computed(() => ["All", ...new Set(props.matches.map((m: PatternInstance) => m.patternName))]);
const search = ref<string[]>(["All"]);

function filterByPatternName(value: string) {
  return search.value.includes("All") || search.value.includes(value);
}

const headers = [
  {
    title: "Status",
    value: "status"
  },
  {
    title: "Pattern name",
    value: "patternName",
    filter: filterByPatternName
  },
  {
    title: "Matching bindings",
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

let applyTransformationDisabled = computed(() => selected.value.length === 0);
</script>

<template>
  <div class="mb-4 mt-2">
    <v-btn
      @click="applyTransformation(true)"
      color="primary"
      :disabled="applyTransformationDisabled"
      :title="
        applyTransformationDisabled
          ? 'Select at leat one item'
          : 'Show transformation summary and download transformation file'
      "
      >Apply transformation</v-btn
    >
  </div>

  <!--TODO replace-->
  <v-row class="align-center">
    <v-col cols="2">
      <v-select clearable label="Select pattern" :items="patternNames" v-model="search" multiple></v-select>
    </v-col>
  </v-row>

  <MatchesTablePagination
    v-model:page="page"
    v-model:items-per-page="itemsPerPage"
    :total-items="props.matches.length"
  />

  <v-data-table
    :headers="headers"
    :items="paginatedItems"
    return-object
    :items-per-page="itemsPerPage"
    hide-default-footer
  >
    <template v-slot:[`item.status`]="{ item }">
      <PatternInstanceStatusToggle v-model:status="item.status" />
    </template>
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

  <v-row class="mt-5">
    <v-col cols="12" lg="2" order-lg="2">
      <v-btn
        :disabled="applyTransformationDisabled"
        variant="plain"
        class="text-lg-caption p-0 my-auto btn-selected-items-count"
        color="grey-darken-2"
        @click="applyTransformation(true)"
      >
        <span v-if="selected.length > 0">Selected items: {{ selected.length }}</span>
        <span v-else>Select items to transform</span>
      </v-btn>
    </v-col>

    <v-col cols="12" lg="10" order-lg="2">
      <MatchesTablePagination
        v-model:page="page"
        v-model:items-per-page="itemsPerPage"
        :total-items="props.matches.length"
      />
    </v-col>
  </v-row>

  <v-row>
    <v-col cols="12">
      <v-btn
        @click="applyTransformation(true)"
        color="primary"
        :disabled="applyTransformationDisabled"
        :title="
          applyTransformationDisabled
            ? 'Select at leat one item'
            : 'Show transformation summary and download transformation file'
        "
        >Apply transformation</v-btn
      >
    </v-col>
  </v-row>
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
.btn-selected-items-count.v-btn--disabled {
  opacity: 1 !important;
}
</style>
