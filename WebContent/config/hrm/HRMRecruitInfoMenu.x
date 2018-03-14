#
# TBuilder Config File 
#
# Title:受试者招募表菜单栏
#
# Company:JavaHis
#
# Author:wangb 2016.07.04
#
# version 1.0
#

<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;return;clear;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;return;clear;close

return.Type=TMenuItem
return.Text=传回
return.Tip=传回(Ctrl+R)
return.M=
return.key=Ctrl+R
return.Action=onReturn
return.pic=054.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

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
