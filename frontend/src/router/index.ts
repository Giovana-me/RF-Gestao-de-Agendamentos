import { createRouter, createWebHistory } from "vue-router";
import ProcedimentosView from "../views/ProcedimentosView.vue";
import MeusAgendamentosView from "../views/MeusAgendamentosView.vue";

const routes = [
  {
    path: "/",
    redirect: "/procedimentos"
  },
  {
    path: "/procedimentos",
    name: "procedimentos",
    component: ProcedimentosView
  },
  {
    path: "/meus-agendamentos",
    name: "meus-agendamentos",
    component: MeusAgendamentosView
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
