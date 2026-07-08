import { createRouter, createWebHistory } from "vue-router";
import ProcedimentosView from "../views/ProcedimentosView.vue";
import MeusAgendamentosView from "../views/MeusAgendamentosView.vue";
import LoginView from "../views/LoginView.vue";

const routes = [
  {
    path: "/login",
    name: "login",
    component: LoginView
  },
  {
    path: "/",
    redirect: "/login"
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
