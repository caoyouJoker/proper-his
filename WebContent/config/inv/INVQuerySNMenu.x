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
UI.button=query;|;excel;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;excel;|;clear;|;close

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

print.Type=TMenuItem
print.Text=打印
print.Tip=打印(Ctrl+P)
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif   

excel.Type=TMenuItem
excel.Text=导Excel
excel.Tip=导Excel(Ctrl+E)
excel.M=E
excel.key=Ctrl+E
excel.Action=onExcel
excel.pic=exportexcel.gif