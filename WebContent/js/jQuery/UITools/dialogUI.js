function alert(msg, func_1){
    window.parent.parent.parent.parentalert(msg, func_1);
}

function confirm(msg, func_1, func_2){
    window.parent.parent.parent.parentconfirm(msg, func_1, func_2);
}

$(function(){
    var divobj = jQuery("<div id='dialog'></div>");
    divobj.attr("title", "提示");
    divobj.attr("id", "dialog");
    divobj.css("display", "none");
    jQuery("body").append(divobj);
});

function parentalert(msg, func_1){
    $("#dialog").text(msg);
    $("#dialog").dialog({
        autoOpen: true,
        width: 300,
        bgiframe: true,
        resizable: false,
        draggable: false,
        modal: true,
        stack: false,
        height: 190,
        width: 400,
        buttons: {
            "确定": function(){
                jQuery(this).dialog("close");
                try {
                    func_1();
                } 
                catch (e) {
                }
            }//, 
            //"Cancel": function() { 
            //	jQuery(this).dialog("close"); 
            //} 
        },
        dialogopen: function(){
        },
        beforeclose: function(){
            try {
               func_1();
            } 
            catch (e) {
            }
        }
    })
    setTimeout(function(){
        $("#dialog").focus()
    }, 5);
}

function parentconfirm(msg, func_1, func_2){
    $("#dialog").text(msg);
    $("#dialog").dialog({
        autoOpen: true,
        width: 300,
        bgiframe: true,
        resizable: false,
        draggable: false,
        modal: true,
        stack: false,
        height: 190,
        width: 400,
        buttons: {
            "确定": function(){
                jQuery(this).dialog("close");
                try {
                  func_1();
                } 
                catch (e) {
                }
            },
            "取消": function(){
                jQuery(this).dialog("close");
                try {
                	func_2();
                } 
                catch (e) {
                }
            }
        },
        dialogopen: function(){
        },
        beforeclose: function(){
            try {
              func_2();
            } 
            catch (e) {
            }
        }
    })
    setTimeout(function(){
        $("#dialog").focus()
    }, 5);
}

/*
*添加修改操作是否成功提示框,若strResult 为1 弹出提示框并在提示框执行完成后
*执行returnMethodOwn 方法，注意该方法必须在调用函数外部定义
*/

function alertInfo(strResult,strInfo)
{
	if(strResult=="1")
	{
		//注意回调方法是调用的父窗口的方法
		//dialog("操作信息","text:"+strInfo,"150","20","","refreshPage();");
		alert(strInfo,function(){refreshPage();});
	}else{
      	//dialog("操作信息","text:"+strInfo,"150","20","");
		alert(strInfo);
  }
}