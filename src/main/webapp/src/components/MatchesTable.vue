<script setup lang="ts">

import type {PatternMatch, ResultBinding} from "@/types/PatternMatch";

const props = defineProps<{ matches: PatternMatch[] }>();

const headers = [{
    title: "Pattern File",
    value: "patternFile"
}, {
    title: "Matching Bindings",
    value: "bindings"
}];

function valueToString(binding: ResultBinding) {
    if (binding.datatype === "http://www.w3.org/2000/01/rdf-schema#Resource") {
        return `<${binding.value}>`;
    } else {
        return `${binding.value}^^${binding.datatype}`;
    }
}
</script>

<template>
    <v-data-table :headers="headers" :items="props.matches" show-select>
        <template v-slot:item.bindings="{ value }">
            <ul>
                <li v-for="binding in value">{{ binding.name }}: {{ valueToString(binding) }}</li>
            </ul>
        </template>
    </v-data-table>
</template>
