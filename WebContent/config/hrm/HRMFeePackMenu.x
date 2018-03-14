 #
  # Title:健检套餐设定
  #
  # Description:健检套餐设定
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;delete;new;|;newcopy;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;delete;new;|;newcopy;|;clear;|;close

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
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif


new.Type=TMenuItem
new.Text=新增
new.Tip=新增
new.M=Q
new.key=Ctrl+F
new.Action=onNew
new.pic=new.gif


newcopy.Type=TMenuItem
newcopy.Text=套餐复制
newcopy.Tip=套餐复制
newcopy.M=Q
newcopy.key=Ctrl+P
newcopy.Action=onNewCopy
newcopy.pic=detail-1.gif

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
