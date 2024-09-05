<script setup lang="ts">
import { useRouter } from "vue-router";
import useMessageStore from "@/store/messageStore";
import { computed, ref } from "vue";
import Constants from "@/constants/Constants";

const router = useRouter();
const messageStore = useMessageStore();

const ontologyUrl = ref<string>();
const patternUrls = ref<string[]>([]);
const showProgress = ref<boolean>(false);

const valid = computed(
  () => ontologyUrl.value !== undefined && ontologyUrl.value.trim().length > 0 && patternUrls.value.length > 0
);

async function submit() {
  showProgress.value = true;
  const resp = await fetch(`${Constants.SERVER_URL}/ontology/urls`, {
    credentials: Constants.SERVER_URL.length > 0 ? "include" : "same-origin",
    method: "POST",
    body: JSON.stringify({ ontology: ontologyUrl.value, patterns: patternUrls.value })
  });
  showProgress.value = false;
  if (resp.ok) {
    messageStore.publishMessage("Ontology and patterns loaded.");
    await router.push("/matches");
  } else if (resp.status === 401) {
    messageStore.publishMessage("PatOMat2 is currently fully utilized. Please try again later.");
  } else {
    messageStore.publishMessage("Failed to load and process ontology and patterns. Got message: " + resp.body);
  }
}
// TODO Form
</script>

<template>
  <v-overlay :model-value="showProgress" class="align-center justify-center">
    <v-progress-circular color="primary" size="64" indeterminate></v-progress-circular>
  </v-overlay>
  <v-form class="mt-2">
    <div class="float-right">
      <v-btn color="primary" :disabled="!valid || showProgress" :loading="showProgress" @click="submit">Load</v-btn>
    </div>
  </v-form>
</template>

<style scoped></style>
