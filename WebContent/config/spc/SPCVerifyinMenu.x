 #
  # Title: 验收入库管理
  #
  # Description:验收入库管理
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2009-05-06
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;delete;|;query;|;clear;|;onExport;|;print;|;printcos;|;printnss;|;onExport2;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;delete;|;query;|;clear;|;onExport;|;print;|;printcos;|;printnss;|;onExport2;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除(Delete)
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

onExport.Type=TMenuItem
onExport.Text=引用订单
onExport.Tip=引用订单
onExport.M=E
onExport.Action=onExport
onExport.pic=045.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=P
print.Action=onPrint
print.pic=print.gif

printcos.Type=TMenuItem
printcos.Text=卫耗材打印
printcos.Tip=卫耗材打印
printcos.M=T
printcos.Action=onPrintcos
printcos.pic=print.gif

printnss.Type=TMenuItem
printnss.Text=膳食打印
printnss.Tip=膳食打印
printnss.M=U
printnss.Action=onPrintnss
printnss.pic=print.gif


onExport2.Type=TMenuItem
onExport2.Text=导出
onExport2.Tip=导出(Ctrl+A)
onExport2.M=A
onExport2.key=Ctrl+A
onExport2.Action=onExport2
onExport2.pic=export.gif