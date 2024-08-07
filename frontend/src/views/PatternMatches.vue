<script setup lang="ts">
import Constants from "@/constants/Constants";
import { ref } from "vue";
import MatchesTable from "@/components/MatchesTable.vue";
import type { PatternInstance } from "@/types/PatternInstance";
import type { PatternInstanceTransformation } from "@/types/PatternInstanceTransformation";
import { downloadAttachment } from "@/util/Utils";
import useMessageStore from "@/store/messageStore";
import { useRouter } from "vue-router";
import type { TransformationSummary } from "@/types/TransformationSummary";
import TransformationSummaryView from "@/components/TransformationSummaryView.vue";

const router = useRouter();
const messageStore = useMessageStore();

const matches = ref<PatternInstance[]>([]);
const transformationSummary = ref<TransformationSummary | null>(null);

const fetchMatches = async () => {
  const resp = await fetch(`${Constants.SERVER_URL}/matches`, {
    credentials: "include"
  });
  if (resp.status === 200) {
    matches.value = await resp.json();
  } else if (resp.status === 409) {
    messageStore.publishMessage("Ontology not uploaded, yet.");
    router.push("/load");
  } else if (resp.status === 401) {
    messageStore.publishMessage("PatOMat2 is currently fully utilized. Please try again later.");
  } else {
    const error = await resp.json();
    messageStore.publishMessage("Unable to get pattern matches. Got message: " + error.message);
  }
};
fetchMatches();

function onInstanceChange(change: PatternInstance) {
  const index = matches.value.findIndex((match) => match.id === change.id);
  matches.value.splice(index, 1, change);
}

const applyTransformation = async (applyDeletes: boolean, instances: PatternInstanceTransformation[]) => {
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
  if (resp.ok) {
    downloadTransformedOntology();
    transformationSummary.value = await resp.json();
  } else {
    const error = await resp.json();
    messageStore.publishMessage("Failed to apply transformation. Got message: " + error.message);
  }
};

const downloadTransformedOntology = async () => {
  const resp = await fetch(`${Constants.SERVER_URL}/transformation/ontology`, {
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
  <MatchesTable :matches="matches" :on-instance-change="onInstanceChange" :on-transform="applyTransformation" />
  <TransformationSummaryView :summary="transformationSummary" />
</template>
