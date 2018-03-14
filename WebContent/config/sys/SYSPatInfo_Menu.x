<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;new;delete;query;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.enText=Window
Window.enTip=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.enText=File
File.enTip=File
File.M=F
File.Item=save;new;delete;Refresh;query;|;clear;|;regist;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.enText=save
save.enTip=save
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

new.Type=TMenuItem
new.Text=新增
new.Tip=新增
new.enText=Add
new.enTip=Add
new.M=S
new.key=Ctrl+S
new.Action=onNew
new.pic=039.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除
delete.enText=delete
delete.enTip=delete
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif


query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.enTip=query
query.enText=query
query.M=Q
query.key=Ctrl+F
query.Action=onQueryInformation
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.enText=Refresh
Refresh.enTip=Refresh
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif



clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.enTip=Empty
clear.enText=Empty
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

regist.Type=TMenuItem
regist.Text=注册照相组件
regist.Tip=注册照相组件
regist.M=R
regist.key=
regist.Action=onRegist
regist.pic=007.gif


close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.enText=Log out
close.enTip=Log out
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

