<script setup lang="ts">
import { useRouter } from "vue-router";
import useMessageStore from "@/store/messageStore";
import { computed, ref } from "vue";
import { mdiMinus, mdiPlus } from "@mdi/js";
import Constants from "@/constants/Constants";

const router = useRouter();
const messageStore = useMessageStore();

const ontologyUrl = ref<string>();
const patternUrls = ref<string[]>([]);
const showProgress = ref<boolean>(false);
const patternCount = ref<number>(1);

const valid = computed(
  () => ontologyUrl.value !== undefined && ontologyUrl.value.trim().length > 0 && patternUrls.value.length > 0
);

async function submit() {
  showProgress.value = true;
  const resp = await fetch(`${Constants.SERVER_URL}/ontology/urls`, {
    credentials: Constants.SERVER_URL.length > 0 ? "include" : "same-origin",
    method: "POST",
    headers: {
      "Content-Type": Constants.MEDIA_TYPE_JSON
    },
    body: JSON.stringify({
      ontology: ontologyUrl.value!.trim(),
      patterns: patternUrls.value.filter((purl) => purl.trim().length > 0).map((purl) => purl.trim())
    })
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

function addPatternInput() {
  patternCount.value++;
  patternUrls.value.push(patternUrls.value[0]);
  patternUrls.value[0] = "";
}

function removePattern(index: number) {
  patternCount.value--;
  patternUrls.value.splice(index, 1);
}
</script>

<template>
  <v-overlay :model-value="showProgress" class="align-center justify-center">
    <v-progress-circular color="primary" size="64" indeterminate></v-progress-circular>
  </v-overlay>
  <v-form class="mt-2">
    <v-text-field v-model="ontologyUrl" label="Ontology URL"></v-text-field>
    <v-text-field
      v-for="i in patternCount"
      :key="i"
      :append-icon="i > 1 ? mdiMinus : mdiPlus"
      label="Transformation pattern URL"
      v-model="patternUrls[i - 1]"
      @click:append="i === 1 ? addPatternInput() : removePattern(i - 1)"
      class="mb-2"
    ></v-text-field>
    <div class="float-right">
      <v-btn color="primary" :disabled="!valid || showProgress" :loading="showProgress" @click="submit">Load </v-btn>
    </div>
  </v-form>
</template>

<style scoped></style>
