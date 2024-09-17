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
    <p class="mb-6">
      Its development was supported by the EU’s Horizon Europe grant no. 101058682 (<a
        href="https://ontodeside.eu/"
        target="_blank"
        >Onto-DESIDE</a
      >).
    </p>
    <div>
      <h4 class="text-h4 mb-2">Docs</h4>
      <p class="mb-2">
        Documentation for the REST API of this instance is available
        <a :href="`${Constants.SERVER_URL}/swagger-ui/index.html`" target="_blank noreferrer noopener">here</a>. Javadoc
        for the backend implementation is available at
        <a href="https://onto-deside-vse.github.io/patomat2/" target="_blank noreferrer noopener"
          >https://onto-deside-vse.github.io/patomat2/</a
        >.
      </p>

      <h2>Screencasts</h2>
      <div>
        <ul class="list-inside">
          <li>
            <a href="https://youtu.be/Au4Se0U_u8w"
              >Presentation and screencast for PatOMat2 (EKAW2024 demo paper submission)</a
            >
            (25 minutes)
          </li>
          <li>
            <a href="https://youtu.be/uWrm1spTF-o">Screencast for PatOMat2 (EKAW2024 demo paper submission)</a> (15
            minutes)
          </li>
          <li><a href="https://youtu.be/7Qzgkn2Tp4Q">Screencast for PatOMat2</a> (5 minutes)</li>
        </ul>
      </div>
    </div>
  </div>

  <Changelog />
</template>
<style scoped>
.list-inside {
  list-style: inside;
}
</style>
