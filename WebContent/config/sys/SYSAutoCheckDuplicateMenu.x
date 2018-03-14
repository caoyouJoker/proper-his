 #
  # Title: 张建国单档开发练习
  #
  # Description: 张建国单档开发练习
  #
  # Copyright: JavaHis (c) 2011
  #
  # @author Zhangjg 2011.04.11
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;handle;undo;query;picture;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;handle;undo;query;picture;clear;|;close

save.Type=TMenuItem
save.Text=合并
save.Tip=合并
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

picture.Type=TMenuItem
picture.Text=二代身份证
picture.Tip=二代身份证
picture.M=
picture.key=
picture.Action=onIdCardNo
picture.pic=idcard.gif

handle.Type=TMenuItem
handle.Text=处理
handle.Tip=处理
handle.M=H
handle.key=Ctrl+H
handle.Action=onHandle
handle.pic=execute.gif

undo.Type=TMenuItem
undo.Text=撤销
undo.Tip=撤销
undo.M=U
undo.key=Ctrl+U
undo.Action=onUndo
undo.pic=Undo.gif

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

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif