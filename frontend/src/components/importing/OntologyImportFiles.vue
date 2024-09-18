<script setup lang="ts">
import { computed, ref } from "vue";
import { useRouter } from "vue-router";
import useMessageStore from "@/store/messageStore";
import { uploadTransformationInputFiles } from "@/api/OntologyStorageApi";

const router = useRouter();
const messageStore = useMessageStore();

const ontologyFile = ref<File>();
const patternFiles = ref<File[]>([]);
const showProgress = ref<boolean>(false);

const valid = computed(() => ontologyFile.value !== undefined && patternFiles.value.length > 0);

async function upload() {
  await uploadAndHandleResponse(() => router.push("/matches"));
}

async function uploadAndHandleResponse(onSuccess: () => Promise<any>) {
  showProgress.value = true;
  const resp = await uploadTransformationInputFiles(ontologyFile.value!, patternFiles.value);
  showProgress.value = false;
  if (resp.ok) {
    messageStore.publishMessage("Ontology and patterns uploaded.");
    await onSuccess();
  } else if (resp.status === 401) {
    messageStore.publishMessage("PatOMat2 is currently fully utilized. Please try again later.");
  } else {
    const error = await resp.json();
    messageStore.publishMessage(
      "Failed to load and process ontology and patterns. Server responded with error: " + error.message
    );
  }
}

async function uploadAndTransform() {
  await uploadAndHandleResponse(() => router.push({ name: "pattern-matches", query: { transform: "true" } }));
}
</script>

<template>
  <v-overlay :model-value="showProgress" class="align-center justify-center">
    <v-progress-circular color="primary" size="64" indeterminate></v-progress-circular>
  </v-overlay>
  <v-form class="mt-2">
    <v-file-input v-model="ontologyFile" label="Ontology file" />
    <v-file-input
      v-model="patternFiles"
      label="Transformation pattern files"
      hint="Select at least one transformation pattern file corresponding to the required JSON format."
      persistent-hint
      multiple
    />

    <div class="float-right">
      <v-btn color="primary" :disabled="!valid || showProgress" :loading="showProgress" @click="upload">Load</v-btn>
      <v-btn
        color="primary"
        :disabled="!valid || showProgress"
        :loading="showProgress"
        @click="uploadAndTransform"
        class="ml-2"
        >Load and transform</v-btn
      >
    </div>
  </v-form>
</template>
