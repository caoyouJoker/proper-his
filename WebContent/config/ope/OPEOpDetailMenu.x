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
UI.button=save;|;opstmp;|;clear;|;close

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
File.Item=save;|;opstmp;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.zhText=保存
save.enText=Save
save.Tip=保存(Ctrl+S)
save.zhTip=保存(Ctrl+S)
save.enTip=Save(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

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
opstmp.Text=手术模板
opstmp.zhText=手术模板
opstmp.enText=Operation Template
opstmp.Tip=手术模板
opstmp.zhTip=手术模板
opstmp.enTip=Operation Template
opstmp.Action=onOpstmp
opstmp.pic=new.gif

emr.Type=TMenuItem
emr.Text=结构化手术记录
emr.zhText=结构化手术记录
emr.enText=Operation Rec
emr.Tip=结构化手术记录
emr.zhTip=结构化手术记录
emr.enTip=Operation Rec
emr.Action=onEmr
emr.pic=emr-2.gif

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