 #
  # Title: ��ͷ�����ָ������������Ϣ����
  #
  # Description: ��ͷ�����ָ������������Ϣ����
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author lim 2016.12.05
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=LiquidationTablePrint;|;LiquidationDetailsDown;|;AuditChargesDown;|;export;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=LiquidationTablePrint;|;LiquidationDetailsDown;|;AuditChargesDown;|;export;|;clear;|;close

LiquidationTablePrint.Type=TMenuItem
LiquidationTablePrint.Text=������ӡ
LiquidationTablePrint.Tip=������ӡ
LiquidationTablePrint.M=P
LiquidationTablePrint.key=Ctrl+P
LiquidationTablePrint.Action=onLiquidationTablePrint
LiquidationTablePrint.pic=print.gif

LiquidationDetailsDown.Type=TMenuItem
LiquidationDetailsDown.Text=������ϸ����
LiquidationDetailsDown.Tip=������ϸ����
LiquidationDetailsDown.Action=onLiquidationDetailsDown
LiquidationDetailsDown.pic=query.gif

AuditChargesDown.Type=TMenuItem
AuditChargesDown.Text=��˿ۿ�����
AuditChargesDown.Tip=��˿ۿ�����
AuditChargesDown.Action=onAuditChargesDown
AuditChargesDown.pic=print.gif

export.Type=TMenuItem
export.Text=���
export.Tip=���
export.M=E
export.key=F4
export.Action=onExport
export.pic=exportexcel.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
