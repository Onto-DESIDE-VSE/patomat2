<script setup lang="ts">
import Constants from "@/constants/Constants";
import { onMounted, ref } from "vue";
import MatchesTable from "@/components/match/MatchesTable.vue";
import type { PatternInstance } from "@/types/PatternInstance";
import type { PatternInstanceTransformation } from "@/types/PatternInstanceTransformation";
import { downloadAttachment } from "@/util/Utils";
import useMessageStore from "@/store/messageStore";
import { useRouter } from "vue-router";
import type { TransformationSummary } from "@/types/TransformationSummary";
import TransformationSummaryView from "@/components/match/TransformationSummaryView.vue";
import MatchesStatistics from "@/components/match/MatchesStatistics.vue";
import type { LoadedTransformationInput } from "@/types/LoadedTransformationInput";
import { clearSessionData, getLoadedInput } from "@/api/OntologyStorageApi";
import type { SortMethod } from "@/types/SortMethod";
import type { NewEntityIdentifierConfig } from "@/types/NewEntityIdentifierConfig";
import { setNewEntityIdentifierConfig } from "@/api/NewEntityIdentifier";

const router = useRouter();
const messageStore = useMessageStore();

const transformationInput = ref<LoadedTransformationInput | null>({ ontology: "", patterns: [] });
const matches = ref<PatternInstance[]>([]);
const transformationSummary = ref<TransformationSummary | null>(null);
const showProgress = ref(false);
const sortMethods = ref<SortMethod[]>([]);
const loadedSortMethods = ref<Set<string>>(new Set());
const defaultSortMethod: SortMethod = { value: "default", name: "Default" };

const fetchMatches = async () => {
  showProgress.value = true;
  const resp = await fetch(`${Constants.SERVER_URL}/matches`, {
    credentials: "include"
  });
  showProgress.value = false;
  if (resp.status === 200) {
    const data = await resp.json();
    data.map((match: PatternInstance, index: number) => {
      match.status = null;
      match.sortValues = {
        ...(match.sortValues || {}),
        default: index // default order obtained from server
      };
      return match;
    });
    loadedSortMethods.value = new Set([defaultSortMethod.value]);
    matches.value = data; //default value of selection status for UI
  } else if (resp.status === 409) {
    messageStore.publishMessage("Ontology not uploaded, yet.");
    await router.push("/load");
  } else if (resp.status === 401) {
    messageStore.publishMessage("PatOMat2 is currently fully utilized. Please try again later.");
  } else {
    const error = await resp.json();
    messageStore.publishMessage("Unable to get pattern matches. Got message: " + error.message);
  }
};

const fetchSortMethods = async () => {
  try {
    const resp = await fetch(`${Constants.SERVER_URL}/sort/options`, {
      credentials: "include"
    });
    if (resp.status === 200) {
      const data = await resp.json();
      sortMethods.value = [
        defaultSortMethod,
        ...data.map((item: any) => ({
          value: item.value,
          name: item.name
        }))
      ];
    } else {
      sortMethods.value = [defaultSortMethod];
      messageStore.publishMessage("Unable to get sort methods.");
    }
  } catch (error) {
    sortMethods.value = [defaultSortMethod];
    messageStore.publishMessage("Failed to fetch sort methods: " + error);
  }
};

const applySorting = async (sortMethod: SortMethod): Promise<boolean> => {
  // check, if it is necessary to load sort values from server
  if (loadedSortMethods.value.has(sortMethod.value)) {
    return true;
  }

  showProgress.value = true;
  try {
    const resp = await fetch(`${Constants.SERVER_URL}/matches?sort=${sortMethod.value}`, {
      credentials: "include"
    });

    if (resp.status === 200) {
      const sortedData = await resp.json();
      sortedData.forEach((sortedInstance: PatternInstance, index: number) => {
        const match = matches.value.find((m) => m.id === sortedInstance.id);
        if (match) {
          // TODO: New entities may have been extended with LLM-generated label (temporary workaround)
          match.newEntities = sortedInstance.newEntities;
          match.likertScore = sortedInstance.likertScore;
          match.sortValues = {
            ...(match.sortValues || {}),
            [sortMethod.value]: index
          };
        }
      });

      loadedSortMethods.value.add(sortMethod.value);
      return true;
    } else {
      const error = await resp.json();
      messageStore.publishMessage("Unable to apply sorting. Got message: " + error.message);
      return false;
    }
  } catch (error) {
    messageStore.publishMessage("Failed to apply sorting: " + error);
    return false;
  } finally {
    showProgress.value = false;
  }
};

const clearData = async () => {
  showProgress.value = true;
  await clearSessionData();
  showProgress.value = false;
  messageStore.publishMessage("User data cleared.");
  await router.push("/load");
};

onMounted(async () => {
  transformationInput.value = await getLoadedInput();
  if (transformationInput.value === null) {
    messageStore.publishMessage("Ontology not uploaded, yet.");
    return;
  }
  await fetchSortMethods();
  await fetchMatches();
  if (router.currentRoute.value.query.transform === "true") {
    await applyTransformation(
      true,
      matches.value.map((m) => ({ id: m.id }))
    );
  }
});

function onInstanceChange(change: PatternInstance) {
  const index = matches.value.findIndex((match) => match.id === change.id);
  matches.value.splice(index, 1, change);
}

const onNewEntityIriConfigChange = async (config: NewEntityIdentifierConfig) => {
  await setNewEntityIdentifierConfig(config);
  messageStore.publishMessage("New entity IRI config updated. Reloading pattern matches.");
  await fetchMatches();
};

const applyTransformation = async (applyDeletes: boolean, instances: PatternInstanceTransformation[]) => {
  showProgress.value = true;
  const resp = await fetch(`${Constants.SERVER_URL}/transformation`, {
    method: "PUT",
    body: JSON.stringify({
      applyDeletes,
      patternInstances: instances
    }),
    credentials: "include",
    headers: {
      "Content-Type": "application/json"
    }
  });
  showProgress.value = false;
  if (resp.ok) {
    await downloadTransformedOntology();
    transformationSummary.value = await resp.json();
  } else {
    const error = await resp.json();
    messageStore.publishMessage("Failed to apply transformation. Got message: " + error.message);
  }
};

const downloadTransformedOntology = async () => {
  const resp = await fetch(`${Constants.SERVER_URL}/transformation/ontology/content`, {
    method: "GET",
    credentials: "include"
  });
  if (resp.ok) {
    downloadAttachment(resp);
  } else {
    const error = await resp.json();
    messageStore.publishMessage("Failed to download transformed ontology. Got message: " + error.message);
  }
};
</script>

<template>
  <h3 class="text-h3 mb-6">Pattern matches</h3>
  <v-overlay :model-value="showProgress" class="align-center justify-center">
    <v-progress-circular color="primary" size="64" indeterminate></v-progress-circular>
  </v-overlay>
  <MatchesStatistics
    v-if="transformationInput !== null"
    :matches="matches"
    :transformation-input="transformationInput"
  />
  <MatchesTable
    :matches="matches"
    :on-instance-change="onInstanceChange"
    :on-transform="applyTransformation"
    :sort-methods="sortMethods"
    :default-sort-method="defaultSortMethod"
    :loaded-sort-methods="loadedSortMethods"
    :on-sort-change="applySorting"
    :on-clear="clearData"
    :on-new-entity-iri-config-change="onNewEntityIriConfigChange"
  />
  <TransformationSummaryView :summary="transformationSummary" />
</template>
