 #
  # Title: 住院医保综合对账查询
  #
  # Description:住院医保综合对账查询
  #
  # Copyright: JavaHis (c) 2009
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
query.Tip=查询
query.M=Q
query.Action=onQuery
query.pic=query.gif


clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

export.Type=TMenuItem
export.Text=汇出
export.Tip=汇出
execrpt.M=E
export.Action=onExport
export.pic=export.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
