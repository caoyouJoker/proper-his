#
  # Title: ��ũ����������
  # Description:��ũ����������
  # Copyright: 2017
  # @version 2.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;apply;|;upload;|;onCancel;|;onSaveY;|;onSave;|;onSaveC;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;apply;|;upload;|;onCancel;|;onSaveY;|;onSave;|;onSaveC;|;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+Q
query.Action=onQuery
query.pic=query.gif

apply.Type=TMenuItem
apply.Text=ת��ϸ
apply.Tip=ת��ϸ
apply.M=A
apply.Action=onApply
apply.pic=sta-1.gif

upload.Type=TMenuItem
upload.Text=��ϸ�ϴ�
upload.Tip=��ϸ�ϴ�
upload.M=U
upload.Action=onUpload
upload.pic=016.gif

onCancel.Type=TMenuItem
onCancel.Text=��ϸ����
onCancel.Tip=��ϸ����
onCancel.M=I
onCancel.Action=onCancelDetail
onCancel.pic=pat.gif

onSaveY.Type=TMenuItem
onSaveY.Text=Ԥ����
onSaveY.Tip=Ԥ����
onSaveY.M=S
onSaveY.Action=onSettlementY
onSaveY.pic=017.gif


onSave.Type=TMenuItem
onSave.Text=����
onSave.Tip=����
onSave.M=S
onSave.Action=onSettlement
onSave.pic=018.gif

onSaveC.Type=TMenuItem
onSaveC.Text=�˽���
onSaveC.Tip=�˽���
onSaveC.M=S
onSaveC.Action=onSettlementC
onSaveC.pic=019.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif