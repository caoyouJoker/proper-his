 #
  # Title: 批次解锁档
  #
  # Description:批次解锁
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2009.04.20
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=unlock;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=unlock;|;close

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

unlock.Type=TMenuItem
unlock.Text=解锁
unlock.Tip=解锁
unlock.M=X
unlock.Action=onUnLock
unlock.pic=unlock.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
