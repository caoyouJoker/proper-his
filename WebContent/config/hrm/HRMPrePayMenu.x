 #
  # Title: 健检预交金
  #
  # Description:健检预交金
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;delete;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;delete;|;clear;|;close

save.Type=TMenuItem
save.Text=交费
save.Tip=交费
save.M=S
save.key=Ctrl+S
save.Action=onCharge
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=退费
delete.Tip=退费
delete.M=N
delete.key=Delete
delete.Action=onRefund
delete.pic=delete.gif


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
