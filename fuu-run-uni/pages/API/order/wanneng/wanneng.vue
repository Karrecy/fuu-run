<template>
	<template>
	  <nut-notify></nut-notify>
	</template>
	<scroll-div scroll-y style="height: 100vh;">
		<div class="box">
		<!-- 		<nut-cell @click="toAddAddress('start')" :title="'起点: '+submitForm.startAddress.title+' '+submitForm.startAddress.detail" :sub-title="submitForm.startAddress.name+' '+submitForm.startAddress.phone">
					<template #icon>
							<image class="nut-icon" src="/static/icons/start.png"></image>
					</template>
					<template #link>
						<nut-button @tap.stop="showStartAddresses" size="mini" type="primary">地址簿</nut-button>
					</template>
				</nut-cell> -->
				<nut-cell @click="toAddAddress('end')" :title="'终点  '+submitForm.endAddress.title+' '+submitForm.endAddress.detail" :sub-title="submitForm.endAddress.name+' '+submitForm.endAddress.phone">
					<template #icon>
						<image class="nut-icon" src="/static/icons/end.png"></image>
					</template>
					<template #link>
						<nut-button @tap.stop="showEndAddresses($event)" size="mini" type="primary">地址簿</nut-button>
					</template>
				</nut-cell>
				<nut-cell @click="showDatePopupClick" is-link title="配送时间" :desc="specifiedTimeText">
					<!-- <template #icon>
						<image class="nut-icon" src="/static/logo.png"></image>
					</template> -->
				</nut-cell>
				<nut-cell @click="showGenderPopup = true" is-link title="性别限制" :desc="genderText">
					<!-- <template #icon>
						<image class="nut-icon" src="/static/logo.png"></image>
					</template> -->
				</nut-cell>
			</div>
			<div class="box">
				<nut-cell @click="showServiceTypePopup = true" is-link title="物品类型" :desc="submitForm.tag">
					<template #icon>
						<image class="nut-icon" src="/static/icons/包裹.png"></image>
					</template>
				</nut-cell>
				<div style="position: relative;">
					<nut-textarea placeholder="请输入详细描述" v-model="submitForm.detail" limit-show max-length="100" />
					<div @click="showImagePopup = true" class="myImage">
						<nut-icon name="/static/icons/添加图片.png"></nut-icon>
						<span style="height: 100%;vertical-align: middle;">补充物品图</span>
					</div>
					<div @click="showFilePopup = true" class="myImage" style="color: #fa4700;left: 114px;">
						<nut-icon name="/static/icons/添加文件.png"></nut-icon>
						<span style="height: 100%;vertical-align: middle;">补充附件</span>
					</div>
				</div>
		
			</div>
			<div class="box">
		
				<nut-cell @click="showMoneyPopup = true" is-link title="追加金额" :desc="submitForm.additionalAmount">
					<template #icon>
						<image class="nut-icon" src="/static/icons/钱币.png"></image>
					</template>
				</nut-cell>
		<!-- 		<nut-cell is-link title="商品预估金额" desc="">
		
				</nut-cell> -->
		
			</div>
			<div class="box">
				<nut-cell @click="showCancelTimePopup = true" is-link title="自动取消" :desc="autoCancelText">
		
				</nut-cell>
				<nut-cell title="支付方式" desc="微信支付">
					<template #link>
						<image style="margin-left: 5px;" class="nut-icon" src="/static/icons/微信支付.png"></image>
					</template>
				</nut-cell>
			</div>
			
			<div class="orderBtnBox">
				<div class="boxLeft">
					<span class="boxPrice">{{totalPrice}}</span>
					<span class="boxPrice" style="font-size: small;"> 元</span>
				</div>
				<div class="boxRight">
					<button :loading="btnSubmitLoading" type="primary" class="myBtn" @click="submitOrder">提交订单</button>
				</div>
			</div>
			
			<div class="box" style="height: 15%;">
				<div style="width: 100%;height: 20%;">
				</div>
			</div>
				
		
			<template>
				<nut-popup v-model:visible="showGenderPopup" position="bottom" safe-area-inset-bottom>
					<nut-picker :columns="genderData" title="性别限制" 
						@cancel="showGenderPopup = false" @confirm="onGenderConfirm">
					</nut-picker>
				</nut-popup>
				<nut-popup v-model:visible="showDatePopup" position="bottom" safe-area-inset-bottom>
					<nut-date-picker v-model="currentDate" :min-date="minDate" :max-date="maxDate" type="datetime"
						:minute-step="15" is-show-chinese @confirm="onTimedConfirm" ok-text="指定时间"
						@cancel="onTimedCancel" cancel-text="尽快配送">
					</nut-date-picker>
				</nut-popup>
				<nut-popup v-model:visible="showServiceTypePopup" position="bottom" safe-area-inset-bottom>
					<div class="nut-picker__bar">
						<div class="nut-picker__cancel nut-picker__left nut-picker__button">取消</div>
						<div class="nut-picker__title">物品类型</div>
						<div @click="confirmTag" class="nut-picker__confirm nut-picker__right nut-picker__button">确认</div>
					</div>
					<div class="custom-content">
						<nut-tag 
							v-for="(item, index) in tagList"
							:key="index" 
							custom-color="#fa2400" 
							@click="setActiveTag(index)"
							:plain="index === activeTagIndex ? false : true">{{item.name}}</nut-tag>
						
						<div v-show="tagRemark != ''" class="remark">{{tagRemark}}</div>
						<div v-show="activeTagIndex == tagList.length-1">
							<nut-input  v-model="submitForm.tag" placeholder="其它" max-length="8" show-word-limit></nut-input>
						</div>
				<!-- 		<div class="range-box">
							<div style="margin-bottom: 26px;" class="remark">
								<span style="font-weight: bold;color: black;">物品重量: </span>{{submitForm.weight}} (按实际情况填写)
							</div>
							<nut-range v-model="rangeValue" :max="20" :min="2" @change="onWeightChange" />
						</div> -->
					</div>
		
		
				</nut-popup>
				<nut-popup v-model:visible="showImagePopup" position="bottom" safe-area-inset-bottom>
					<div class="nut-picker__bar">
						<div class="nut-picker__title">附件文件</div>
					</div>
					<div class="custom-content">
						<nut-uploader v-model:file-list="defaultImageList" :media-type="['image']" @file-item-click="imageClick" @success="uploadSuccess" @delete="uploadDelete"
							@oversize="oversize" :headers="headers" :data="uploaderData" :maximize="5242880" name="file" maximum="5"
							:url="uploadUrl"></nut-uploader>
					</div>
				</nut-popup>
				<nut-popup v-model:visible="showFilePopup" position="bottom" safe-area-inset-bottom>
					<div class="nut-picker__bar">
						<div class="nut-picker__title">附件文件</div>
					</div>
					<div class="custom-content" style="padding-bottom: 20px;">
						 <div 
							 @click="viewFile(item)"
							 v-for="(item, index) in defaultFileList" 
							 :key="index" 
							 class="fileBox">
							  <image style="float: left;height: 35px;width: 35px;margin: 5px;" :src="'/static/fileicon/'+item.icon" mode=""/>
							  <div class="title">
								<div class="name">{{item.name}}</div>
								<div class="size">{{item.size}}</div>
							  </div>
							  <image  @tap.stop="removeFile(index)"  class="fileDelIcon" src="/static/icons/del.png"></image>
				        </div>
						<nut-button :loading="btnFileLoading" @click="uploadFileMessage" block type="info">从微信聊天中上传</nut-button>
						
					</div>
				</nut-popup>
				<nut-popup v-model:visible="showMoneyPopup" position="bottom" safe-area-inset-bottom>
					<div class="nut-picker__bar">
						<div class="nut-picker__title">追加赏金可提高接单几率</div>
					</div>
					<div class="custom-content">
						<nut-input v-model="currAttachMoney" type="digit">
							<template #left>
								<nut-icon name="/static/icons/RMB.png"></nut-icon>
							</template>
		
							<template #right>
								<nut-button @click="onConfimAttachMoney" type="primary" size="small">确定追加</nut-button>
							</template>
		
						</nut-input>
					</div>
				</nut-popup>
				<nut-popup v-model:visible="showCancelTimePopup" position="bottom" safe-area-inset-bottom>
					<nut-picker  :columns="cancelTimes" title="未接单自动取消" @confirm="onConfirmCancelTime"
						@cancel="showCancelTimePopup = false">
					</nut-picker>
				</nut-popup>
				<nut-address
					v-model:visible="showStartAddressPopup" 
					type="exist" 
					:exist-address="startAddressList" 
					:is-show-custom-address="false" 
					@selected="selectedStartAddress" 
					exist-address-title="取货地址">
				</nut-address>
				<nut-address
					v-model:visible="showEndAddressPopup" 
					type="exist" 
					:exist-address="endAddressList" 
					:is-show-custom-address="false" 
					@selected="selectedEndAddress" 
					exist-address-title="送货地址">
				</nut-address>
			</template>
	</scroll-div>
	
</template>
<script setup>

</script>
<script>
	import {upload_url} from '@/request/request.js'
	
	import {
		toRaw
	} from "vue";
	import {
		postSubmitOrder
	} from "@/request/apis/order.js"
	import {
		listSchool
	} from "@/request/apis/school.js"
	import {
		getListTag
	} from "@/request/apis/tag.js"
	import {
		getListAddress
	} from "@/request/apis/address.js"
	import {
		uploadFile
	} from '@/request/request.js'
	import { useNotify } from 'nutui-uniapp/composables';
	export default {
		setup() {
		   const notify = useNotify();
		   const showPrimary = (message) => {notify.primary(message);};
		   const showSuccess = (message) => {notify.success(message);};
		   const showDanger = (message) => {notify.danger(message);};
		   const showWarning = (message) => {notify.warning(message);};
		   const hideNotify = () => {notify.hide();};
		   return {showPrimary,showSuccess,showDanger,showWarning,hideNotify};
		 },
		data() {
			return {
				btnSubmitLoading:false,
				btnFileLoading:false,
				defaultFileList: [], // 文件列表
				defaultFileList: [], // 文件列表
				defaultImageList: [], // 图片列表
				totalPrice:'--',
				uploadUrl:upload_url,
				specifiedTimeText:'尽快配送',
				genderText:'不限',
				uploaderData:{
					type:1,
					name:''
				},
				autoCancelText:'30分钟',
				currAttachMoney:0,
				activeTagIndex:-1,
				tagRemark:'',
				tagList:[
					{
						name:'其它',
						remark:''
					}
				],
				currSchool:null, //当前校区
				submitForm:{
					serviceType:2 , // 万能帮
					schoolId:null, // 学校id
					weight:null,  // 物品重量
					detail:'',  // 订单详情
					tag:'',  // 物品类型
					isTimed:0, // 是否指定配送时间
					specifiedTime:null, // 具体指定配送时间
					autoCancelTtl:1800, // 付款未接单自动取消时间 秒
					gender:2, // 限制性别 2不限
					additionalAmount:0, // 追加金额
					estimatedPrice:0, //商品预估价格
					attachImages:[], // 附件图片的ossid
					attachFiles:[], // 附件文件的ossid
					startAddress:{
						id:'',
						name:'',
						phone:'',
						title:'',
						detail:'',
						lat:'',
						lon:''
					},
					endAddress:{
						id:'',
						name:'',
						phone:'',
						title:'',
						detail:'',
						lat:'',
						lon:''
					},
				},
				headers: {
					Authorization: 'Bearer ' + uni.getStorageSync("token"),
					'Content-Type': 'multipart/form-data',
				},
				ossList: [],
				ossFileList: [],
				showGenderPopup: false,
				showDatePopup: false,
				showServiceTypePopup: false,
				showImagePopup: false,
				showMoneyPopup: false,
				showCancelTimePopup: false,
				showStartAddressPopup:false,
				showEndAddressPopup:false,
				showFilePopup:false,
				genderData: [{
						text: "不限",
						value: 2
					},
					{
						text: "男",
						value: 1
					},
					{
						text: "女",
						value: 0
					}
				],
				minDate: new Date(2020, 0, 1),
				maxDate: new Date(2025, 10, 1),
				currentDate: new Date(2022, 4, 10, 10, 10),
				rangeValue: 2,
				cancelTimes: [{
						text: '30分钟',
						value: 1800
					},
					{
						text: '1小时',
						value: 3600
					},
					{
						text: '2小时',
						value: 7200
					},
					{
						text: '3小时',
						value: 10800
					},
					{
						text: '5小时',
						value: 18000
					},
					{
						text: '10小时',
						value: 36000
					},
					{
						text: '24小时',
						value: 86400
					},
					{
						text: '48小时',
						value: 172800
					}
				],
				startAddressList:[],
				endAddressList:[],
			}
		},
		onLoad() {
			//this.initDate()
			this.initData()
			this.getAddresses()
			this.getTags()
			this.cacurlatePrice()
		},
		onReachBottom() {

		},
		methods: {
			
			canBePrinted(extension) {
				// 定义常见的可打印文件后缀
				  const printableExtensions = [
				    'pdf', // PDF 格式
				    'doc', 'docx', // Word 文档
				    'xls', 'xlsx', // Excel 文档
				    'ppt', 'pptx', // PowerPoint 演示文稿
				  ];
				
				  // 检查后缀名是否在可打印的格式列表中
				  return printableExtensions.includes(extension);
			},
			formatBytes(bytes, decimals = 2) {
				 if (bytes === 0) return '0 B';
				
				  const k = 1024; // 1KB = 1024B
				  const dm = decimals < 0 ? 0 : decimals; // 小数点位数
				  const sizes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']; // 各单位
				
				  const i = Math.floor(Math.log(bytes) / Math.log(k)); // 计算字节属于哪个单位
				  return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
			},
			getFileExtension(url) {
				const parts = url.split('.');
				return parts.length > 1 ? parts.pop().toLowerCase() : '';
			},
			getFileIcon(extension) {
				// 定义文件类型及其图标
				  const fileTypes = {
				    documents: {
				      extensions: ['pdf'],
				      icon: 'pdf-icon.png' // PDF文件图标
				    },
				    word: {
				      extensions: ['doc', 'docx'],
				      icon: 'word-icon.png' // Word文件图标
				    },
				    excel: {
				      extensions: ['xls', 'xlsx'],
				      icon: 'excel-icon.png' // Excel文件图标
				    },
				    powerpoint: {
				      extensions: ['ppt', 'pptx'],
				      icon: 'ppt-icon.png' // PowerPoint文件图标
				    },
				    unknown: {
				      extensions: [],
				      icon: 'unknown-icon.png' // 未知文件类型图标
				    },
				}
				 // 遍历每种文件类型，检查后缀并返回相应的图标
				  for (let category in fileTypes) {
				    if (fileTypes[category].extensions.includes(extension)) {
				      return fileTypes[category].icon;
				    }
				  }
				  // 如果找不到匹配的后缀，返回未知文件类型
				  return fileTypes.unknown.icon
			},
			viewFile(item) {
				console.log(item);
				uni.openDocument({
				  filePath: item.path,
				  showMenu: true,
				  success: function (res) {
				    console.log('打开文档成功');
				  },
				  fail(err) {
				  	console.log(err);
					uni.showToast({
						title:"文件打开失败",
						icon:'none'
					})
				  }
				});
			},
			removeFile(index) {
				this.defaultFileList.splice(index, 1)
				this.ossFileList.splice(index, 1)
			},
			uploadFileMessage() {
				let that = this
				let fileList = this.defaultFileList
				if(fileList.length >= 5) {
					this.showWarning("最多上传5个哦")
					return
				}
			
				uni.chooseMessageFile({
					count:5-fileList.length,
					type:"file",
					success:res => {
						console.log(res);
						let tempFiles = res.tempFiles
						for (var i = 0; i < tempFiles.length; i++) {
							let item = tempFiles[i]
							let name = item.name
							let path = item.path
							let size = item.size
							if(size > 5242880) {
								this.showWarning("文件大小不得超过5MB")
								return
							}
							size = that.formatBytes(size)
							item.size = size
							item.extension = that.getFileExtension(path)
							item.icon = that.getFileIcon(item.extension)
							if(!that.canBePrinted(item.extension)) {
								this.showWarning("仅支持doc、xls、ppt、pdf")
								return
							}
							that.btnFileLoading = true
							uni.uploadFile({
								url:upload_url,
								filePath:path,
								name:'file',
								formData:{
									type:2,
									name:name
								},
								header:that.headers,
								success(upRes) {
									console.log(upRes);
									let data = JSON.parse(upRes.data)
									data = data.data
									
									that.ossFileList.push(data.ossId)
									
									item.url = data.url
									that.defaultFileList.push(item) 
								},
								fail(err) {
									that.showDanger(err)
								},
								complete() {
									that.btnFileLoading = false
								}
							})
						}
					},
					complete:res => {
						//uni.hideLoading()
					}
				})
			},
			submitOrder() {
				let that = this
				let form = this.submitForm
				let ossIds = []
				let ossList = this.ossList
				// for (var i = 0; i < ossList.length; i++) {
				// 	ossIds.push(ossList[i].ossId)
				// }
				form.attachImages = ossList
				form.attachFiles = this.ossFileList
				that.btnSubmitLoading = true
				
				const SUBSCRIBE_ID = 'uQ8cRcy8jM8Rb09EUDZopOZgCLQcrxlFlGNzVez8_-w' // 模板ID
				if (wx.requestSubscribeMessage) {
				  wx.requestSubscribeMessage({
				    tmplIds: [SUBSCRIBE_ID],
				    complete() {
				    	postSubmitOrder(form).then(res => {
				    		console.log(res);
				    		uni.requestPayment({
				    			timeStamp: res.data.timeStamp, // 时间戳
				    			nonceStr: res.data.nonceStr, // 随机字符串
				    			package: res.data.packageValue,
				    			signType: res.data.signType, // 签名算法
				    			paySign: res.data.paySign, // 签名
				    			success: function (response) {
				    				console.log('支付成功',response);
				    				that.showSuccess("支付成功")
				    				setTimeout(() => {
				    					// 业务逻辑。。。
				    					uni.redirectTo({
				    						url:"/pages/API/order/detail/detail?id="+res.data.orderId
				    					})
				    				},1500)
				    				
				    			},
				    			fail: function (err) {
				    				console.log('支付失败',err);
				    				that.showDanger("支付失败")
				    				setTimeout(() => {
				    					// 业务逻辑。。。
				    					uni.redirectTo({
				    						url:"/pages/API/order/detail/detail?id="+res.data.orderId
				    					})
				    				},1500)
				    				
				    			},
				    			complete() {
				    				that.btnSubmitLoading = false
				    			}
				    		});
				    	}).catch(err => {
				    		that.showDanger(err)
				    	}).finally(res => {
				    		that.btnSubmitLoading = false
				    	})
				    }
				  });
				} else {
				  wx.showModal({
				    title: '提示',
				    content: '请更新您微信版本，来获取订阅消息功能',
				    showCancel: false
				  })
				}
				
				
				
			},
			cacurlatePrice() {
				let additionalAmount = Number(this.submitForm.additionalAmount)
				let floorPrice = Number(this.currSchool.floorPrice)
				console.log(additionalAmount);
				console.log(floorPrice);
				let totalPrice = (additionalAmount + floorPrice).toFixed(2);
				this.totalPrice = totalPrice
			},
			onGenderConfirm(e) {
				let item = e.selectedOptions[0]
				this.submitForm.gender = item.value
				this.genderText = item.text
				this.showGenderPopup = false
			},
			onConfirmCancelTime(e) {
				let item = e.selectedOptions[0]
				this.submitForm.autoCancelTtl = item.value
				this.autoCancelText = item.text
				this.showCancelTimePopup = false

			},
			onConfimAttachMoney() {
				let value = this.currAttachMoney;
				
				// 1. 去除非数字和小数点的字符
				value = value.replace(/[^\d.]/g, "");
				// 2. 确保只有一个小数点
				value = value.replace(/\.{2,}/g, "."); // 多个点替换为一个点
				value = value.replace(/^(\d*\.?)|(\d*)\.?/g, "$1$2"); // 保留第一个点
				// 3. 限制小数点后两位
				if (value.includes(".")) {
				const parts = value.split(".");
				value = `${parts[0]}.${parts[1].substring(0, 2)}`;
				}
				// 4. 去除前导0（保留"0."形式）
				value = value.replace(/^0+(\d)/, "$1");
				if (value.startsWith(".")) {
				value = "0" + value; // 如果以小数点开头，补全前导0
				}
				console.log(value);
				// 更新格式化后的值
				this.submitForm.additionalAmount = value;
				this.currAttachMoney = value
				this.cacurlatePrice()
				this.showMoneyPopup = false
				
			},
			onTimedConfirm(e) {
				console.log(e);
				let day = e.selectedOptions[2].value
				let hour = e.selectedOptions[3].value
				let minute = e.selectedOptions[4].value
				
				this.submitForm.isTimed = 1
				this.submitForm.specifiedTime = this.parseLocalDateTime(this.currentDate)
				this.specifiedTimeText = day+'日 '+hour+':'+minute
				this.showDatePopup = false
			},
			parseLocalDateTime(date) {
				const localDateTime = date.getFullYear() + "-" +
				    String(date.getMonth() + 1).padStart(2, '0') + "-" +
				    String(date.getDate()).padStart(2, '0') + " " +
				    String(date.getHours()).padStart(2, '0') + ":" +
				    String(date.getMinutes()).padStart(2, '0') + ":" +
				    String(date.getSeconds()).padStart(2, '0');
				return localDateTime
			},
			onTimedCancel() {
				this.submitForm.isTimed = 0
				this.submitForm.specifiedTime = null
				this.specifiedTimeText = '尽快配送'
				this.showDatePopup = false
			},
			confirmTag() {
				if(this.activeTagIndex == this.tagList.length - 1 && this.submitForm.tag =='') {
					this.showWarning("请先输入物品类型")
					return
				}
				if(this.activeTagIndex != this.tagList.length - 1) {
					this.submitForm.tag = this.tagList[this.activeTagIndex].name
				}
				this.showServiceTypePopup = false
			},
			setActiveTag(index) {
			      this.activeTagIndex = index; // 设置当前点击的索引
				  this.tagRemark = this.tagList[index].remark
			},
			initData(){
				this.currSchool = this.$store.state.currSchool;
				this.submitForm.schoolId = this.currSchool.id
			},
			updateAddress(data,type) {
				if(type == 'start') {
					this.submitForm.startAddress = data
				}
				else if(type == 'end') {
					this.submitForm.endAddress = data
				}
			},
			showStartAddresses(){
				this.showStartAddressPopup = true;
			},
			showEndAddresses(){
				this.showEndAddressPopup = true;
			},
			toAddAddress(type) {
				let data = null
				if(type == 'start') {
					data = JSON.stringify(this.submitForm.startAddress)
				}
				else {
					data = JSON.stringify(this.submitForm.endAddress)
				}
				uni.navigateTo({
					url:'/pages/API/address/add/add?data=' + data + '&type=' + type
				})
			},
			selectedStartAddress(prevExistAdd,nowExistAdd,arr) {
				if(nowExistAdd.provinceName + nowExistAdd.addressDetail == this.submitForm.endAddress.title + '-' +  this.submitForm.endAddress.detail) {
				this.showWarning("取送地址不能相同")
					return
				}
				this.submitForm.startAddress = {
					id:nowExistAdd.id,
					name:nowExistAdd.name,
					phone:nowExistAdd.phone,
					title:nowExistAdd.provinceName,
					detail:nowExistAdd.addressDetail.startsWith('-') ? nowExistAdd.addressDetail.slice(1) : str,
					lat:nowExistAdd.lat,
					lon:nowExistAdd.lon
				}
				
			},
			selectedEndAddress(prevExistAdd,nowExistAdd,arr) {
				if(nowExistAdd.provinceName + nowExistAdd.addressDetail == this.submitForm.startAddress.title + '-' +  this.submitForm.startAddress.detail) {
					this.showWarning("取送地址不能相同")
					return
				}
				this.submitForm.endAddress = {
					name:nowExistAdd.name,
					phone:nowExistAdd.phone,
					title:nowExistAdd.provinceName,
					detail:nowExistAdd.addressDetail.startsWith('-') ? nowExistAdd.addressDetail.slice(1) : str,
					lat:nowExistAdd.lat,
					lon:nowExistAdd.lon
				}
				
			},
			getTags(){
				getListTag({
					schoolId:this.currSchool.id,
					serviceType:this.submitForm.serviceType
				}).then(res => {
					console.log(res);
					this.tagList.unshift(...res.data)
				}).catch(err => {
					this.showWarning(err)
				})
			},
			getAddresses() {
				getListAddress().then(res => {
					console.log(res);
					let rows = res.data
					this.startAddressList = this.mapFields(rows)
					this.endAddressList = this.mapFields(rows)
				}).catch(err => {
					this.showWarning(err)
				})
			},
			imageClick(file) {
				file = file.fileItem
				console.log(file);
				let imgsArray = [];
				imgsArray[0] = file.url;
				uni.previewImage({
					current: 0,
					urls: imgsArray
				});
			},
			oversize(files) {
				console.log(files);
				this.showWarning('文件大小超出5MB')
			},
			uploadDelete(e) {
				let index = e.index
				this.ossList.splice(index, 1)
			},
			uploadSuccess(e) {
				console.log(e);
				let data = e.data.data
				data = JSON.parse(data)
				let ossId = data.data.ossId
				this.ossList.push(ossId)
			},

			onWeightChange(e) {
				console.log(e);
				if (e == 2) {
					this.submitForm.weight = '小于2KG'
				} else if (e == 20) {
					this.submitForm.weight = '大于20KG'
				} else {
					this.submitForm.weight = e + 'KG左右'
				}
			},
			showDatePopupClick() {
				this.initDate();
				this.showDatePopup = true
			},
			initDate() {
				const now = new Date();
				// 获取当前分钟数
				const currentMinutes = now.getMinutes();
				// 向上取整到最近的 15 的倍数
				const roundedMinutes = Math.ceil(currentMinutes / 15) * 15;
				// 如果超出 60 分钟，处理进位
				if (roundedMinutes >= 60) {
					now.setHours(now.getHours() + 1);
					now.setMinutes(0); // 分钟归零
				} else {
					now.setMinutes(roundedMinutes); // 设置为向上取整的分钟数
				}

				this.currentDate = now; // 当前时间
				this.minDate = now;
				// 设置最大时间为当前时间 + 7天
				const max = new Date();
				max.setDate(max.getDate() + 7); // 当前时间加7天
				this.maxDate = max;
			},
			mapFields(data) {
				 return data.map((formData) => ({
				    id: formData.id,
				    addressDetail: '-' + formData.detail,
				    cityName: '', // 默认值为空
				    countyName: '', // 默认值为空
				    provinceName: formData.title,
				    selectedAddress: false, 
				    townName: '', // 默认值为空
				    name: formData.name,
				    phone: formData.phone,
					lat:formData.lat,
					lon:formData.lon,
				  }));
			},
		},

	}
</script>

<style>
	@import url("/common/common.css");
	
	.nut-button--block {
		width: 90% !important;
		margin-left: 5% !important;
		margin-top: 20px !important;
	}
/* 	.nut-popup {
		border-radius: 10px 10px 0 0 !important;
	} */
	
	.orderBtnBox {
		box-sizing: border-box;
		 position: fixed;
		  bottom: 36px;
		  left: 5%;
		  width: 90%;
		  display: flex;
		  flex-direction: row;
		  align-items: center;
		  background-color: #fff;
		  border-top: 1px solid #eee;
		  box-shadow: 0 -2px 5px rgba(0, 0, 0, 0.1);
		  z-index: 100;
		      border-radius: 50px;
		      overflow: hidden;
	}
	.orderBtnBox .boxLeft {
		 flex: 1; /* 自适应宽度 */
		 background-color: #40465d;
		     height: 48px;
			 padding-left: 20px;
		
	}
	.orderBtnBox .boxRight {
		width: 112px;
	}
	.orderBtnBox .boxPrice {
			font-size: large;
			font-weight: bold;
		    line-height: 48px;
			color: white;
	}
	.orderBtnBox .myBtn {
			font-size: medium !important;
		    height: 48px;
		    line-height: 48px;
			border-radius: 0 !important;
			background-color: #cc6bf3 !important;
	}

	page {
		background: linear-gradient(180deg, #b300fa 0%, #f8f8f8 30%, #f8f8f8 100%);
	}

	.nut-cell__link {
		margin-left: 4px;
	}

	.nut-uploader {
		justify-content: space-between;
	}

	.nut-uploader__upload {
		margin: 0 10px 10px 0;
	}

	.myImage {
		position: absolute;
		bottom: 16px;
		left: 20px;
		height: 20px;
		line-height: 20px;
		z-index: 9;
		font-size: small;
		color: #b300fa;
		vertical-align: middle;
	}

	.nut-textarea {
		padding: 10px 20px 40px 20px !important;
	}

	.nut-textarea__limit {
		right: 20px !important;
		bottom: 16px !important;
	}

	textarea {
		height: 80px !important;
		font-size: small !important;
		padding: 8px !important;
		border-radius: 10px !important;
		background-color: #f7f8f9 !important;
	}

	.range-box {
		padding: 34px 10px;
	}

	.remark {
		font-size: small;
		color: gray;
		padding: 8px 10px;
	}

	

	.nut-input {
		border-radius: 15px !important;
		font-size: medium !important;
	}

	.nut-input--border {
		border: 1px solid red !important;
	}
</style>