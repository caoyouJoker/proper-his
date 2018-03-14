# 
#  Title:票据资料汇出
# 
#  Description:票据资料汇出
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;exeContract;|;export;|;print;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;exeContract;|;export;|;print;|;clear;|;close

export.Type=TMenuItem
export.Text=汇出
export.Tip=汇出
export.M=E
export.key=F4
export.Action=onExport
export.pic=exportexcel.gif

exeContract.Type=TMenuItem
exeContract.Text=记账单位结算
exeContract.Tip=记账单位结算
exeContract.M=C
exeContract.key=Ctrl+C
exeContract.Action=exeContract
exeContract.pic=Commit.gif

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
print.M=B
print.Action=onPrint
print.pic=print.gif