 #
  # Title: 查询库存结余Menu
  #
  # Description:查询库存结余Menu
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author shendr 2014-05-16
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;export;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;export;|;clear;|;close

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

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

export.Type=TMenuItem
export.Text=导出EXCEL
export.Tip=导出EXCEL
execrpt.M=E
export.Action=onExport
export.pic=045.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
