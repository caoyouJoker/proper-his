/*
*添加修改操作是否成功提示框,若strResult 为1 弹出提示框并在提示框执行完成后
*执行returnMethodOwn 方法，注意该方法必须在调用函数外部定义
*/
function alertInfoDEL(strResult,strInfo)//该方法2011-05-04 删除，改用jQueryUI实现该功能
{
	if(strResult=="1")
	{
		//注意回调方法是调用的父窗口的方法
		dialog("操作信息","text:"+strInfo,"150","20","","refreshPage();");
	}else{
      	dialog("操作信息","text:"+strInfo,"150","20","");
  }
}


//关闭dialog弹出层
function closeDialog(){
	parent.closediv();
}