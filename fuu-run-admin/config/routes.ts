import component from "@/locales/bn-BD/component";
import { Routes } from "@umijs/renderer-react/dist/browser";
import route from "mock/route";

/**
 * @name umi 的路由配置
 * @description 只支持 path,component,routes,redirect,wrappers,name,icon 的配置
 * @param path  path 只支持两种占位符配置，第一种是动态参数 :id 的形式，第二种是 * 通配符，通配符只能出现路由字符串的最后。
 * @param component 配置 location 和 path 匹配后用于渲染的 React 组件路径。可以是绝对路径，也可以是相对路径，如果是相对路径，会从 src/pages 开始找起。
 * @param routes 配置子路由，通常在需要为多个路径增加 layout 组件时使用。
 * @param redirect 配置路由跳转
 * @param wrappers 配置路由组件的包装组件，通过包装组件可以为当前的路由组件组合进更多的功能。 比如，可以用于路由级别的权限校验
 * @param name 配置路由的标题，默认读取国际化文件 menu.ts 中 menu.xxxx 的值，如配置 name 为 login，则读取 menu.ts 中 menu.login 的取值作为标题
 * @param icon 配置路由的图标，取值参考 https://ant.design/components/icon-cn， 注意去除风格后缀和大小写，如想要配置图标为 <StepBackwardOutlined /> 则取值应为 stepBackward 或 StepBackward，如想要配置图标为 <UserOutlined /> 则取值应为 user 或者 User
 * @doc https://umijs.org/docs/guides/routes
 */
export default [
  {
    path: '/user',
    layout: false,
    routes: [
      {
        name: 'login',
        path: '/user/login',
        component: './User/Login',
      },
    ],
  },
  {
    path: '/welcome',
    name: 'welcome',
    icon: 'smile',
    component: './Welcome',
  },
  {
    path: '/admin',
    name: 'admin',
    icon: 'crown',
    access: 'canAdmin',
    routes: [
      {
        path: '/admin',
        redirect: '/admin/sub-page',
      },
      {
        path: '/admin/sub-page',
        name: 'sub-page',
        component: './Admin',
      },
    ],
  },

  // {
  //   name: 'dashboard',
  //   icon: 'dashboard',
  //   path: '/dashboard',
  //   routes: [
  //     {
  //       path: '/dashboard/analysis',
  //       name: 'analysis',
  //       component: './dashboard/analysis',
  //       perms: 'dashboard:analysis:view',
  //       access: 'routeFilter',
  //     },
  //     // {
  //     //   path: '/dashboard/monitor',
  //     //   name: 'monitor',
  //     //   component: './dashboard/monitor',
  //     //   perms: 'dashboard:monitor:view',
  //     //   access: 'routeFilter',
  //     // },
  //     // {
  //     //   path: '/dashboard/workbench',
  //     //   name: 'workbench',
  //     //   component: './dashboard/workbench',
  //     //   perms: 'dashboard:workbench:view',
  //     //   access: 'routeFilter',
  //     // },
  //   ]
  // },
  {
    name: 'user-manage',
    icon: 'user',
    path: '/userManage',
    routes: [
      {
        path: '/userManage/pc',
        name: 'pc-user',
        component: './userManage/pc',
        perms: 'system:userpc:view',
        access: 'routeFilter',
      },
      {
        path: '/userManage/xcx',
        name: 'mini-program-user',
        component: './userManage/xcx',
        perms: 'system:userwx:view',
        access: 'routeFilter',
      },
    ]
  },
  {
    name: 'system',
    icon: 'setting',
    path: '/system',
    routes: [
      {
        path: '/system/config',
        name: 'configuration',
        component: './system/config',
        perms: 'system:system:config:view',
        access: 'routeFilter',
      },
      {
        path: '/system/permission',
        name: 'permission',
        component: './system/permission',
        perms: 'system:system:perms:view',
        access: 'routeFilter',
      },
      {
        path: '/system/monitor',
        name: 'monitor',
        component: './system/monitor',
        perms: 'system:system:monitor:view',
        access: 'routeFilter',
      },
      {
        path: '/system/carousel',
        name: 'carousel',
        component: './system/carousel',
        perms: 'system:system:carousel:view',
        access: 'routeFilter',
      }
    ]
  },
  {
    name: 'order',
    icon: 'fileText',
    path: '/order',
    routes: [
      {
        path: '/order',
        component: './order/index',
        perms: 'order:order:view',
        access: 'routeFilter',
      },
      {
        path: '/order/detail/:id',
        component: './order/detail',
        perms: 'order:detail:view',
        access: 'routeFilter',
      },
    ],
  },
  {
    name: 'tag',
    icon: 'book',
    path: '/tag',
    component: './tag',
    perms: 'order:tag:view',
    access: 'routeFilter',
  },
  {
    name: 'workorder',
    icon: 'exception',
    path: '/workorder',
    routes: [
      {
        path: '/workorder/runnerApply',
        name: 'runner-application',
        component: './workorder/runnerApply',
        perms: 'runnerApply:view',
        access: 'routeFilter',
      },
      {
        path: '/workorder/orderAppeal',
        name: 'order-appeal',
        component: './workorder/orderAppeal',
        perms: 'order:appeal:view',
        access: 'routeFilter',
      },
    ]
  },
  {
    name: 'capital',
    icon: 'propertySafety',
    path: '/capital',
    routes: [
      {
        path: '/capital/wallet',
        name: 'wallet',
        component: './capital/wallet',
        perms: 'payment:withdraw:view',
        access: 'routeFilter',
      },
      {
        path: '/capital/withdrawal',
        name: 'withdrawal',
        component: './capital/withdrawal',
        perms: 'payment:recode:view',
        access: 'routeFilter',
      },
      {
        path: '/capital/flow',
        name: 'capital-flow',
        component: './capital/flow',
        perms: 'payment:flow:view',
        access: 'routeFilter',
      },
      {
        path: '/capital/mywallet',
        name: 'my-wallet',
        component: './capital/mywallet',
        perms: 'payment:mywallet:view',
        access: 'routeFilter',
      },
      {
        path: '/capital/myflow',
        name: 'my-flow',
        component: './capital/myflow',
        perms: 'payment:myflow:view',
        access: 'routeFilter',
      },
    ]
  },
  {
    name: 'school',
    icon: 'home',
    path: '/school',
    routes: [
      {
        path: '/school/campus',
        name: 'campus',
        component: './school/campus',
        perms: 'address:school:view',
        access: 'routeFilter',
      },
      {
        path: '/school/region',
        name: 'region',
        component: './school/region',
        perms: 'address:region:view',
        access: 'routeFilter',
      }
    ]
  },
  {
    name: 'oss',
    icon: 'star',
    path: '/oss',
    routes: [
      {
        path: '/oss/manage',
        name: 'oss-manage',
        component: './oss/manage',
        perms: 'oss:oss:view',
        access: 'routeFilter',
      },
      {
        path: '/oss/config',
        name: 'oss-config',
        component: './oss/config',
        perms: 'oss:config:view',
        access: 'routeFilter',
      }
    ]
  },
  {
    name: '个人中心',
    icon: 'star',
    path: '/profile',
    hideInMenu: true,
    component: './User/profile',
  },

  {
    path: '/',
    redirect: '/welcome',
  },
  {
    path: '*',
    layout: false,
    component: './404',
  },
];
