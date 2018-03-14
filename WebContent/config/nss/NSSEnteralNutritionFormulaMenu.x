 #
  # Title: 营养膳食配餐菜单
  #
  # Description:
  #
  # Copyright: JavaHis (c) 2014
  #
  # @author wangb 2014.12.17
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=close

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
