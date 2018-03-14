#############################################
# <p>Title:手术记录Menu </p>
#
# <p>Description:手术记录Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2009.09.28
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=Query;|;opstmp;|;print;|;export;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.zhText=窗口
Window.enText=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.zhText=文件
File.enText=File
File.M=F
File.Item=query;|;opstmp;|;print;|;export;|;clear;|;close

query.Type=TMenuItem
query.Text=查询
query.zhText=查询
query.enText=Query
query.Tip=查询(Ctrl+F)
query.zhTip=保存(Ctrl+F)
query.enTip=Save(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.zhText=刷新
Refresh.enText=Refresh
Refresh.Tip=刷新(F5)
Refresh.zhTip=刷新
Refresh.enTip=Refresh
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=清空
clear.zhText=清空
clear.enText=Empty
clear.Tip=清空(Ctrl+Z)
clear.zhTip=清空
clear.enTip=Empty
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.zhText=退出
close.enText=Quit
close.Tip=退出(Alt+F4)
close.zhTip=退出
close.enTip=Quit
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

opstmp.Type=TMenuItem
opstmp.Text=修改信息
opstmp.zhText=修改信息
opstmp.enText=Operation Template
opstmp.Tip=修改信息
opstmp.zhTip=修改信息
opstmp.enTip=Operation Template
opstmp.Action=onChange
opstmp.pic=new.gif

export.Type=TMenuItem
export.Text=汇出
export.Tip=汇出
export.M=E
export.key=F4
export.Action=onExport
export.pic=exportexcel.gif


print.Type=TMenuItem
print.Text=住院证打印
print.Tip=住院证打印
print.M=
print.key=
print.Action=onPrint
print.pic=print.gif