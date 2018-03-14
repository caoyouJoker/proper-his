 #
  # Title: 天津市基本医疗保险考核预留金支付表
  #
  # Description:天津市基本医疗保险考核预留金支付表
  #
  # Copyright: JavaHis (c) 2009
<Type=TMenuBar>
UI.Item=File;Window
UI.button=download;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=download;|;clear;|;close

download.Type=TMenuItem
download.Text=下载
download.Tip=下载
download.M=E
download.Action=onDownload
download.pic=030.gif


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
