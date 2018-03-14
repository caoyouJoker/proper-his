# 
#  Title:挂号员工作量统计
# 
#  Description:挂号员工作量统计
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.12.22
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;export;|;detial;|;clear;|;print;|;close;|;

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=export;|;detial;|;print;|;Refresh;|;query;|;clear;|;close

export.Type=TMenuItem
export.Text=汇出
export.Tip=汇出
export.M=E
export.key=F4
export.Action=onExport
export.pic=exportexcel.gif

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

detial.Type=TMenuItem
detial.Text=明细
detial.Tip=明细
detial.M=D
detial.key=Ctrl+D
detial.Action=onDetial
detial.pic=detail.gif

