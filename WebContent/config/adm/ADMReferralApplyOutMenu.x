 #
  # Title: ת�������ѯ
  #
  # Description:
  #
  # Copyright: JavaHis (c) 2015
  #
  # @author wangb 2015.8.10
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;referralApply;|;referralApplyHl7;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

referralApply.Type=TMenuItem
referralApply.Text=ת�ﵥ
referralApply.Tip=ת�ﵥ
referralApply.M=
referralApply.key=
referralApply.Action=onShowReferral
referralApply.pic=010.gif

referralApplyHl7.Type=TMenuItem
referralApplyHl7.Text=ת������HL7
referralApplyHl7.Tip=ת������HL7
referralApplyHl7.M=
referralApplyHl7.key=
referralApplyHl7.Action=onOpenRefHL7
referralApplyHl7.pic=Create.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
