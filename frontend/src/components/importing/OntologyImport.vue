<script setup lang="ts">
import { computed, ref, useTemplateRef } from "vue";
import { mdiMinus, mdiPlus, mdiWeb } from "@mdi/js";
import { uploadTransformationInput } from "@/api/OntologyStorageApi";
import { useRouter } from "vue-router";
import useMessageStore from "@/store/messageStore";
import PredefinedPatterns from "@/components/importing/PredefinedPatterns.vue";

const router = useRouter();
const messageStore = useMessageStore();

const ontologyFileOrUrl = ref<"file" | "URL">("file");
const showProgress = ref<boolean>(false);
const ontologyFile = ref<File>();
const ontologyUrl = ref<string>();
const patternFiles = ref<File[]>([]);
const patternUrls = ref<string[]>([]);
const predefinedPatternUrls = ref<string[]>([]);
const patternCount = ref<number>(1);
const resolveImports = ref<boolean>(false);

const inputRefs = useTemplateRef<HTMLInputElement[]>("inputs");

function addPatternInput() {
  patternCount.value++;
  patternUrls.value.push(patternUrls.value[0]);
  patternUrls.value[0] = "";
  if (inputRefs.value) {
    inputRefs.value[0].focus();
  }
}

function onPredefinedPatternsSelected(patterns: readonly any[]) {
  predefinedPatternUrls.value = [...patterns];
}

function removePattern(index: number) {
  patternCount.value--;
  patternUrls.value.splice(index, 1);
}

const valid = computed(() => {
  const anyPatternUrls =
    (patternUrls.value.length > 0 && patternUrls.value.some((v) => v.trim().length > 0)) ||
    predefinedPatternUrls.value.length > 0;
  if (ontologyFileOrUrl.value === "file") {
    return ontologyFile.value !== undefined && (anyPatternUrls || patternFiles.value.length > 0);
  } else {
    return (
      ontologyUrl.value !== undefined &&
      ontologyUrl.value.trim().length > 0 &&
      (anyPatternUrls || patternFiles.value.length > 0)
    );
  }
});

async function upload() {
  await uploadAndHandleResponse(() => router.push("/matches"));
}

async function uploadAndHandleResponse(onSuccess: () => Promise<any>) {
  if (!valid.value) {
    return;
  }
  showProgress.value = true;
  const resp = await uploadTransformationInput(
    ontologyFileOrUrl.value === "file" ? ontologyFile.value! : ontologyUrl.value!,
    patternFiles.value,
    [...patternUrls.value, ...predefinedPatternUrls.value],
    resolveImports.value
  );
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
  <v-form class="mt-6">
    <h4 class="text-h4">Ontology</h4>
    <v-switch
      v-model="ontologyFileOrUrl"
      true-value="file"
      false-value="URL"
      :label="`Load from ${ontologyFileOrUrl}`"
    ></v-switch>
    <v-file-input class="mb-2" v-if="ontologyFileOrUrl === 'file'" v-model="ontologyFile" label="Ontology file" />
    <v-text-field class="mb-2" v-else v-model="ontologyUrl" label="Ontology URL" :prepend-icon="mdiWeb"></v-text-field>
    <v-switch
      v-model="resolveImports"
      label="Resolve ontology imports"
      title="Should owl:imports be resolved and included in pattern processing?"
    />

    <h4 class="text-h4 mb-3">Transformation Patterns</h4>

    <PredefinedPatterns
      :selected-patterns="predefinedPatternUrls"
      :on-patterns-selected="onPredefinedPatternsSelected"
    ></PredefinedPatterns>

    <v-file-input
      class="mb-2"
      v-model="patternFiles"
      label="Transformation pattern files"
      hint="Or provide pattern files"
      persistent-hint
      multiple
    />

    <v-text-field
      v-for="i in patternCount"
      ref="inputs"
      :key="i"
      :append-icon="i > 1 ? mdiMinus : mdiPlus"
      :prepend-icon="mdiWeb"
      label="Transformation pattern URL"
      v-model="patternUrls[i - 1]"
      @click:append="i === 1 ? addPatternInput() : removePattern(i - 1)"
      class="mb-2"
      hint="Or provide URLs from which to load patterns"
      persistent-hint
    ></v-text-field>

    <div class="float-right">
      <v-btn color="primary" :disabled="!valid || showProgress" :loading="showProgress" @click="upload">Load </v-btn>
      <v-btn
        color="primary"
        :disabled="!valid || showProgress"
        :loading="showProgress"
        @click="uploadAndTransform"
        class="ml-2"
        >Load and transform
      </v-btn>
    </div>
  </v-form>
</template>
