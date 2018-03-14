#
# TBuilder Config File 
#
# Title:
#
# Company:JavaHis
#
# Author:sunqy 2014.05.12
#
# version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;delete;|;order;|;print;|;onPrintBAE;|;transfer;|;create;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;delete;|;order;|;print;|;onPrintBAE;|;transfer;|;create;|;clear;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(query)
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
clear.Tip=清空(clear)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

order.Type=TMenuItem
order.Text=术中医嘱录入
order.Tip=术中医嘱录入
order.M=S
order.key=Ctrl+S
order.Action=onOpeOrder
order.pic=Create.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(close)
close.M=X
close.key=Alt+F4
close.Action=onClosePanel
close.pic=close.gif

print.Type=TMenuItem
print.Text=打印
print.zhText=打印
print.enText=Print
print.Tip=打印(Ctrl+P)
print.zhTip=打印
print.enTip=Print
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif


save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

onPrintBAE.Type=TMenuItem
onPrintBAE.Text=介入安全核查单
onPrintBAE.Tip=介入安全核查单
onPrintBAE.M=C
onPrintBAE.key=
onPrintBAE.Action=onPrintBAE
onPrintBAE.pic=print.gif

create.Type=TMenuItem
create.Text=生成交接单
create.Tip=生成交接单
create.M=X
create.key=Alt+F4
create.Action=onCreate
create.pic=save.gif

transfer.Type=TMenuItem
transfer.Text=交接一览表
transfer.Tip=交接一览表
transfer.M=X
transfer.key=Alt+F4
transfer.Action=onTransfer
transfer.pic=correct.gif