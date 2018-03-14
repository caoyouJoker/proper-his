#
  # Title: 科室管理
  #
  # Description:三级科室管理
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author fudw
  # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;new;|;query;|;clear;|;delete;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;new;|;query;|;clear;|;delete;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onUpdate
save.pic=save.gif

new.Type=TMenuItem
new.Text=新增
new.Tip=新增
new.M=N
new.Action=onNew
new.pic=new.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除
delete.M=N
delete.key=Delete
delete.Action=TABLE|removeRow
delete.pic=delete.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif