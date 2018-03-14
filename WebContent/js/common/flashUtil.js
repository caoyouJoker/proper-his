// 判断是否安装了 Flash 插件
function IsFlash7Enabled() {
	try {
		for (i = 7; i < 10; i++) {
			var obj = new ActiveXObject("ShockwaveFlash.ShockwaveFlash." + i);

			if (obj != null) {
				return true;
			}
		}
	} catch (e) {
	}
	return false;
}