 #
  # Title: 肠内营养营养粉使用情况查询报表
  #
  # Description:
  #
  # Copyright: JavaHis (c) 2015
  #
  # @author wangb 2015.06.25
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;export;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;clear;|;close

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

export.Type=TMenuItem
export.Text=导出Excel
export.Tip=导出(Ctrl+O)
export.M=O
export.key=Ctrl+O
export.Action=onExport
export.pic=exportexcel.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
