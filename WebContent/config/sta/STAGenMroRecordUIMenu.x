# 
#  Title:医疗监管指标上传
#
#  Description:医疗监管指标上传
# 
#  Copyright: Copyright (c) Javahis 2012
# 
#  author shibl 2012.6.30
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;excel;|;log;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;excel;|;log;|;clear;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新(F5)
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


excel.Type=TMenuItem
excel.Text=汇出Excel
excel.Tip=打印(Ctrl+E)
excel.M=E
excel.key=Ctrl+E
excel.Action=onExport
excel.pic=exportexcel.gif


log.Type=TMenuItem
log.Text=查询日志
log.Tip=查询(Ctrl+L)
log.M=E
log.key=Ctrl+L
log.Action=onLog
log.pic=date.gif