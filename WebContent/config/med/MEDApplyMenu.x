# 
#  Title:检验条码打印Menu
# 
#  Description:检验条码打印Menu
# 
#  Copyright: Copyright (c) Javahis 2009
# 
#  author JiaoY 2009.05.26
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=apply;|;print;send;read;|;query;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=print;send;read;|;query;|;clear;|;close

apply.Type=TMenuItem
apply.Text=采样执行
apply.Tip=采样执行(Ctrl+H)
apply.M=H
apply.Action=onApply
apply.pic=convert.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印(Ctrl+P)
print.M=S
print.key=Ctrl+S
print.Action=onPrint
print.pic=print.gif

send.Type=TMenuItem
send.Text=传送条码机
send.Tip=传送条码机(Ctrl+T)
send.M=S
send.key=Ctrl+T
send.Action=onSend
send.pic=barcode.gif

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

read.Type=TMenuItem
read.Text=读卡
read.Tip=读卡(Ctrl+P)
read.M=Q
read.key=Ctrl+P
read.Action=onRead
read.pic=042.gif

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