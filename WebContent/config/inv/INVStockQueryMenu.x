#############################################
# <p>Title:物资智能柜查询</p>
#
# <p>Description:物资智能柜查询</p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: ProperSoft</p>
#
# @author huangtt 2013.07.13
# @version 1.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;print;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;print;|;clear;|;close

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=X
clear.key=
clear.Action=onClear
clear.pic=clear.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=
print.key=
print.Action=onPrint
print.pic=print.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif



close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

