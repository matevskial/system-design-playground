<template>
  <div class="about">
    <TitleHeader msg="Home" />
  </div>
</template>

<style>
@media (min-width: 1024px) {
  .about {
    min-height: 100vh;
    display: flex;
  }
}
</style>
<script setup lang="ts">
import TitleHeader from '@/components/TitleHeader.vue'
</script>
