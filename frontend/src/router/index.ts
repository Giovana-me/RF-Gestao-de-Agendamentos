import { createRouter, createWebHistory } from "vue-router";
import ProcedimentosView from "../views/ProcedimentosView.vue";
import MeusAgendamentosView from "../views/MeusAgendamentosView.vue";
import LoginView from "../views/LoginView.vue";
import { estaAutenticado, isCliente, isProprietaria } from "../services/authService";
import AgendaView from "@/views/AgendaView.vue";

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
    path: "/agenda",
    name: "agenda",
    component: AgendaView,
    meta: {
      requiresAuth: true,
      role: "PROPRIETARIA"
    }
  },
  {
    path: "/procedimentos",
    name: "procedimentos",
    component: ProcedimentosView,
    meta: {
      requiresAuth: true,
      role: "CLIENTE"
    }
  },
  {
    path: "/meus-agendamentos",
    name: "meus-agendamentos",
    component: MeusAgendamentosView,
    meta: {
      requiresAuth: true,
      role: "CLIENTE"
    }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, from, next) => {
    if (to.meta.requiresAuth && !estaAutenticado()) {
      next("/login");
      return;
    }
    if (to.meta.role === "CLIENTE" && !isCliente()) {
      next("/agenda");
      return;
    }
    if (to.meta.role === "PROPRIETARIA" && !isProprietaria()) {
      next("/procedimentos");
      return
    }
    next();
});

export default router;
