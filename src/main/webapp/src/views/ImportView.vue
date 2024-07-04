<script setup lang="ts">
import {computed, ref} from "vue";
import Constants from "@/constants/Constants";

const ontologyFile = ref<File>();
const patternFiles = ref<File[]>([]);
const uploading = ref<boolean>(false);

const valid = computed(() => ontologyFile.value !== undefined && patternFiles.value.length > 0);

const upload = async () =>  {
    uploading.value = true;
    const formData = new FormData();
    formData.append("ontology", ontologyFile.value!);
    patternFiles.value.forEach(file => formData.append("pattern", file));
    await fetch(`${Constants.SERVER_URL}/ontology`, {
        method: "POST",
        body: formData
    });
    uploading.value = false;
};

</script>

<template>
    <h3 class="text-h3">Import Ontology and Patterns</h3>
    <v-form class="mt-2">
        <v-file-input v-model="ontologyFile" label="Ontology file"/>
        <v-file-input v-model="patternFiles" label="Pattern files" multiple/>

        <div class="float-right">
        <v-btn color="primary" :disabled="!valid || uploading" :loading="uploading" @click="upload">Import</v-btn>
        </div>
    </v-form>
</template>

<style scoped>

</style>
