<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;clear;detial;print;excel;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=Refresh;query;clear;detial;print;excel;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=P
print.Action=onPrint
print.pic=print.gif

excel.Type=TMenuItem
excel.Text=导出EXCEL
excel.Tip=导出EXCEL
excel.M=E
excel.Action=onExcel
excel.pic=exportexcel.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

detial.Type=TMenuItem
detial.Text=明细
detial.Tip=明细
detial.M=D
detial.key=Ctrl+D
detial.Action=onDetail
detial.pic=detail.gif
