import { createRouter, createWebHistory } from "vue-router";
import ProcedimentosView from "../views/ProcedimentosView.vue";
import MeusAgendamentosView from "../views/MeusAgendamentosView.vue";
import LoginView from "../views/LoginView.vue";
import { estaAutenticado } from "../services/authService";

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
    component: ProcedimentosView,
    meta: {
        requiresAuth: true
    }
  },
  {
    path: "/meus-agendamentos",
    name: "meus-agendamentos",
    component: MeusAgendamentosView,
    meta: {
        requiresAuth: true
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
    next();
});

export default router;
