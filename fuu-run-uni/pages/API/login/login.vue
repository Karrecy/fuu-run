<template>
	<nut-loading-page :loading="true" loading-text="登陆中" />
</template>

<script>
	export default {
		onLoad() {
			this.$root.login()
		}
	}
</script>

<style>
</style>