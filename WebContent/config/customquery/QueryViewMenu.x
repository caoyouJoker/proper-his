 #
  # Title: 自定义查询
  #
  # Description:自定义查询
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=Insert;|;Delete;|;Clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=Insert;Delete;Clear;close

set.Type=TMenuItem
set.Text=新增查询
set.Tip=新增查询
set.M=S
set.key=F2
set.Action=onInsert
set.pic=new.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除
delete.M=Q
delete.key=Ctrl+X
delete.Action=onDelete
delete.pic=delete.gif



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
