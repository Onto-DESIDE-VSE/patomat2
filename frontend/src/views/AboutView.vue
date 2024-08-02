<script setup lang="ts">
import Changelog from "../components/Changelog.vue";
</script>

<template>
  <div class="about">
    <h1>About PatOMat2</h1>
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
    <p>
      Its development was supported by the EU’s Horizon Europe grant no. 101058682 (<a
        href="https://ontodeside.eu/"
        target="_blank"
        >Onto-DESIDE</a
      >).
    </p>
  </div>

  <Changelog />
</template>
