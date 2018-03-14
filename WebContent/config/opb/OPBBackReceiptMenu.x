<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;fill;|;print;|;onLostPrint;|;save;|;close;|;

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=fill;|;query;|;print;|;onLostPrint;|;save;|;close

save.Type=TMenuItem
save.Text=退费
save.Tip=退费
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=Undo.gif

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

fill.Type=TMenuItem
fill.Text=费用清单
fill.Tip=费用清单
fill.M=F
fill.key=Ctrl+H
fill.Action=onFill
fill.pic=detail.gif

print.Type=TMenuItem
print.Text=补打
print.Tip=票据补打
print.M=F
print.key=Ctrl+P
print.Action=onPrint
print.pic=print_red.gif

onLostPrint.Type=TMenuItem
onLostPrint.Text=遗失补印
onLostPrint.Tip=遗失补印
print.M=F
onLostPrint.key=
onLostPrint.Action=onLostPrint
onLostPrint.pic=print_red.gif
