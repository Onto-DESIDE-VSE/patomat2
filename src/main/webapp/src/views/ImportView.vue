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
</script>

<template>
    <h3 class="text-h3 mb-6">Import Ontology and Patterns</h3>
    <div v-if="alreadyUploaded" class="mb-3">
        <p class="text-h6">Ontology and patterns already imported. Uploading new ones will replace the old ones.</p>
        <RouterLink to="/matches">Go to Pattern Matches</RouterLink>
    </div>
    <OntologyImport/>
</template>

<style scoped>

</style>
