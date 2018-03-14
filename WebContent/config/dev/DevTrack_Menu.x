 #
 # Title: 设备追踪
 #
 # Description:设备追踪
 #
 # Copyright: JavaHis (c) 2012
 #
 # @author yuhaibao
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=quary;clear|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=quary;clear|;close

quary.Type=TMenuItem
quary.Text=查询
quary.Tip=查询
quary.M=Q
quary.key=Ctrl+F
quary.Action=onQuery
quary.pic=query.gif

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