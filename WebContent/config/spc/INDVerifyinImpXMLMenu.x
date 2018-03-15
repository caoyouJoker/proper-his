 #
  # Title: 验收入库-引入出货单
  #
  # Description:验收入库-引入出货单
  #
  # Copyright: ProperSoft (c) 2012
  #
  # @author zhangy 2009-05-06
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;return;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=return;|;close



close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

return.Type=TMenuItem
return.Text=传回
return.Tip=传回
return.M=R
return.Action=onReturn
return.pic=054.gif

