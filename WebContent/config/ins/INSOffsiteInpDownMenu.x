 #
  # Title: 人头及病种付费月清算表信息下载
  #
  # Description: 人头及病种付费月清算表信息下载
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author lim 2016.12.05
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=LiquidationTablePrint;|;LiquidationDetailsDown;|;AuditChargesDown;|;export;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=LiquidationTablePrint;|;LiquidationDetailsDown;|;AuditChargesDown;|;export;|;clear;|;close

LiquidationTablePrint.Type=TMenuItem
LiquidationTablePrint.Text=清算表打印
LiquidationTablePrint.Tip=清算表打印
LiquidationTablePrint.M=P
LiquidationTablePrint.key=Ctrl+P
LiquidationTablePrint.Action=onLiquidationTablePrint
LiquidationTablePrint.pic=print.gif

LiquidationDetailsDown.Type=TMenuItem
LiquidationDetailsDown.Text=清算明细下载
LiquidationDetailsDown.Tip=清算明细下载
LiquidationDetailsDown.Action=onLiquidationDetailsDown
LiquidationDetailsDown.pic=query.gif

AuditChargesDown.Type=TMenuItem
AuditChargesDown.Text=审核扣款下载
AuditChargesDown.Tip=审核扣款下载
AuditChargesDown.Action=onAuditChargesDown
AuditChargesDown.pic=print.gif

export.Type=TMenuItem
export.Text=汇出
export.Tip=汇出
export.M=E
export.key=F4
export.Action=onExport
export.pic=exportexcel.gif

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
