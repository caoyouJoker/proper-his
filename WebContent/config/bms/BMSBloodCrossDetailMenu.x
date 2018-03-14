 #
  # Title: 
  #
  # Description: 
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2009.09.22
 # @version 1.0
<Type=TMenuBar>  
UI.Item=File;Window
UI.button=clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=clear;|;close

clear.Type=TMenuItem
clear.Text=查询
clear.Tip=查询(Ctrl+F)
clear.M=Q
clear.key=Ctrl+F
clear.Action=onApplyNoAction
clear.pic=clear.gif

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