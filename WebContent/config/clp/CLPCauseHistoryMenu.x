 #
  # Title: 临床路径不进入原因
  #
  # Description: 临床路径不进入原因
  #
  # Copyright: bluecore (c) 2015
  #
  # @author pangb 20150810
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;execl;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;execl;|;clear;|;close

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

execl.Type=TMenuItem
execl.Text=导出Excel
execl.Tip=导出Excel
execl.M=
execl.key=
execl.Action=onExport
execl.pic=export.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

preview.Type=TMenuItem
preview.Text=临床路径模板
preview.Tip=临床路径模板(Ctrl+P)
preview.M=P
preview.key=Ctrl+P
preview.Action=onPreview
preview.pic=025.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif