<template>
  <AdminLayout>
    <div class="agenda-container">

      <div class="agenda-card">

        <div class="agenda-toolbar">

          <button class="nav-button">
            ◀
          </button>

          <select>
            <option>Março</option>
          </select>

          <select>
            <option>2026</option>
          </select>

          <button class="nav-button">
            ▶
          </button>

        </div>

        <div class="agenda-calendar">

          Calendário será adicionado aqui

        </div>

      </div>

    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import AdminLayout from '@/components/admin/AdminLayout.vue';
</script>

<style scoped>

.agenda-container{
    height: 100%;
    width: 100%;
}

.agenda-card{
    background:white;
    border-radius:18px;
    padding:30px;
    min-height:700px;
    box-shadow:0 0 12px rgba(0,0,0,.15);
}

.agenda-toolbar{
    display:flex;
    align-items:center;
    justify-content:space-between;
    gap:15px;
    margin-bottom:30px;
}

.agenda-toolbar select{
    flex:1;
    height:42px;
}

.nav-button{
    background:none;
    border:none;
    font-size:22px;
    cursor:pointer;
}

.agenda-calendar{
    height:600px;

    display:flex;
    justify-content:center;
    align-items:center;

    border:1px dashed #ccc;
}

</style>