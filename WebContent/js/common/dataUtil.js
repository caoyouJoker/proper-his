/**
 * 执行ajax调用方法，调用该方法，可重写succes和error对应方法
 * @param {Object} url
 * @param {Object} params
 * @param {Object} returnMethod
 * @param {Object} successMethod
 * @param {Object} errorMethod
 * @param {Object} isAsync 是否同步访问
 */
function postMethodWithAjaxWithMethodIn(url,params,returnMethod,successMethod,errorMethod,isAsync,formid){
	if(isAsync==null)isAsync=true;
	if(isAsync==undefined)isAsync=true;
	if(!checkSessionValid()){return;}
	var datastr="";
	if(formid!=""&&formid!=undefined){
		datastr=jQuery("#"+formid).serialize();
	}
	url=url+"?sysSign=1&"+datastr;
	jQuery.ajax({
		"type":"post",
		"url":url,
		"dataType":"json",
		"data":params,
		"async": isAsync,
		"timeout":50000,
		"success":successMethod,
		"failure":errorMethod
	})
	
}

/**
 *
 * @param {Object} url
 * @param {Object} params
 * @param {Object} returnMethod
 * @param {Object} formid
 */
function postMethodWithAjaxReturnDivMessage(url, params, returnMethod, formid){
    function successMethod(data, statusText){
        closeDialog();
        parent.alertInfo(1, data.message);
        returnMethod();
    }
    function errorMethod(data, statusText){
        alert("未知错误！");
        returnMethod();
    }
    var datastr = "";
    if (formid != "" && formid != undefined) {
        datastr = jQuery("#" + formid).serialize();
    }
    url = url + "?sysSign=1&" + datastr;
    jQuery.ajax({
        "type": "post",
        "url": url,
        "dataType": "json",
        "data": params,
        "success": successMethod,
        "error": errorMethod
    })
    
}
function postMethodWithAjaxReturnAlertMessage(url, params, returnMethod, formid){
    function successMethod(data, statusText){
       alert(data.message,function(){
        returnMethod();
       });
    }
    function errorMethod(data, statusText){
        alert("未知错误！"+data);
        returnMethod();
    }
    var datastr = "";
    if (formid != "" && formid != undefined) {
        datastr = jQuery("#" + formid).serialize();
    }
    url = url + "?positionSign=0&" + datastr;
    jQuery.ajax({
        "type": "post",
        "url": url,
        "dataType": "json",
        "data": params,
        "success": successMethod,
        "error": errorMethod
    })
    
}

/*
 * 执行ajax调用方法，调用该方法，可重写succes和error对应方法
 */
function postMethodWithAjaxWithMethodIn(url, params, returnMethod, successMethod, errorMethod){
    jQuery.ajax({
        "type": "post",
        "url": url,
        "dataType": "json",
        "data": params,
        "success": successMethod,
        "error": errorMethod
    }) 
}

/*
 * 执行ajax调用方法，调用该方法，可重写succes和error对应方法
 */
function postAjax(url, params, returnMethod, successMethod, errorMethod)
{
    function successMethod(data, statusText)
    {
		returnMethod(data);
    }
    function errorMethod(data, statusText)
    {
		returnMethod(data);
    }
    jQuery.ajax({
        "type": "post",
        "url": url,
        "dataType": "json",
        "data": params,
        "success": successMethod,
        "error": errorMethod
    })   
}