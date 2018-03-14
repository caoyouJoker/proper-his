<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;export;|;clear;|;print;|;printAll;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu   
File.Text=文件
File.M=F
File.Item=query;|;export;|;clear;|;print;|;printAll;|;close


query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif


Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif


close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

print.Type=TMenuItem
print.Text=打印出库单
print.Tip=打印出库单
print.M=P
print.Action=onPrint
print.pic=print.gif

printAll.Type=TMenuItem
printAll.Text=打印血库历史
printAll.Tip=打印血库历史
printAll.M=A
printAll.Action=onPrintAll
printAll.pic=print.gif


export.Type=TMenuItem
export.Text=汇出
export.Tip=汇出
export.M=E
export.key=F4
export.Action=onExport
export.pic=exportexcel.gif