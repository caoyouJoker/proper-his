 #
  # Title: 供应厂商
  #
  # Description: 供应厂商
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author wukai 2017.02.14
 # @version 1.0

<Type=TMenuBar>
UI.Item=File
UI.button=refresh;|;close

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=refresh;close

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

refresh.Type=TMenuItem
refresh.Text=刷新
refresh.Tip=刷新
refresh.M=R
refresh.key=F5
refresh.Action=onReresh
refresh.pic=Refresh.gif

