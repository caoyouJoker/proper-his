#############################################
# <p>Title:住院护士站医技进度查询界面 </p>
#
# <p>Description:住院护士站医技进度查询界面 </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2010.11.11
# @version 4.0
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

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=X
clear.key=
clear.Action=onClear
clear.pic=clear.gif

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

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

excel.Type=TMenuItem
excel.Text=汇出Excel
excel.Tip=汇出Ctrl+E)
excel.M=E
excel.key=Ctrl+E
excel.Action=onExecl
excel.pic=exportexcel.gif