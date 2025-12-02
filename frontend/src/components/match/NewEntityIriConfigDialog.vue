<script setup lang="ts">
import { onMounted, ref } from "vue";
import { getNewEntityIdentifierConfig } from "@/api/NewEntityIdentifier";
import { EntityLocalNameFormatOptions, type NewEntityIdentifierConfig } from "@/types/NewEntityIdentifierConfig";

const emit = defineEmits(["close"]);

const props = defineProps<{
  show: boolean;
  onSave: (config: NewEntityIdentifierConfig) => void;
}>();

const originalConfig = ref<NewEntityIdentifierConfig>({
  localNameFormat: EntityLocalNameFormatOptions[0],
  namespace: ""
});

const config = ref<NewEntityIdentifierConfig>({
  localNameFormat: EntityLocalNameFormatOptions[0],
  namespace: ""
});

const handleSave = () => {
  props.onSave({ ...config.value });
};

const handleCancel = () => {
  config.value = { ...originalConfig.value };
  emit("close");
};
onMounted(() => {
  getNewEntityIdentifierConfig().then((value) => {
    originalConfig.value = value;
    config.value = value;
  });
});
</script>

<template>
  <v-dialog v-bind:model-value="props.show" width="auto">
    <v-card width="600" title="New entity IRI configuration">
      <v-card-text>
        <v-text-field v-model="config.namespace" label="Namespace" />
        <v-select
          v-model="config.localNameFormat"
          :items="EntityLocalNameFormatOptions"
          item-title="name"
          item-value="value"
          label="Local name format"
        />
      </v-card-text>
      <v-card-actions>
        <v-btn color="primary" @click="handleSave">Save</v-btn>
        <v-btn @click="handleCancel">Close</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
