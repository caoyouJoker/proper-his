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
UI.button=download1;|;download2;|;download3;|;download4;|;export;|;print;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;clear;|;card;|;export;|;close

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
download1.Text=拒付下载
download1.Tip=拒付下载
download1.M=E
download1.Action=onDownload|1
download1.pic=030.gif

download2.Type=TMenuItem
download2.Text=缓支下载
download2.Tip=缓支下载
download2.M=E
download2.Action=onDownload|2
download2.pic=025.gif

download3.Type=TMenuItem
download3.Text=缓支给付下载
download3.Tip=缓支给付下载
download3.M=E
download3.Action=onDownload|3
download3.pic=018.gif

download4.Type=TMenuItem
download4.Text=拒付明细下载
download4.Tip=拒付明细下载
download4.M=E
download4.Action=onDownload|4
download4.pic=detail.gif