
/**
 * 延时执行方法
 * 
 * @param {Object} method 方法
 * @param {Object} delaytime 延时时间
 */
function delayMethod(method,delaytime){
	var realDelayTime=200;
	if(delaytime!=null){
		realDelayTime=delaytime;
	}
	setTimeout(function(){
		method();
	},realDelayTime);
}