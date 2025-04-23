<script setup lang="ts">
import { computed, watch } from "vue";

const props = defineProps({
  page: {
    type: Number,
    required: true
  },
  itemsPerPage: {
    type: Number,
    required: true
  },
  totalItems: {
    type: Number,
    required: true
  },
  itemsOptions: {
    type: Array,
    default: () => [5, 10, 20, 50, 100]
  }
});

const emit = defineEmits(["update:page", "update:itemsPerPage"]);

const modelPage = computed({
  get: () => props.page,
  set: (val) => emit("update:page", val)
});

const modelItemsPerPage = computed({
  get: () => props.itemsPerPage,
  set: (val) => emit("update:itemsPerPage", val)
});

const pageCount = computed(() => Math.ceil(props.totalItems / props.itemsPerPage));

const startIndex = computed(() => (props.page - 1) * props.itemsPerPage);

const endIndex = computed(() => {
  const end = props.page * props.itemsPerPage;
  return end > props.totalItems ? props.totalItems : end;
});

watch(modelItemsPerPage, () => {
  modelPage.value = 1;
});
</script>

<template>
  <v-row class="mx-2" justify="end">
    <div class="mr-1 my-auto text-lg-caption text-grey-darken-2">
      Items {{ startIndex + 1 }}–{{ endIndex }} of {{ totalItems }}
    </div>

    <v-pagination v-model="modelPage" :length="pageCount" :total-visible="6" color="primary"></v-pagination>

    <v-select
      v-model="modelItemsPerPage"
      :items="itemsOptions"
      label="Items per page"
      density="compact"
      outlined
      hide-details
      style="max-width: 120px"
    ></v-select>
  </v-row>
</template>
