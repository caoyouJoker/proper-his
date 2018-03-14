 #
  # Title: 构建查询SQL
  #
  # Description:构建查询SQL
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 2.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;delete;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=set;delete;clear;close


delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除
delete.M=Q
delete.key=Ctrl+X
delete.Action=onDelete
delete.pic=delete.gif

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=R
clear.key=F5
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
