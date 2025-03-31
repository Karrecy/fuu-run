<template>
  <!-- <nut-loading-page custom-color="skyblue" loading-color="skyblue" bg-color="#e8e8e8" :loading="true"  /> -->

</template>

<script>
	export default {
		data() {
			return {
				title: 'Hello',
				userInfo:{
					userWx:{}
				},
				visible1:false, //检查有没有选择校区dialog
			}
		},
		onLoad() {

		},
		onShow() {
			this.initData()
		},	
		methods: {
		
		}
	}
</script>
