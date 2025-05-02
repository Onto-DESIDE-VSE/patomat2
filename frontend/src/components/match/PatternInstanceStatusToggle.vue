<script setup lang="ts">
import { mdiCheck, mdiClose, mdiHelp } from "@mdi/js";

const props = defineProps<{
  status: boolean | null;
}>();

const emit = defineEmits(["update:status"]);

function toggleStatus() {
  if (props.status === null) {
    emit("update:status", true);
  } else if (props.status) {
    emit("update:status", false);
  } else {
    emit("update:status", null);
  }
}
</script>

<template>
  <v-btn
    :color="status === true ? 'primary' : status === false ? 'red' : 'grey-lighten-3'"
    title="Approve or reject this item"
    icon
    size="small"
    variant="flat"
    class="status-toggle-btn"
    @click="toggleStatus"
  >
    <v-icon :icon="status === true ? mdiCheck : status === false ? mdiClose : mdiHelp"></v-icon>
  </v-btn>
</template>

<style>
.status-toggle-btn,
.status-toggle-btn * {
  transition: none !important;
}
</style>
