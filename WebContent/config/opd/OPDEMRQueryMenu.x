# 
#  Title:挂号历史查询
# 
#  Description:挂号历史查询
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.10.22
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;export;|;clear;|;read;|;bingli;|;patquery;|;caseSheet;|;close;|;

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=export;|;print;|;Refresh;|;query;|;clear;|;patquery;|;close

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

patquery.Type=TMenuItem
patquery.Text=病患查询
patquery.Tip=病患查询
patquery.M=
patquery.key=
patquery.Action=onPatQuery
patquery.pic=search-1.gif

read.Type=TMenuItem
read.Text=读卡
read.Tip=读卡(Ctrl+R)
read.M=
read.key=Ctrl+R
read.Action=onReadEKT
read.pic=042.gif

bingli.Type=TMenuItem
bingli.Text=病历
bingli.Tip=病历
bingli.M=
bingli.key=
bingli.Action=onErdSheet
bingli.pic=search.gif

caseSheet.Type=TMenuItem
caseSheet.Text=打印处方签
caseSheet.Tip=打印处方签
caseSheet.M=
caseSheet.key=
caseSheet.Action=onCaseSheet
caseSheet.pic=018.gif

