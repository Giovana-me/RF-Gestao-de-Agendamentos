<template>
  <section class="steps">
    <div
      class="step"
      :class="{ active: etapaAtual === 1, done: etapaAtual > 1 }"
    >
      <span>1</span>
      <p>Procedimento</p>
    </div>

    <div class="line"></div>

    <div
      class="step"
      :class="{ active: etapaAtual === 2, done: etapaAtual > 2 }"
    >
      <span>2</span>
      <p>Data</p>
    </div>

    <div class="line"></div>

    <div
      class="step"
      :class="{ active: etapaAtual === 3, done: etapaAtual > 3 }"
    >
      <span>3</span>
      <p>Hora</p>
    </div>

    <div class="line"></div>

    <div
      class="step"
      :class="{ active: etapaAtual === 4 }"
    >
      <span>4</span>
      <p>Detalhes</p>
    </div>
  </section>
</template>

<script setup>
defineProps({
  etapaAtual: Number
});
</script>

<style scoped>
.steps {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 35px;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.step span {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: #ffeeee;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  transition: .2s;
}

.step.active span {
  background: #dd81b5;
  color: white;
}

.step.done span {
  background: #dd81b5;
  color: white;
}

.line {
  width: 90px;
  height: 1px;
  background: #000;
  margin: 0 14px 28px;
}

.step p {
  margin: 0;
  font-size: 14px;
}
</style>