 #
  # Title: 医疗卡交易记录操作
  #
  # Description: 医疗卡交易记录操作
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangp 2011.12.20
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=EKTprint;|;query;|;clear;|;export;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=EKTprint;|;close

EKTprint.Type=TMenuItem
EKTprint.Text=打印
EKTprint.Tip=打印(Ctrl+P)
EKTprint.M=P
EKTprint.key=Ctrl+P
EKTprint.Action=onPrint
EKTprint.pic=print.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Y
query.key=
query.Action=onQuery
query.pic=query.gif

export.Type=TMenuItem
export.Text=汇出
export.Tip=汇出
execrpt.M=E
export.Action=onExport
export.pic=export.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif