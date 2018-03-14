 #
  # Title: 申请单号查询
  #
  # Description:申请单号查询
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2009-05-05
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=new;|;return;|;close

Window.Type=TMenu
Window.Text=窗口
Window.zhText=窗口
Window.enText=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.zhText=文件
File.enText=File
File.M=F
File.Item=new;|;return;|;close

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.zhText=刷新
Refresh.enText=Refresh
Refresh.Tip=刷新
Refresh.zhTip=刷新
Refresh.enTip=Refresh
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

close.Type=TMenuItem
close.Text=退出
close.zhText=退出
close.enText=Quit
close.Tip=退出(Alt+F4)
close.zhTip=退出(Alt+F4)
close.enTip=Quit(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

return.Type=TMenuItem
return.Text=传回
return.zhText=传回
return.enText=Retrieve
return.Tip=传回
return.zhTip=传回
return.enTip=Retrieve
return.M=R
return.Action=onReturn
return.pic=054.gif

new.Type=TMenuItem
new.Text=新建
new.zhText=新建
new.enText=New
new.Tip=新建(Ctrl+N)
new.zhTip=新建(Ctrl+N)
new.enTip=New(Ctrl+N)
new.M=N
new.key=Ctrl+N
new.Action=onNew
new.pic=new.gif



