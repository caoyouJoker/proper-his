 #
  # Title: 临床路径准进准出
  #
  # Description: 临床路径准进准出
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author luhai 2010.05.09
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;delete;|;query;|;clear;|;add;|;change;|;timeInterval;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;delete;|;query;|;clear;|;add;|;change;|;timeInterval;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

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

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

add.Type=TMenuItem
add.Text=新增
add.Tip=新增(Ctrl+N)
add.M=A
add.key=Ctrl+N
add.Action=onAdd
add.pic=039.gif

change.Type=TMenuItem
change.Text=变更临床路径
change.Tip=变更临床路径
change.M=A
change.key=
change.Action=onChange
change.pic=026.gif

//timeInterval.Type=TMenuItem
//timeInterval.Text=实际时程设定
//timeInterval.Tip=实际时程设定
//timeInterval.M=A
//timeInterval.key=
//timeInterval.Action=durationConfig
//timeInterval.pic=spreadout.gif

timeInterval.Type=TMenuItem
timeInterval.Text=路径展开
timeInterval.Tip=路径展开
timeInterval.M=A
timeInterval.key=
timeInterval.Action=durationConfig
timeInterval.pic=spreadout.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClosePanel
close.pic=close.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif