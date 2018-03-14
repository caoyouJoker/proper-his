 #
  # Title: 输血反应查询
  #
  # Description:输血反应查询
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2009-05-05
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

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

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


