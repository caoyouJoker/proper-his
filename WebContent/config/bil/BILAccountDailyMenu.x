<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;query;;clear;print;|;detailprint;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;query;Refresh;clear;print;|;detailprint;|;close

save.Type=TMenuItem
save.Text=日结
save.Tip=日结
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

print.Type=TMenuItem
print.Text=补印
print.Tip=补印
print.M=P
print.key=
print.Action=onPrint
print.pic=print.gif

detailprint.Type=TMenuItem
detailprint.Text=医保明细打印
detailprint.Tip=医保明细打印
detailprint.M=F
detailprint.key=Ctrl+P
detailprint.Action=onDetailPrint
detailprint.pic=Print.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=关闭
close.Tip=关闭
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

