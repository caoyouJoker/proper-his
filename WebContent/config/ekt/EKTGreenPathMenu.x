# 
#  Title:绿色通道Menu
# 
#  Description:绿色通道Menu
# 
#  Copyright: Copyright (c) Javahis 2009
# 
#  author pangben 20111009
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;delete;|;query;|;ektCard;|;onReg;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;delete;|;query;|;ektCard;|;onReg;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

onReg.Type=TMenuItem
save.Text=预约挂号
onReg.Tip=预约挂号
onReg.M=R
onReg.key=Ctrl+R
onReg.Action=onSaveReg
onReg.pic=018.gif



delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除(Delete)
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
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

new.Type=TMenuItem
new.Text=新增
new.Tip=新增(Ctrl+N)
new.M=N
new.key=Ctrl+N
new.Action=onNew
new.pic=new.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
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

ektCard.Type=TMenuItem
ektCard.Text=读医疗卡
ektCard.Tip=读医疗卡
ektCard.M=
ektCard.Action=onReadEktCard
ektCard.pic=042.gif