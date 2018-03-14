#
  # Title: 门诊收费
  #
  # Description:门诊收费和补充计价
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author fudw
  # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;fill;|;delete;|;record;|;clear;|;close;|;

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;fill;|;Refresh;|;query;|;delete;|;record;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

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
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

fill.Type=TMenuItem
fill.Text=补打
fill.Tip=补打
fill.M=F
fill.key=Ctrl+H
fill.Action=onFill
fill.pic=Print.gif

record.Type=TMenuItem
record.Text=就诊号
record.Tip=就诊号
record.M=R
record.key=F5
record.Action=onRecord
record.pic=detail.gif