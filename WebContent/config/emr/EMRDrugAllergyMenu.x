<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;return;|;delete;|;query;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;delete;|;query;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=Q
save.key=Ctrl+F
save.Action=onSave
save.pic=save.gif

return.Type=TMenuItem
return.Text=传回
return.Tip=传回
return.M=Q
return.key=Ctrl+F
return.Action=onReturnAllergy
return.pic=054.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除
delete.M=Q
delete.key=Ctrl+F
delete.Action=onDelete
delete.pic=delete.gif


query.Type=TMenuItem
query.Text=查询
query.Tip=查询
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

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

