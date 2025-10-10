<script setup lang="ts">
import type { TransformationSummary } from "@/types/TransformationSummary";
import { ref, watch } from "vue";
import NewEntitySummary from "@/components/match/NewEntitySummary.vue";

const props = defineProps<{
  summary: TransformationSummary | null;
}>();

const internalOpen = ref(props.summary !== null);

watch(
  () => props.summary,
  (summary) => {
    if (summary !== null) {
      internalOpen.value = true;
    }
  }
);

function onClose() {
  internalOpen.value = false;
}
</script>

<template>
  <v-dialog width="1080" v-model="internalOpen" persistent @keydown.esc="onClose()" id="transformation-summary">
    <v-card title="Transformation Summary">
      <v-container>
        <v-row>
          <v-col cols="2" class="font-weight-bold"> New entities:</v-col>
          <v-col cols="10">
            <ul class="mt-1 mb-1">
              <li v-for="entity in props.summary!.addedEntities" :key="entity.identifier" class="mb-1">
                <NewEntitySummary :entity="entity" />
              </li>
            </ul>
          </v-col>
        </v-row>
        <v-row v-if="props.summary!.addedStatements">
          <v-col cols="2" class="font-weight-bold"> Added statements:</v-col>
          <v-col cols="10">
            <pre class="text-pre-wrap">{{ props.summary!.addedStatements.trim() }}</pre>
          </v-col>
        </v-row>
        <v-row v-if="props.summary!.deletedStatements">
          <v-col cols="2" class="font-weight-bold"> Removed statements:</v-col>
          <v-col cols="10">
            <pre>
                            {{ props.summary!.deletedStatements }}
                            </pre
            >
          </v-col>
        </v-row>
      </v-container>
      <v-card-actions>
        <v-spacer></v-spacer>

        <v-btn text="Close" @click="onClose()"></v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<style scoped>
#transformation-summary {
  font-size: 0.875rem;
}
</style>
