import {
	tansParams
} from '@/utils/utils.js';
import {checkLogin,xcxLogin,getInfo,login} from "@/request/apis/login.js";
	import {getSchool} from "@/request/apis/school.js";

// 全局请求封装 http://localhost:8081
const domain = 'localhost' //使用localhost或局域网ip
// const domain = '你的域名'
const base_url = 'http://' + domain + ':8081'
// const base_url = 'https://' + domain + '/dev' // 部署环境
export const upload_url = base_url + '/system/oss/upload'
export const ws_url = 'ws://'+ domain +':4400'
// export const ws_url = 'wss://'+ domain +'/ws' // 部署环境

function uploadFile1(filePath, formData) {
	return new Promise((resolve, reject) => {
		let header = {
			'Content-Type': 'multipart/form-data'
		}
		let token = uni.getStorageSync('token')
		if (token) {
			header['Authorization'] = 'Bearer ' + token
		}
		uni.uploadFile({
			url: base_url + '/system/oss/upload',
			filePath: filePath,
			name: 'file',
			header: header, // 设置请求的 header

			formData: formData, // HTTP 请求中其他额外的 form data
			success: function(res) {
				if (res.statusCode == 200) {
					var json = res.data ? JSON.parse(res.data) : {};
					if (json.code == 200) {
						uni.hideLoading();
						resolve(json);
					} else {
						uni.showToast({
							title: res.data.msg,
							icon: 'none',
							duration: 1000
						})
						reject(json);
					}

				} else if (res.statusCode == 401) {
					login.call(this)
					reject(res);
				} else {
					uni.showToast({
						title: res.data.msg,
						icon: 'none',
						duration: 1000
					})
					reject(res.data);
				}
			},
			fail: function(err) {
				if (err.errMsg != null && err.errMsg.indexOf("request:fail") >= 0) {
					uni.showToast({
						title: err.msg,
						icon: 'none',
						duration: 1000
					})
					reject(null)
				} else {
					uni.showToast({
						title: err.msg,
						icon: 'none',
						duration: 1000
					})
					reject(err)
				}
			}
		})
	});
}

// 需要修改token，和根据实际修改请求头
function request1(params) {
	let url = params.url;
	let method = params.method || "get";
	let data = params.data || {};
	let param = params.params
	if (method === "get" && param) {
		url = url + "?" + tansParams(param)
	}

	let header = {
		'Content-Type': 'application/json',
		...params.header,
	};

	// 获取本地token
	if (uni.getStorageSync("token")) {
		header['Authorization'] = 'Bearer ' + uni.getStorageSync("token");
	}

	return new Promise((resolve, reject) => {
		if(params.showLoading == true) {
			uni.showLoading({
				title: "加载中..."
			})
		}
		uni.request({
			url: base_url + url,
			method: method,
			header: header,
			data: data,
			success(response) {
				console.log(response);
				const res = response.data
				// 根据返回的状态码做出对应的操作
				if (res.code == 200) {
					console.log(222);
					resolve(res);
				} else {
					switch (res.code) {
						case 401:
								console.log(666);
							login.call(this)
							// uni.reLaunch({
							// 	url:'/pages/tabBar/index/index'
							// })
							break;
						case 404:
							uni.showToast({
								title: '请求地址不存在...',
								duration: 2000,
							})
							break;
						case 500:
							console.log(111)
							reject(res.msg);
							break
						default:
							// uni.showToast({
							// 	title: res.msg,
							// 	duration: 2000,
							// 	icon: "none"
							// })
							break;
					}
				}
			},
			fail(err) {
				console.log(err)
				reject(err);
			},
			complete() {
		
			}
		});
	});
};


export const request = request1

export const uploadFile = uploadFile1
// module.exports = {
// 	request:request,
// 	uploadFile:uploadFile
// }