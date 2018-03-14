 #
  # Title: 物质参数档
  #
  # Description:物质参数档
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author wangzl 2013.01.05
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;unlock;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;unlock;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

unlock.Type=TMenuItem
unlock.Text=批次解锁
unlock.Tip=批次解锁
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
