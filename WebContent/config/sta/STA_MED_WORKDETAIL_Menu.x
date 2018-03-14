 #
  # Title: 外转院所
  #
  # Description: 外转院所
  #
  # Copyright: bluecore (c) 2012
  #
  # @author shibl
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;Excel;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;Excel;|;clear;|;close



Excel.Type=TMenuItem
Excel.Text=导出数据
Excel.Tip=导出数据(Ctrl+D)
Excel.M=D
Excel.key=Ctrl+D
Excel.Action=onExport
Excel.pic=045.gif


query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

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