<script setup lang="ts">
import { computed, ref } from "vue"
import Constants from "@/constants/Constants"
import { useRouter } from "vue-router"
import useMessageStore from "@/store/messageStore"

const router = useRouter()
const messageStore = useMessageStore()

const ontologyFile = ref<File>()
const patternFiles = ref<File[]>([])
const uploading = ref<boolean>(false)

const valid = computed(() => ontologyFile.value !== undefined && patternFiles.value.length > 0)

const upload = async () => {
  uploading.value = true
  const formData = new FormData()
  formData.append("ontology", ontologyFile.value!)
  patternFiles.value.forEach((file) => formData.append("pattern", file))
  const resp = await fetch(`${Constants.SERVER_URL}/ontology`, {
    credentials: Constants.SERVER_URL.length > 0 ? "include" : "same-origin",
    method: "POST",
    body: formData
  })
  if (resp.ok) {
    uploading.value = false
    messageStore.publishMessage("Ontology and patterns uploaded.")
    router.push("/matches")
  } else {
    messageStore.publishMessage("Failed to upload and process ontology and patterns. Got message: " + resp.body)
  }
}
</script>

<template>
  <v-form class="mt-2">
    <v-file-input v-model="ontologyFile" label="Ontology file" />
    <v-file-input
      v-model="patternFiles"
      label="Pattern files"
      hint="Select at least one pattern file corresponding to the JSON format."
      multiple
    />

    <div class="float-right">
      <v-btn color="primary" :disabled="!valid || uploading" :loading="uploading" @click="upload">Import</v-btn>
    </div>
  </v-form>
</template>
