#
  # Title: 批次程序
  #
  # Description:批次程序
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author zhangy
  # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;delete;|;start;|;stop;|;server;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;delete;|;start;|;stop;|;server;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除(Delete)
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

start.Type=TMenuItem
start.Text=启动
start.Tip=启动
start.M=S
start.Action=onStart
start.pic=openbill.gif

stop.Type=TMenuItem
stop.Text=停止
stop.Tip=停止
stop.M=S
stop.Action=onStop
stop.pic=closebill.gif

server.Type=TMenuItem
server.Text=参数设定
server.Tip=参数设定
server.M=S
server.Action=onServer
server.pic=export.gif

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

