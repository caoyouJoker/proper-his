#
  # Title: ҽ���ϴ�����
  #
  # Description:ҽ���ϴ�����
  #
  # Copyright: ProperSoft(c) 2012
  #
  # @author pangben 2012-2-3
  # @version 2.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;apply;|;upload;|;detailupload;|;changeInfo;|;onSave;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;apply;|;upload;|;detailupload;|;changeInfo;|;onSave;|;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+Q
query.Action=onQuery
query.pic=query.gif

apply.Type=TMenuItem
apply.Text=ת����ϸ
apply.Tip=ת����ϸ
apply.M=A
apply.Action=onApply
apply.pic=sta-1.gif

upload.Type=TMenuItem
upload.Text=�ָ�
upload.Tip=�ָ�
upload.M=U
upload.Action=onUpdate
upload.pic=016.gif

detailupload.Type=TMenuItem
detailupload.Text=����ϸ�ϴ�
detailupload.Tip=����ϸ�ϴ�
detailupload.M=U
detailupload.Action=ondetailUpdate
detailupload.pic=Commit.gif

changeInfo.Type=TMenuItem
changeInfo.Text=ת������������
changeInfo.Tip=ת������������
changeInfo.M=I
changeInfo.Action=onQueryInfo
changeInfo.pic=pat.gif


onSave.Type=TMenuItem
onSave.Text=����
onSave.Tip=����
onSave.M=S
onSave.Action=onSettlement
onSave.pic=018.gif

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