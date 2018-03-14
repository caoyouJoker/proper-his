 #
 # Title: 设备类别参数设定设定
 #
 # Description:设备类别参数设定设定
 #
 # Copyright: JavaHis (c) 2008
 #
 # @author sundx
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=quary;clear;generateReceipt;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=quary;clear;generateReceipt;|;close



quary.Type=TMenuItem
quary.Text=查询
quary.Tip=查询
quary.M=Q
quary.key=Ctrl+F
quary.Action=onQuery
quary.pic=query.gif


clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

generateReceipt.Type=TMenuItem
generateReceipt.Text=生成订购单
generateReceipt.Tip=生成订购单
generateReceipt.M=R
generateReceipt.key=Ctrl+R
generateReceipt.Action=onGenerateReceipt
generateReceipt.pic=037.gif

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