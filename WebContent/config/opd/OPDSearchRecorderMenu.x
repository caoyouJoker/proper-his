#
# TBuilder Config File 
#
# Title:修改药品或者检验检查的执行状态
#
# Company:JavaHis
#
# Author:yanj 2013.04.07
#
# version 1.0
#

<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;read;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;query;|;read;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQurey
query.pic=query.gif

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


read.Type=TMenuItem
read.Text=读卡
read.Tip=读卡(Ctrl+R)
read.M=
read.key=Ctrl+R
read.Action=onReadEKT
read.pic=042.gif

