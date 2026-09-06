import { createRouter, createWebHistory } from "vue-router";
import ProcedimentosView from "../views/ProcedimentosView.vue";
import MeusAgendamentosView from "../views/MeusAgendamentosView.vue";
import LoginView from "../views/LoginView.vue";
import { estaAutenticado, isCliente, isProprietaria } from "../services/authService";
import AgendaView from "@/views/AgendaView.vue";
import AdminLayout from "@/components/admin/AdminLayout.vue";
import CadastroView from "../views/CadastroView.vue";

const routes = [
  {
    path: "/login",
    name: "login",
    component: LoginView
  },
  {
    path: "/cadastro",
    name: "cadastro",
    component: CadastroView
  },
  {
    path: "/",
    redirect: "/login"
  },
  {
    path: "/agenda",
    component: AdminLayout,
    meta: {
      requiresAuth: true,
      role: "PROPRIETARIA"
    },
    children: [
      {
        path: "",
        name: "agenda",
        component: AgendaView
      }
    ]
  },
  {
    path: "/procedimentos-admin",
    component: AdminLayout,
    meta: {
      requiresAuth: true,
      role: "PROPRIETARIA"
    },
    children: [
      {
        path: "",
        name: "procedimentos-admin",
        component: () => import("@/views/GerenciarProcedimentosView.vue")
      }
    ]
  },
  {
    path: "/horarios-admin",
    component: AdminLayout,
    meta: {
      requiresAuth: true,
      role: "PROPRIETARIA"
    },
    children: [
      {
        path: "",
        name: "horarios-admin",
        component: () =>
          import("@/views/GerenciarHorariosView.vue")
      }
    ]
  },
  {
    path: "/clientes",
    component: AdminLayout,
    meta: {
      requiresAuth: true,
      role: "PROPRIETARIA"
    },
    children: [
      {
        path: "",
        name: "clientes",
        component: () => import("@/views/ClientesView.vue")
      }
    ]
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
    path: "/tabela-precos",
    name: "TabelaPrecos",
    component: () => import("@/views/TabelaPrecosView.vue"),
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
