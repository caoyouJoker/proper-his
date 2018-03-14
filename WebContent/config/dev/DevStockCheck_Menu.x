<Type=TMenuBar>
UI.Item=File
UI.button=save;|;query;|;check;|;export;|;print;|;clear;|;close

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;query;|;check;|;export;|;print;|;clear;|;close

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
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif


export.Type=TMenuItem
export.Text=导出
export.Tip=导出
export.M=E
export.key=F4
export.Action=onExport
export.pic=export.gif


check.Type=TMenuItem
check.Text=盘点
check.Tip=盘点
check.M=P
check.key=F9
check.Action=onCheck
check.pic=026.gif



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
print.key=Alt+F8
print.Action=onPrint
print.pic=print.gif