import { createRouter, createWebHistory } from "vue-router";
import HomeView from "../views/HomeView.vue";
import Constants from "@/constants/Constants";

const router = createRouter({
  history: createWebHistory(Constants.CONTEXT_PATH),
  routes: [
    {
      path: "/",
      name: "home",
      component: HomeView
    },
    {
      path: "/load",
      name: "load",
      component: () => import("../views/TransformationInputView.vue"),
      meta: {
        title: "Load ontology and patterns"
      }
    },
    {
      path: "/matches",
      name: "pattern-matches",
      component: () => import("../views/PatternMatches.vue"),
      meta: {
        title: "Pattern matches"
      }
    },
    {
      path: "/about",
      name: "about",
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import("../views/AboutView.vue"),
      meta: {
        title: "About"
      }
    }
  ]
});

export default router;
