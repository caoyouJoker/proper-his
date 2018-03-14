<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;apply;|;print;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;apply;|;print;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=P
print.key=Ctrl+P
print.Action=onPrintXDDialog
print.pic=print.gif

apply.Type=TMenuItem
apply.Text=检验结果汇入
apply.Tip=检验结果汇入
apply.M=M
apply.key=Ctrl+M
apply.Action=onMedApply
apply.pic=031.gif


close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif