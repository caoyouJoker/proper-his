#############################################
# <p>Title:���ת����Menu </p>
#
# <p>Description:���ת����Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK
# @version 1.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=outDept;|;inDept;|;bed;|;cancelBed;|;reload;|;cancelTrans;|;cancelInHospital;|;create;|;transfer;|;close


Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=outDept;|;inDept;|;reload;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=checkSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

bed.Type=TMenuItem
bed.Text=��������
bed.Tip=��������
bed.M=
bed.key=
bed.Action=onBed
bed.pic=048.gif

cancelBed.Type=TMenuItem
cancelBed.Text=ȡ������
cancelBed.Tip=ȡ������
cancelBed.M=
cancelBed.key=
cancelBed.Action=onCancelBed
cancelBed.pic=Undo.gif

outDept.Type=TMenuItem
outDept.Text=ת�ƹ���
outDept.Tip=ת�ƹ���
outDept.M=Q
outDept.key=Ctrl+F
outDept.Action=onOutDept
outDept.pic=tempsave.gif

inDept.Type=TMenuItem
inDept.Text=������Ϣ
inDept.Tip=������Ϣ
inDept.M=Q
inDept.key=Ctrl+F
inDept.Action=onInStation
inDept.pic=013.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

reload.Type=TMenuItem
reload.Text=������Ϣ
reload.Tip=������Ϣ
reload.M=
reload.key=Ctrl+R
reload.Action=onReload
reload.pic=008.gif

cancelTrans.Type=TMenuItem
cancelTrans.Text=ȡ��ת��
cancelTrans.Tip=ȡ��ת��
cancelTrans.M=Q
cancelTrans.key=Ctrl+F
cancelTrans.Action=onCancelTrans
cancelTrans.pic=002.gif

cancelInHospital.Type=TMenuItem
cancelInHospital.Text=ȡ�����
cancelInHospital.Tip=ȡ�����
cancelInHospital.M=Q
cancelInHospital.key=Ctrl+F
cancelInHospital.Action=onCancleInDP
cancelInHospital.pic=030.gif

create.Type=TMenuItem
create.Text=���ɽ��ӵ�
create.Tip=���ɽ��ӵ�
create.M=X
create.key=Alt+F4
create.Action=onCreate
create.pic=save.gif

transfer.Type=TMenuItem
transfer.Text=����һ����
transfer.Tip=����һ����
transfer.M=X
transfer.key=Alt+F4
transfer.Action=onTransfer
transfer.pic=correct.gif