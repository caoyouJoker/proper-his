 #
  # Title: 医保资料查询
  #
  # Description: 医保资料查询
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author lim 2011.09.28
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;EKTprint1;|;EKTprint2;|;INSprintNoLocal;|;export;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;EKTprint1;|;EKTprint2;|;INSprintNoLocal;|;export;|;clear;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.Action=onQuery
query.pic=query.gif

EKTprint1.Type=TMenuItem
EKTprint1.Text=资格确认书打印
EKTprint1.Tip=资格确认书打印
EKTprint1.M=P
EKTprint1.key=Ctrl+P
EKTprint1.Action=onQualificationPrint
EKTprint1.pic=print.gif

EKTprint2.Type=TMenuItem
EKTprint2.Text=二号表打印
EKTprint2.Tip=二号表打印
EKTprint2.M=P
EKTprint2.key=Ctrl+P
EKTprint2.Action=onPrint2
EKTprint2.pic=print.gif


INSprintNoLocal.Type=TMenuItem
INSprintNoLocal.Text=异地结算单
INSprintNoLocal.Tip=异地结算单
INSprintNoLocal.M=P
INSprintNoLocal.key=Ctrl+P
INSprintNoLocal.Action=onPrintNoLocal
INSprintNoLocal.pic=print.gif

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
