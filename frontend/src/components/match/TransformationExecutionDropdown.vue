<script setup lang="ts">
import { mdiMenuDown } from "@mdi/js";

const props = defineProps<{
  executeInserts: () => void;
  executeInsertsAndDeletes: () => void;
  menuPosition: "top" | "bottom";
  disabled: boolean;
}>();
</script>

<template>
  <v-row class="align-center">
    <v-col cols="2">
      <v-menu :location="props.menuPosition">
        <template v-slot:activator="{ props }">
          <v-btn id="apply-transformation-top" color="primary" v-bind="props" :disabled="props.disabled" class="w-100">
            Apply transformation
            <v-icon dark end size="medium">{{ mdiMenuDown }}</v-icon>
          </v-btn>
        </template>
        <v-list>
          <v-list-item @click="props.executeInserts">
            <v-list-item-title>Apply only inserts</v-list-item-title>
          </v-list-item>
          <v-list-item @click="props.executeInsertsAndDeletes">
            <v-list-item-title>Apply deletes and inserts</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </v-col>
  </v-row>
</template>
