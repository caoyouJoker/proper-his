 #
  # Title: ����ҽ�Ʒ���Ŀ��ϸ��
  #
  # Description: ����ҽ�Ʒ���Ŀ��ϸ��
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author lim 2011.09.28
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;EKTprint;|;OPDprint;|;INPprint;|;TPMprint;|;APDDown;|;APDexport;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;clear;|;EKTprint;|;OPDprint;|;INPprint;|;TPMprint;|;APDDown;|;APDexport;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.Action=onQuery
query.pic=query.gif

EKTprint.Type=TMenuItem
EKTprint.Text=��ӡ
EKTprint.Tip=��ӡ
EKTprint.M=P
EKTprint.key=Ctrl+P
EKTprint.Action=onPrint
EKTprint.pic=print.gif

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

OPDprint.Type=TMenuItem
OPDprint.Text=��������
OPDprint.Tip=��������
OPDprint.M=
OPDprint.key=
OPDprint.Action=print_O
OPDprint.pic=print.gif

INPprint.Type=TMenuItem
INPprint.Text=סԺ����
INPprint.Tip=סԺ����
INPprint.M=
INPprint.key=
INPprint.Action=print_P
INPprint.pic=print.gif

TPMprint.Type=TMenuItem
TPMprint.Text=�ܸ�����
TPMprint.Tip=�ܸ�����
TPMprint.M=
TPMprint.key=
TPMprint.Action=print_Q
TPMprint.pic=print.gif

APDDown.Type=TMenuItem
APDDown.Text=����֧����ϸ����
APDDown.Tip=����֧����ϸ����
APDDown.M=
APDDown.key=
APDDown.Action=APDDown
APDDown.pic=print.gif

APDexport.Type=TMenuItem
APDexport.Text=����֧����ϸ����
APDexport.Tip=����֧����ϸ����
APDexport.M=
APDexport.key=
APDexport.Action=APDexport
APDexport.pic=exportexcel.gif
