 #
  # Title: 包接收确认
  # Description:包接收确认
  # Copyright: JavaHis (c) 2008
  # @author yufh
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;cancel;|;query;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;cancel;|;query;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onUpdate
save.pic=save.gif

cancel.Type=TMenuItem
cancel.Text=取消接收
cancel.Tip=取消接收
cancel.M=N
cancel.key=Ctrl+N
cancel.Action=onCancel
cancel.pic=Undo.gif

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

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif