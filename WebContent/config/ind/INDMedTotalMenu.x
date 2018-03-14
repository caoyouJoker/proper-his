 #
  # Title: 住院药瞩总数统计报表
  #
  # Description: 住院药瞩总数统计报表
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author lij 2017.05.18
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
File.Item=query;|;clear;|;export;|;close

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

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

export.Type=TMenuItem
export.Text=导出
export.Tip=导出
export.M=E
export.key=Ctrl+E
export.Action=onExport
export.pic=export.gif

//print.Type=TMenuItem
//print.Text=打印
//print.Tip=打印
//print.M=P
//print.key=Ctrl+P
//print.Action=onPrint
//print.pic=print.gif