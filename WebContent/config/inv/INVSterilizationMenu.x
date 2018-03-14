 #
  # Title: 灭菌
  #
  # Description:灭菌
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author fudw 2009-05-15
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;cancel;|;query;|;print;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;cancel;|;query;|;print;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onUpdate
save.pic=save.gif

cancel.Type=TMenuItem
cancel.Text=撤销
cancel.Tip=撤销
cancel.M=N
cancel.key=Ctrl+D
cancel.Action=onCancel
cancel.pic=Undo.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=barcode.gif


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