<script setup lang="ts">
import {ref} from "vue";
import Constants from "@/constants/Constants";
import OntologyImport from "@/components/OntologyImport.vue";

const alreadyUploaded = ref(false);

const uploadedCheck = async () => {
    const resp = await fetch(`${Constants.SERVER_URL}/ontology`, {
        method: "HEAD",
        credentials: "include"
    });
    alreadyUploaded.value = resp.status === 200;
};
uploadedCheck();

const downloadOntologyFile = async () => {
    const resp = await fetch(`${Constants.SERVER_URL}/ontology`, {
        method: "GET",
        credentials: "include"
    });
    const blob = await resp.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = resp.headers.get("content-disposition")!.split(";")[1].split("=")[1].replaceAll("\"", "");
    a.click();
    window.URL.revokeObjectURL(url);
}
</script>

<template>
    <h3 class="text-h3 mb-6">Import Ontology and Patterns</h3>
    <div v-if="alreadyUploaded" class="mb-3">
        <p class="text-h6 mb-3">Ontology and patterns already imported. Uploading new ones will replace the old ones.</p>
        <RouterLink to="/matches">
            <v-btn color="primary" variant="tonal">Go to Pattern Matches</v-btn>
        </RouterLink>
        |
        <v-btn @click="downloadOntologyFile" color="primary" variant="tonal">Download ontology</v-btn>
    </div>
    <OntologyImport/>
</template>

<style scoped>

</style>
