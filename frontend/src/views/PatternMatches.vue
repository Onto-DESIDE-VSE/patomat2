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
import { getLoadedInput } from "@/api/OntologyStorageApi";

const router = useRouter();
const messageStore = useMessageStore();

const transformationInput = ref<LoadedTransformationInput | null>({ ontology: "", patterns: [] });
const matches = ref<PatternInstance[]>([]);
const transformationSummary = ref<TransformationSummary | null>(null);
const showProgress = ref(false);

const fetchMatches = async () => {
  showProgress.value = true;
  const resp = await fetch(`${Constants.SERVER_URL}/matches`, {
    credentials: "include"
  });
  showProgress.value = false;
  if (resp.status === 200) {
    matches.value = await resp.json();
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

onMounted(async () => {
  transformationInput.value = await getLoadedInput();
  if (transformationInput.value === null) {
    messageStore.publishMessage("Ontology not uploaded, yet.");
    return;
  }
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
  <MatchesTable :matches="matches" :on-instance-change="onInstanceChange" :on-transform="applyTransformation" />
  <TransformationSummaryView :summary="transformationSummary" />
</template>
