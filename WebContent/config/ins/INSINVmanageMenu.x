# 
#  Title:临床路径溢出原因表
# 
#  Description:临床路径溢出原因表
# 
#  Copyright: Copyright (c) Javahis 2011
# 
#  author pangben 2011.07.07
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;apply;|;download;|;print;|;clear;|;close;|;

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;apply;|;download;|;print;|;clear;|;close;|;

apply.Type=TMenuItem
apply.Text=申请
apply.Tip=申请
apply.M=E
apply.key=F4
apply.Action=onApply
apply.pic=008.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
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
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

download.Type=TMenuItem
download.Text=购领票据审批下载
download.Tip=购领票据审批下载
download.M=E
download.key=Ctrl+E
download.Action=onDownLoad
download.pic=001.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

