 #
  # Title: 医疗卡密码修改操作
  #
  # Description: 医疗卡密码修改操作
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2011.09.28
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;read;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;read;|;clear;|;close


save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

read.Type=TMenuItem
read.Text=读卡
read.Tip=读卡(Ctrl+R)
read.M=R
read.key=Ctrl+R
read.Action=onReadEKT
read.pic=042.gif

EKTprint.Type=TMenuItem
EKTprint.Text=补印
EKTprint.Tip=补印
EKTprint.M=P
EKTprint.key=Ctrl+P
EKTprint.Action=onPrint
EKTprint.pic=print_red.gif

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
