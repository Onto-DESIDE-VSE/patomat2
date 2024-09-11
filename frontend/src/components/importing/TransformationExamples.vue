<script setup lang="ts">
import { onMounted, ref } from "vue";
import Constants from "@/constants/Constants";
import { useRouter } from "vue-router";
import useMessageStore from "@/store/messageStore";
import { mdiCircleSmall } from "@mdi/js";

const router = useRouter();
const messageStore = useMessageStore();

const examples = ref<string[]>([]);
const showProgress = ref<boolean>(false);

onMounted(async () => {
  examples.value = await fetch(`${Constants.SERVER_URL}/examples`, {
    method: "GET",
    credentials: "include"
  }).then((response) => response.json());
});

async function onApply(example: string) {
  showProgress.value = true;
  const resp = await fetch(`${Constants.SERVER_URL}/examples?name=${example}`, {
    method: "POST",
    credentials: "include"
  });
  showProgress.value = false;
  if (resp.ok) {
    messageStore.publishMessage("Ontology and patterns loaded.");
    await router.push("/matches");
  } else if (resp.status === 401) {
    messageStore.publishMessage("PatOMat2 is currently fully utilized. Please try again later.");
  } else {
    const error = await resp.json();
    messageStore.publishMessage(
      "Failed to load and process example ontology and patterns. Server responded with error: " + error.message
    );
  }
}
</script>

<template>
  <v-overlay :model-value="showProgress" class="align-center justify-center">
    <v-progress-circular color="primary" size="64" indeterminate></v-progress-circular>
  </v-overlay>
  <template v-if="examples.length > 0">
    <h3 class="text-h4 mb-3 mt-6">Transformation Examples</h3>
    <div style="max-width: 500px">
      <p>Try one of these examples if you want to see how PatOMat2 works.</p>
      <v-list v-for="example in examples" :key="example" density="compact">
        <v-list-item @click="onApply(example)">
          <template v-slot:prepend>
            <v-icon :icon="mdiCircleSmall"></v-icon>
          </template>
          {{ example }}
        </v-list-item>
      </v-list>
    </div>
  </template>
</template>

<style scoped></style>
