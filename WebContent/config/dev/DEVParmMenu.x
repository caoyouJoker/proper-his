 #
 # Title: 设备参数档
 #
 # Description:6.1.1.	设备参数档
 #
 # Copyright: JavaHis (c) 2013
 #
 # @author fux
 # @version 1.0 
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh 
 
File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif


close.Type=TMenuItem
close.Text=退出
close.Tip=退出
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