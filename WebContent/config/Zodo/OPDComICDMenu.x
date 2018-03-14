<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;delete;|;close

Window.Type=TMenu
Window.Text=窗口
Window.zhText=窗口
Window.enText=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.zhText=文件
File.enText=File
File.M=F
File.Item=save;delete;Refresh;|;close

save.Type=TMenuItem
save.Text=保存
save.zhText=保存
save.enText=Save
save.Tip=保存
save.zhTip=保存
save.enTip=Save
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif


delete.Type=TMenuItem
delete.Text=删除
delete.zhText=删除
delete.enText=Delete
delete.Tip=删除
delete.zhTip=删除
delete.enTip=Delete
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.zhText=刷新
Refresh.enText=Refresh
Refresh.Tip=刷新
Refresh.zhTip=刷新
Refresh.enTip=Refresh
Refresh.M=R
Refresh.key=F5
Refresh.Action=onClear
Refresh.pic=Refresh.gif

close.Type=TMenuItem
close.Text=退出
close.zhText=退出
close.enText=Quit
close.Tip=退出
close.zhTip=退出
close.enTip=Quit
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif