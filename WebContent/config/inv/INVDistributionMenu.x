#############################################
# <p>Title:物资管理Menu </p>
#
# <p>Description:物资管理Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author zhangh 2013.05.3
# @version 1.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;printUp;|;printDown;|;excelUp;|;excelDown;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;print;|;excel;|;clear;|;close

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

printUp.Type=TMenuItem
printUp.Text=打印上表
printUp.Tip=打印上表
printUp.M=P
printUp.key=Ctrl+P
printUp.Action=onPrintUp
printUp.pic=print.gif

printDown.Type=TMenuItem
printDown.Text=打印下表
printDown.Tip=打印下表
printDown.M=P
printDown.key=Ctrl+P
printDown.Action=onPrintDown
printDown.pic=print-1.gif

excelUp.Type=TMenuItem
excelUp.Text=导上表Excel
excelUp.Tip=导上表Excel
excelUp.M=E
excelUp.key=Ctrl+E
excelUp.Action=onExeclUp
excelUp.pic=exportexcel.gif

excelDown.Type=TMenuItem
excelDown.Text=导下表Excel
excelDown.Tip=导下表Excel
excelDown.M=E
excelDown.key=Ctrl+E
excelDown.Action=onExeclDown
excelDown.pic=export.gif