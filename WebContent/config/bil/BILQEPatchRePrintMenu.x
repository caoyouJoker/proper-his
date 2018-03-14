<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;ektCard;|;print;|;tablePrint;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;ektCard;|;print;|;tablePrint;|;clear;|;close

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

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=F
print.key=Ctrl+P
print.Action=onPrint
print.pic=Print.gif

ektCard.Type=TMenuItem
ektCard.Text=读医疗卡
ektCard.Tip=读医疗卡
ektCard.M=
ektCard.key=F6
ektCard.Action=onEKT
ektCard.pic=042.gif

tablePrint.Type=TMenuItem
tablePrint.Text=表格数据打印
tablePrint.Tip=表格数据打印
tablePrint.M=F
tablePrint.key=Ctrl+P
tablePrint.Action=onTablePrint
tablePrint.pic=Print.gif

