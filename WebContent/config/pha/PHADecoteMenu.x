<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;cancel;|;printlist;|;printpaster;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;query;|;cancel;|;printlist;|;printpaster;|;clear;|;close

save.Type=TMenuItem
save.Text=确认
save.Tip=确认
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

cancel.Type=TMenuItem
cancel.Text=取消
cancel.Tip=取消
cancel.M=Q
cancel.key=
cancel.Action=onCancel
cancel.pic=cancle.gif

printlist.Type=TMenuItem
printlist.Text=打印清单
printlist.Tip=打印清单
printlist.M=P
printlist.Action=onPrintList
printlist.pic=print.gif

printpaster.Type=TMenuItem
printpaster.Text=打印贴纸
printpaster.Tip=打印贴纸
printpaster.M=P
printpaster.Action=onPrintPaster
printpaster.pic=pha_print.gif

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

