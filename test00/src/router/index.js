import { createRouter, createWebHistory } from "vue-router";
import Start from "../views/Start.vue";
import MainUser from "../views/MainUser.vue";
import ProfileUser from "../views/ProfileUser.vue";
import ProfileCompany from "../views/ProfileCompany.vue";
import ViewCompany from "../views/ViewCompany.vue";
import MainCompany from "../views/MainCompany.vue";
import InterviewRoom from "../views/InterviewRoom.vue";
import SearchCompany from "../views/SearchCompany.vue";
import SearchUser from "../views/SearchUser.vue";
import Recruiting from "../views/Recruiting.vue";
import FinduserByTag from "@/views/FinduserBytag";
const routes = [
  {
    path: "/",
    name: "Start",
    component: Start,
  },
  {
    path: "/user",
    name: "MainUser",
    component: MainUser,
    beforeEnter: requireAuth,
  },
  {
    path: "/profileuser/:userindex",
    name: "ProfileUser",
    component: ProfileUser,
    beforeEnter: requireAuth,
  },
  {
    path: "/viewcompany",
    name: "ViewCompany",
    component: ViewCompany,
    beforeEnter: requireAuth,
  },
  {
    path: "/company",
    name: "MainCompany",
    component: MainCompany,
    beforeEnter: requireAuth,
  },
  {
    path: "/profilecompany/:companyindex",
    name: "ProfileCompany",
    component: ProfileCompany,
    beforeEnter: requireAuth,
  },
  {
    path: "/searchcompany/",
    name: "SearchCompany",
    component: SearchCompany,
    beforeEnter: requireAuth,
  },
  {
    path: "/searchuser/",
    name: "SearchUser",
    component: SearchUser,
    beforeEnter: requireAuth,
  },
  {
    path: "/user/interviewroom/:company/:url",
    name: "InterviewRoom",
    component: InterviewRoom,
    // 링크공유로 로그인안해도 들어올수있도록
  },
  {
    path: "/searchcompany",
    name: "searchcompany",
    component: SearchCompany,
    beforeEnter: requireAuth,
  },
  {
    path: "/recruiting/:companyindex",
    name: "recruiting",
    component: Recruiting,
    beforeEnter: requireAuth,
  },
  {
    path: "/company/findtag",
    name: "FinduserByTag",
    component: FinduserByTag,
    beforeEnter: requireAuth,
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

function requireAuth(to, from, next) {
  const { $cookies } = router.app.config.globalProperties;
  if ($cookies.get("PID_AUTH") == null) {
    alert("로그인이 필요한 서비스입니다");
    return next("/");
  }
  next();
}

export default router;
