#############################################
# <p>Title:手术排程查询Menu </p>
#
# <p>Description:手术排程查询Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2009.12.09
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;print;info;opRecord;|;export;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;print;info;opRecord;|;export;|;clear;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印(Ctrl+P)
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

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

info.Type=TMenuItem
info.Text=手术详细资料
info.Tip=手术详细资料
info.Action=onInfo
info.pic=spreadout.gif

opRecord.Type=TMenuItem
opRecord.Text=手术记录
opRecord.Tip=手术记录
opRecord.Action=onOpRecord
opRecord.pic=031.gif

export.Type=TMenuItem
export.Text=导出
export.Tip=导出
export.M=E
export.key=Ctrl+E
export.Action=onExport
export.pic=export.gif

