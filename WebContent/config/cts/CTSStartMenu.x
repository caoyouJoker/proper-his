#
  # Title: ��������ѡ��
  #
  # Description:��������ѡ��
  #
  # Copyright: ProperSoft (c) 2012
  #
  # @author zhangp
  # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;clear;|;close;

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;query;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=openbill.gif


query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
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

Recipients.Type=TMenuItem
Recipients.Text=��Ʊ
Recipients.Tip=��Ʊ
Recipients.M=T
Recipients.key=F9
Recipients.Action=Recipients
Recipients.pic=openbill-2.gif

returnback.Type=TMenuItem
returnback.Text=����
returnback.Tip=����
returnback.M=O
returnback.key=F10
returnback.Action=returnback
returnback.pic=closebill.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

Adjustment.Type=TMenuItem
Adjustment.Text=����Ʊ��
Adjustment.Tip=����Ʊ��
Adjustment.M=M
Adjustment.key=F11
Adjustment.Action=onAdjustment
Adjustment.pic=correct.gif

export.Type=TMenuItem
export.Text=���
export.Tip=���
export.M=E
export.key=F4
export.Action=onExport
export.pic=print.gif