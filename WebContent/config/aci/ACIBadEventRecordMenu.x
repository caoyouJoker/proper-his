<Type=TMenuBar>
UI.Item=File;Window
UI.button=insert;update;|;examine;unexamine;|;rate;|;new;|;delete;|;query;|;sms;|;export;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=insert;update;|;examine;unexamine;|;rate;|;new;|;delete;|;query;|;sms;|;export;|;clear;|;close

insert.Type=TMenuItem
insert.Text=保存
insert.Tip=保存
insert.M=S
insert.key=Ctrl+S
insert.Action=onSave
insert.pic=save.gif

update.Type=TMenuItem
update.Text=更新
update.Tip=更新
update.M=S
update.key=Ctrl+S
update.Action=onUpdate
update.pic=Redo.gif

examine.Type=TMenuItem
examine.Text=审核
examine.Tip=审核
examine.M=U
examine.key=Ctrl+U
examine.Action=onExamine
examine.pic=execute.gif

unexamine.Type=TMenuItem
unexamine.Text=取消审核
unexamine.Tip=取消审核
unexamine.M=U
unexamine.key=Ctrl+U
unexamine.Action=onUnExamine
unexamine.pic=004.gif

rate.Type=TMenuItem
rate.Text=评级
rate.Tip=评级
rate.M=R
rate.key=Ctrl+R
rate.Action=onRate
rate.pic=046.gif

new.Type=TMenuItem
new.Text=新增
new.Tip=新增
new.M=N
new.key=Ctrl+N
new.Action=onNew
new.pic=039.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除
delete.M=D
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

sms.Type=TMenuItem
sms.Text=发短信
sms.Tip=发短信
sms.M=M
sms.key=Ctrl+M
sms.Action=onSendSMS
sms.pic=014.gif

export.Type=TMenuItem
export.Text=导出
export.Tip=导出
export.M=E
export.key=Ctrl+E
export.Action=onExport
export.pic=export.gif

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
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif