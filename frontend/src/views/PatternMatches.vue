<script setup lang="ts">
import Constants from "@/constants/Constants"
import { ref } from "vue"
import MatchesTable from "@/components/MatchesTable.vue"
import type { PatternInstance } from "@/types/PatternInstance"
import type { PatternInstanceTransformation } from "@/types/PatternInstanceTransformation"
import { downloadAttachment } from "@/util/Utils"
import useMessageStore from "@/store/messageStore"

const messageStore = useMessageStore()

const matches = ref<PatternInstance[]>([])

const fetchMatches = async () => {
  const resp = await fetch(`${Constants.SERVER_URL}/matches`, {
    credentials: "include"
  })
  if (resp.status === 200) {
    matches.value = await resp.json()
  } else if (resp.status === 409) {
    messageStore.publishMessage("Ontology not uploaded, yet.")
  } else {
    messageStore.publishMessage("Unable to get pattern matches. Got message: " + resp.body)
  }
}
fetchMatches()

const applyTransformation = async (applyDeletes: boolean, instances: PatternInstanceTransformation[]) => {
  const resp = await fetch(`${Constants.SERVER_URL}/transformation`, {
    method: "PUT",
    body: JSON.stringify({
      applyDeletes,
      patternInstances: instances
    }),
    credentials: "include",
    headers: {
      "Content-Type": "application/json"
    }
  })
  if (resp.ok) {
    downloadAttachment(resp)
  } else {
    messageStore.publishMessage("Failed to apply transformation. Got message: " + resp.body)
  }
}
</script>

<template>
  <h3 class="text-h3 mb-6">Pattern matches</h3>
  <MatchesTable :matches="matches" :on-transform="applyTransformation" />
</template>
