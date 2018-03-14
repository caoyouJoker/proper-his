 #
  # Title: 医疗卡交易记录
  #
  # Description: 医疗卡交易记录
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2010.09.16
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=download1;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=download1;|;clear;|;close

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

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=P
print.Action=onPrint
print.pic=print.gif

export.Type=TMenuItem
export.Text=汇出
export.Tip=汇出
execrpt.M=E
export.Action=onExport
export.pic=export.gif

download1.Type=TMenuItem
download1.Text=下载
download1.Tip=下载
download1.M=E
download1.Action=onDownload
download1.pic=030.gif
