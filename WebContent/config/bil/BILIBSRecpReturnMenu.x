<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;save;|;print;|;losePrint;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;clear;save;print;|;Refresh;|;losePrint;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=S
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

save.Type=TMenuItem
save.Text=收据退费
save.Tip=收据退费
save.M=P
save.Action=onSave
save.pic=030.gif

print.Type=TMenuItem
print.Text=补印
print.Tip=补印
print.M=B
print.Action=onPrint
print.pic=print.gif

losePrint.Type=TMenuItem
losePrint.Text=票据遗失补印
losePrint.Tip=票据遗失补印
losePrint.M=
losePrint.Action=onLosePrint
losePrint.pic=print_red.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

