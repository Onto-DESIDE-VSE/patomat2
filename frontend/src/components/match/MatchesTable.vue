<!--suppress CssUnusedSymbol -->
<script setup lang="ts">
import { computed, onMounted, ref, watch } from "vue";
import _ from "lodash";
import type { NewEntity, PatternInstance } from "@/types/PatternInstance";
import type { PatternInstanceTransformation } from "@/types/PatternInstanceTransformation";
import MatchesTablePagination from "@/components/match/MatchesTablePagination.vue";
import PatternInstanceStatusToggle from "@/components/match/PatternInstanceStatusToggle.vue";
import EditableLabel from "@/components/match/EditableLabel.vue";
import SparqlWithVariables from "@/components/match/SparqlWithVariables.vue";
import BindingValue from "@/components/match/BindingValue.vue";
import { mdiInformation, mdiMenuDown, mdiCloseCircle } from "@mdi/js";
import Constants from "@/constants/Constants";

interface MatchesTablePreferences {
  showTransformationSparql: boolean;
  showPatternName: boolean;
  itemsPerPage: number;
}

const defaultTablePreferences: MatchesTablePreferences = {
  showTransformationSparql: false,
  showPatternName: true,
  itemsPerPage: 10
};

const tablePreferences = ref<MatchesTablePreferences>(defaultTablePreferences);

onMounted(() => {
  const saved = localStorage.getItem("tablePreferences");
  if (saved) {
    tablePreferences.value = JSON.parse(saved);
  }
});

watch(
  tablePreferences,
  (newVal) => {
    localStorage.setItem("tablePreferences", JSON.stringify(newVal));
  },
  { deep: true }
);

const props = defineProps<{
  matches: PatternInstance[];
  onInstanceChange: (instance: PatternInstance) => void;
  onTransform: (applyDeletes: boolean, instances: PatternInstanceTransformation[]) => void;
}>();

const selected = computed(() => paginatedItems.value.filter((item) => item.status === true));
//const rejected = computed(() => paginatedItems.value.filter((item) => item.status === false));

// keys are pattern instance ids, value are mapping of variable names to new labels
const newEntityLabels = ref<Map<number, { [key: string]: string }>>(new Map());

const filteredItems = computed(() => {
  return props.matches.filter((item) => {
    // filter by status
    if (!filterByStatus(item.status)) {
      return false;
    }

    // filter by patternName
    if (!filterByPatternName(item.patternName)) {
      return false;
    }

    // fulltext search
    if (!searchText.value) {
      return true;
    } else {
      const searchTextLower = searchText.value.toLowerCase();

      if (item.match.bindings.some((binding) => binding.value.toLowerCase().includes(searchTextLower))) {
        return true;
      }

      return item.newEntities?.some((entity) =>
        entity.labels?.some((label) => label.value.toLowerCase().includes(searchTextLower))
      );
    }
  });
});

const patternNames = computed(() => [...new Set(props.matches.map((m: PatternInstance) => m.patternName))]);
const searchPatternName = ref<string[]>([]);
// watching for pattern names change - select default value
watch(
  patternNames,
  (newPatternNames) => {
    if (newPatternNames.length === 1) {
      searchPatternName.value = [newPatternNames[0]]; // vybereme jediný pattern
    }
  },
  { immediate: true }
);

const allowShowPatternNameSwitch = computed(() => patternNames.value.length === 1);

function filterByPatternName(value: string) {
  return searchPatternName.value.length === 0 || searchPatternName.value.includes(value);
}

const statusNames = computed(() => ["All", "Approved", "Rejected", "Undecided"]);
const searchStatus = ref<string>("All");

function filterByStatus(value: boolean | null) {
  switch (searchStatus.value) {
    case "Approved":
      return value === true;
    case "Rejected":
      return value === false;
    case "Undecided":
      return value === null;
    default:
      return true;
  }
}

const searchText = ref<string>();

const isFilterActive = computed(() => {
  return (
    searchStatus.value !== "All" ||
    (patternNames.value.length > 1 && searchPatternName.value.length > 0) ||
    searchText.value
  );
});

function clearFilter() {
  searchStatus.value = "All";
  searchPatternName.value = [];
  searchText.value = "";
}

const itemsCountHtml = computed(() => {
  if (props.matches.length === 0) {
    return "<strong class='text-red'>No matches found</strong>";
  }
  if (isFilterActive.value) {
    if (filteredItems.value.length === 0) {
      return (
        "<strong class='text-red'>No matches corresponding filter</strong>, total matches: <strong>" +
        props.matches.length +
        "</strong>"
      );
    } else {
      return (
        "Filtered <strong>" +
        filteredItems.value.length +
        "</strong> of <strong>" +
        props.matches.length +
        "</strong> matches"
      );
    }
  } else {
    return "Total matches: <strong>" + props.matches.length + "</strong>, no filtering";
  }
});

const page = ref(1);
const startIndex = computed(() => (page.value - 1) * tablePreferences.value.itemsPerPage);
const endIndex = computed(() => Math.min(page.value * tablePreferences.value.itemsPerPage, filteredItems.value.length));
const paginatedItems = computed(() => filteredItems.value.slice(startIndex.value, endIndex.value));

watch([searchStatus, searchPatternName, searchText], () => {
  page.value = 1; // reset na první stránku
});

const headers = computed(() => [
  {
    title: "Status",
    value: "status",
    filterable: false,
    visible: true,
    width: "50px",
    sortable: false
  },
  {
    title: "Pattern name",
    key: "patternName",
    value: "patternName",
    filterable: false,
    visible: !allowShowPatternNameSwitch.value || tablePreferences.value.showPatternName,
    sortable: false
  },
  {
    title: "Matching bindings",
    key: "bindings",
    value: "match.bindings",
    filterable: false,
    visible: true,
    sortable: false
  },
  {
    title: "Transformation SPARQL",
    key: "transformationSparql",
    value: (item: PatternInstance) => ({
      insert: item.sparqlInsert,
      del: item.sparqlDelete,
      variables: item.newEntities
        .map((newEntity) => {
          return {
            name: newEntity.variableName,
            value: newEntity.identifier,
            datatype: Constants.RDFS_RESOURCE
          };
        })
        .concat(item.match.bindings)
    }),
    filterable: false,
    visible: tablePreferences.value.showTransformationSparql,
    sortable: false
  },
  {
    title: "New entities",
    key: "newEntities",
    value: (item: PatternInstance) => ({
      id: item.id,
      newEntities: item.newEntities.map((newEntity) => ({
        name: newEntity.variableName,
        value: newEntity.identifier,
        datatype: Constants.RDFS_RESOURCE,
        labels: newEntity.labels
      }))
    }),
    filterable: false,
    visible: true,
    sortable: false
  }
]);

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

  <v-row class="align-center mt-4" dense>
    <v-col cols="12" lg="2">
      <v-select
        clearable
        label="Filter by pattern"
        :items="patternNames"
        v-model="searchPatternName"
        multiple
        placeholder="All patterns"
        density="comfortable"
        :disabled="patternNames.length <= 1"
        hide-details
      ></v-select>
    </v-col>
    <v-col cols="12" lg="2">
      <v-select
        label="Filter by status"
        :items="statusNames"
        v-model="searchStatus"
        density="comfortable"
        hide-details
      ></v-select>
    </v-col>
    <v-col cols="12" lg="4">
      <v-text-field
        clearable
        label="Search URI or label"
        v-model="searchText"
        variant="filled"
        density="comfortable"
        rounded="md"
        hide-details
      ></v-text-field>
    </v-col>
  </v-row>
  <v-row class="align-center" dense>
    <v-col class="d-flex align-center">
      <span class="text-body-2 px-2 py-2 px-lg-4" v-html="itemsCountHtml"></span>
      <v-btn
        v-if="isFilterActive"
        variant="plain"
        color="primary"
        class="text-body-2"
        density="comfortable"
        :prepend-icon="mdiCloseCircle"
        @click="clearFilter"
      >
        Clear filter
      </v-btn>
    </v-col>
  </v-row>

  <v-row class="mt-5 align-center">
    <v-col cols="12" lg="5" order-lg="2">
      <div class="d-flex flex-row align-center" v-if="filteredItems.length > 0">
        <v-menu>
          <template #activator="{ props: menuProps }">
            <v-btn
              v-bind="menuProps"
              color="grey-lighten-2"
              size="small"
              variant="flat"
              style="text-transform: none"
              :append-icon="mdiMenuDown"
            >
              Change status
            </v-btn>
          </template>

          <v-list>
            <v-list-item
              @click="
                filteredItems.forEach((item) => {
                  if (item.status === null) item.status = true;
                })
              "
            >
              <v-list-item-title>Approve all undecided</v-list-item-title>
            </v-list-item>
            <v-list-item
              @click="
                filteredItems.forEach((item) => {
                  if (item.status === null) item.status = false;
                })
              "
            >
              <v-list-item-title>Reject all undecided</v-list-item-title>
            </v-list-item>
            <v-list-item @click="filteredItems.forEach((item) => (item.status = null))">
              <v-list-item-title>Clear decisions</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-switch
          v-model="tablePreferences.showPatternName"
          title="Show Pattern name"
          density="compact"
          hide-details
          class="ml-6"
          v-show="allowShowPatternNameSwitch"
        >
          <template #label>
            <span class="text-body-2" title="Show Pattern name">Pattern name</span>
          </template>
        </v-switch>

        <v-switch
          v-model="tablePreferences.showTransformationSparql"
          title="Show Transformation SPARQL"
          label="Show SPARQL"
          density="compact"
          hide-details
          class="ml-6"
        >
          <template #label>
            <span class="text-body-2" title="Show Transformation SPARQL">Show SPARQL</span>
          </template>
        </v-switch>
      </div>
    </v-col>

    <v-col cols="12" lg="7" order-lg="2">
      <MatchesTablePagination
        v-model:page="page"
        v-model:items-per-page="tablePreferences.itemsPerPage"
        :show-items-count="false"
        :total-items="filteredItems.length"
      />
    </v-col>
  </v-row>

  <v-data-table
    :headers="headers.filter((h) => h.visible)"
    :items="paginatedItems"
    return-object
    :items-per-page="tablePreferences.itemsPerPage"
    hide-default-footer
  >
    <template v-slot:[`item.status`]="{ item }">
      <PatternInstanceStatusToggle v-model:status="item.status" />
    </template>
    <template v-slot:[`item.bindings`]="{ value }">
      <ul class="mt-1 mb-1">
        <li v-for="binding in value" :key="binding.id" class="mb-1">
          <BindingValue :binding="binding" :tooltip-with-label="true"></BindingValue>
        </li>
      </ul>
    </template>
    <template v-slot:[`item.transformationSparql`]="{ value }">
      <div class="mb-1 mt-1 sparql">
        <div v-if="value.del" class="mt-1 mb-1">
          <SparqlWithVariables :sparql="value.del" :bindings="value.variables"></SparqlWithVariables>
        </div>
        <SparqlWithVariables :sparql="value.insert" :bindings="value.variables"></SparqlWithVariables>
      </div>
      <v-row align="center" no-gutters class="mb-1 font-italic" v-if="!value.del">
        <v-icon v-bind="props" class="mr-1">{{ mdiInformation }}</v-icon>
        Instance containing blank node-based binding. Cannot delete.
      </v-row>
    </template>
    <template v-slot:[`item.newEntities`]="{ value }">
      <v-list dense class="mt-1 mb-1">
        <v-list-item v-for="entity in value.newEntities" :key="entity.name" class="px-0">
          <v-list-item-title class="text-body-2">
            <BindingValue :binding="entity"></BindingValue>
          </v-list-item-title>

          <div v-if="entity.labels?.length > 0" class="ml-4">
            <EditableLabel :patternInstanceId="value.id" :entity="entity" :onSave="onNewEntityLabelChanged" />
          </div>
        </v-list-item>
      </v-list>
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
        <span v-if="selected.length > 0"
          >Selected items: <strong>{{ selected.length }}</strong></span
        >
        <span v-else>Select items to transform</span>
      </v-btn>
    </v-col>

    <v-col cols="12" lg="10" order-lg="2">
      <MatchesTablePagination
        v-model:page="page"
        v-model:items-per-page="tablePreferences.itemsPerPage"
        :show-items-count="false"
        :total-items="filteredItems.length"
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
  max-width: 35vw;
}
.btn-selected-items-count.v-btn--disabled {
  opacity: 1 !important;
}
</style>
