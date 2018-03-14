#############################################
# <p>Title:病案编目Menu </p>
#
# <p>Description:病案编目Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2009.05.11
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;lending;|;dbfConvert;|;print;excel;|;clear;|;mrshow;|;searchshow;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;lending;|;dbfConvert;|;print;excel;|;clear;|;mrshow;|;searchshow;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除(Delete)
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif


query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

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

mrshow.Type=TMenuItem
mrshow.Text=病历浏览
mrshow.Tip=病历浏览(Ctrl+W)
mrshow.M=W
mrshow.key=Ctrl+W
mrshow.Action=onShow
mrshow.pic=012.gif

searchshow.Type=TMenuItem
searchshow.Text=全文检索
searchshow.Tip=全文检索(Ctrl+W)
searchshow.M=H
searchshow.key=Ctrl+H
searchshow.Action=onAllSearch
searchshow.pic=006.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

lending.Type=TMenuItem
lending.Text=借阅病历
lending.Tip=借阅病历(Alt+L)
lending.M=L
lending.key=Alt+L
lending.Action=onLending
lending.pic=emr.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印(Ctrl+P)
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

dbfConvert.Type=TMenuItem
dbfConvert.Text=DBF转档
dbfConvert.Tip=DBF转档
dbfConvert.M=P
dbfConvert.Action=saveDBF
dbfConvert.pic=046.gif

excel.Type=TMenuItem
excel.Text=汇出Excel
excel.Tip=汇出Ctrl+E)
excel.M=E
excel.key=Ctrl+E
excel.Action=onExecl
excel.pic=exportexcel.gif

