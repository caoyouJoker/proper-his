 #
  # Title: 肠内营养定制历史数据菜单
  #
  # Description:
  #
  # Copyright: JavaHis (c) 2015
  #
  # @author wangb 2015.4.20
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=return;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=return;|;close

return.Type=TMenuItem
return.Text=传回
return.Tip=传回
return.M=R
return.Action=onReturn
return.pic=054.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
