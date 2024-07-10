<script setup lang="ts">
import Constants from "@/constants/Constants";
import {ref} from "vue";
import MatchesTable from "@/components/MatchesTable.vue";
import type {PatternMatch} from "@/types/PatternMatch";

const matches = ref<PatternMatch[]>([]);

const fetchMatches = async () => {
    const resp = await fetch(`${Constants.SERVER_URL}/matches`, {
        credentials: "include"
    });
    matches.value = await resp.json();
};
fetchMatches();
</script>

<template>
    <h3 class="text-h3 mb-6">Pattern matches</h3>
    <MatchesTable :matches="matches"/>
</template>
