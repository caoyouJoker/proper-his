#
# Title: 普通药下架menu
#
# Description:普通药下架menu
#
# Copyright: BlueCore (c) 2012
#
# @author wangzl
# @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;query;clear;again;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;Refresh;clear;query;again;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除
delete.M=S
delete.key=Ctrl+D
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

again.Type=TMenuItem
again.Text=换周转箱
again.Tip=换周转箱
again.M=C
again.Action=onAgain
again.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif