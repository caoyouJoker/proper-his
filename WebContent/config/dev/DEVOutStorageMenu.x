 #
 # Title: 设备类别参数设定设定
 #
 # Description:设备类别参数设定设定
 #
 # Copyright: JavaHis (c) 2008
 #
 # @author sundx
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;quary;clear;creatOutRequest;print;|;close

Window.Type=TMenu
Window.Text=窗口 
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;quary;clear;creatOutRequest;print;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

quary.Type=TMenuItem
quary.Text=查询
quary.Tip=查询
quary.M=Q
quary.key=Ctrl+F
quary.Action=onQuery
quary.pic=query.gif


clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

creatOutRequest.Type=TMenuItem
creatOutRequest.Text=导入请购单
creatOutRequest.Tip=导入请购单
creatOutRequest.M=C
creatOutRequest.key=Ctrl+I
creatOutRequest.Action=onGenerateReceipt  
creatOutRequest.pic=039.gif


onReject.Type=TMenuItem
onReject.Text=驳回
onReject.Tip=驳回
onReject.M=r
onReject.key=Ctrl+R
onReject.Action=onReject
onReject.pic=Undo.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=P  
print.key=Ctrl+P 
print.Action=onPrint 
print.pic=print.gif 


close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif