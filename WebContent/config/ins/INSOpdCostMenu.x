 #
  # Title: 门诊医疗费项目明细单
  #
  # Description: 门诊医疗费项目明细单
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author lim 2011.09.28
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;EKTprint;|;OPDprint;|;INPprint;|;TPMprint;|;APDDown;|;APDexport;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;clear;|;EKTprint;|;OPDprint;|;INPprint;|;TPMprint;|;APDDown;|;APDexport;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.Action=onQuery
query.pic=query.gif

EKTprint.Type=TMenuItem
EKTprint.Text=打印
EKTprint.Tip=打印
EKTprint.M=P
EKTprint.key=Ctrl+P
EKTprint.Action=onPrint
EKTprint.pic=print.gif

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

OPDprint.Type=TMenuItem
OPDprint.Text=门诊运行
OPDprint.Tip=门诊运行
OPDprint.M=
OPDprint.key=
OPDprint.Action=print_O
OPDprint.pic=print.gif

INPprint.Type=TMenuItem
INPprint.Text=住院运行
INPprint.Tip=住院运行
INPprint.M=
INPprint.key=
INPprint.Action=print_P
INPprint.pic=print.gif

TPMprint.Type=TMenuItem
TPMprint.Text=拒付汇总
TPMprint.Tip=拒付汇总
TPMprint.M=
TPMprint.key=
TPMprint.Action=print_Q
TPMprint.pic=print.gif

APDDown.Type=TMenuItem
APDDown.Text=调整支付明细下载
APDDown.Tip=调整支付明细下载
APDDown.M=
APDDown.key=
APDDown.Action=APDDown
APDDown.pic=print.gif

APDexport.Type=TMenuItem
APDexport.Text=调整支付明细导出
APDexport.Tip=调整支付明细导出
APDexport.M=
APDexport.key=
APDexport.Action=APDexport
APDexport.pic=exportexcel.gif
