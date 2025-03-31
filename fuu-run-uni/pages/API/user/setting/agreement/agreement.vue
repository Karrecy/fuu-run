<template>
	<view>
			<rich-text :nodes="html"></rich-text>
	</view>
</template>

<script>
	import {
		getAgreement
	} from "@/request/apis/login.js"
	export default {
		data() {
			return {
				html:``
			}
		},
		onLoad() {
			getAgreement().then(res => {
				console.log(res);
				this.html = res.data
			})
		},
		onShow() {
		},	
		methods: {
			
		}
	}
</script>

<style>
	.rich-text {
	  padding: 20rpx;
	}
        body {
            font-family: "微软雅黑", Arial, sans-serif;
            line-height: 1.6;
            padding: 20px;
            background: #f9f9f9;
            color: #333;
          }
          h1, h2, h3 {
            color: #222;
          }
          h1 {
            text-align: center;
            margin-bottom: 20px;
          }
          .section {
            background: #fff;
            padding: 15px 20px;
            margin-bottom: 20px;
            border-radius: 5px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
          }
          p {
            margin: 10px 0;
          }
          ul {
            list-style: disc inside;
            margin: 10px 0;
          }
          .note {
            font-size: 0.9em;
            color: #666;
          }
    </style>