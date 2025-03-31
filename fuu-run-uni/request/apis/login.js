// 引入 request 文件
import { request } from '@/request/request.js'
import {getSchool} from "@/request/apis/school.js";

export const login = async function () {
  // let isLogined = await checkLogin();
  if (true) {
    uni.showLoading({
      title: "登陆中...",
      duration: 2000,
    });
    let code = await getCode();
    let loginRes = await xcxLogin({ xcxCode: code });
    let token = loginRes.data.token;
    uni.setStorageSync("token", token);
  }
  let info = await getInfo();
  console.log(info);
  // 在这里使用 this.$store 需要确保上下文正确
  this.$store.commit("login", info.data.user);
  this.$store.commit("setConfig", info.data.config);
  checkSchool.call(this);  // 确保 this 指向 Vue 实例
};

async function getCode() {
  const code = await getLoginCode();
  return code;
}

function checkSchool() {
  let school = uni.getStorageSync("currentSchool");
  if (school == null || school == undefined || school == "") {
	  this.$store.commit("setAppLaunch", true);
    uni.navigateTo({
      url: "/pages/API/school/select/select",
    });
  } else {
    getSchool(school.id).then((res) => {
      console.log(res);
      this.$store.commit("setSchool", res.data);
      uni.setStorageSync("currentSchool", res.data);
	  this.$store.commit("setAppLaunch", true);
    });
  }
}

const getLoginCode = () => {
  return new Promise((resolve, reject) => {
    uni.login({
      success: (res) => {
        if (res.code) {
          resolve(res.code); // 返回 code
        } else {
          reject('获取 code 失败');
        }
      },
      fail: (err) => {
        reject('登录失败: ' + err.errMsg); // 错误处理
      },
    });
  });
};

// 检查是否需要登录
export const checkLogin = () => {
  return request({
    url: '/xcxCheckLogin',
    method: 'get',
  });
};

// 登录
export const xcxLogin = (params) => {
  return request({
    url: '/xcxLogin',
    method: 'post',
    data: params,
    header: { 'Content-Type': 'application/x-www-form-urlencoded' },
  });
};

// 查询学习列表
export const getInfo = () => {
  return request({
    url: '/getInfo',
    method: 'get',
  });
};

// 获取用户协议
export const getAgreement = () => {
  return request({
    url: '/agreement',
    method: 'get',
  });
};

// 获取轮播图
export const getCarousel = () => {
  return request({
    url: '/carousel',
    method: 'get',
  });
};
