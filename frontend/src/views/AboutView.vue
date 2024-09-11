<script setup lang="ts">
import Changelog from "../components/Changelog.vue";
import Constants from "@/constants/Constants";
</script>

<template>
  <div class="about">
    <h3 class="text-h3 mb-6">About PatOMat2</h3>
    <p class="mb-1">
      The PatOMat2 application aims at pattern-based transformation of existing ontologies. It allows bridging different
      modeling styles of web ontologies.
    </p>
    <p class="mb-1">
      Its source code is available at
      <a href="https://github.com/Onto-DESIDE-VSE/patomat2" target="_blank"
        >https://github.com/Onto-DESIDE-VSE/patomat2</a
      >.
    </p>
    <p class="mb-3">
      Its development was supported by the EU’s Horizon Europe grant no. 101058682 (<a
        href="https://ontodeside.eu/"
        target="_blank"
        >Onto-DESIDE</a
      >).
    </p>
    <div>
      <h2>API Docs</h2>
      <p>
        Documentation for the REST API of this instance is available
        <a :href="`${Constants.SERVER_URL}/swagger-ui/index.html`" target="_blank noreferrer noopener">here</a>.
      </p>
    </div>
  </div>

  <Changelog />
</template>
