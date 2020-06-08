<template>
  <div class="home">
    <ExperimentReport url="results/report_exp.json"/>
  </div>
</template>

<script>
// @ is an alias to /src
import ExperimentReport from '@/components/ExperimentReport.vue'

export default {
  name: 'home',
  components: {
    ExperimentReport
  }
}
</script>
