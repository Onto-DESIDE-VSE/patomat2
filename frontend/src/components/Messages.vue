<script setup lang="ts">
import useMessageStore from "@/store/messageStore"
import { computed, ref } from "vue"

const messages = useMessageStore().messages
const message = computed(() => (messages.length > 0 ? messages[0] : undefined))
const show = ref(true)
</script>

<template>
  <v-snackbar v-if="message" v-model="show">
    {{ message }}
  </v-snackbar>
</template>
