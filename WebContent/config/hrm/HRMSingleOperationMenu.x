<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;delRow;|;operation;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;delRow;|;operation;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delRow.Type=TMenuItem
delRow.Text=删除医嘱
delRow.Tip=删除医嘱
delRow.M=D
delRow.Action=onDelRow
delRow.pic=delete.gif

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

operation.Type=TMenuItem
operation.Text=手术室计费
operation.Tip=手术室计费
operation.M=P
operation.Action=onOperation
operation.pic=operation.gif
