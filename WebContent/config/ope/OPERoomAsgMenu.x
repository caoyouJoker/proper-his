#############################################
# <p>Title:�����ų�Menu </p>
#
# <p>Description:�����ų�Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2009.09.27
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;asg;|;batchAsg;|;opInfo;|;opRecord;|;create;|;transfer;|;intervenNurPlat;|;consent;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��(F5)
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

asg.Type=TMenuItem
asg.Text=�����ų�
asg.Tip=�����ų�
asg.Action=onAsg
asg.pic=time.gif

batchAsg.Type=TMenuItem
batchAsg.Text=���������ų�
batchAsg.Tip=���������ų�
batchAsg.Action=onBatchAsg
batchAsg.pic=time.gif

opInfo.Type=TMenuItem
opInfo.Text=����������ϸ
opInfo.Tip=����������ϸ
opInfo.Action=onOpInfo
opInfo.pic=detail-1.gif

opRecord.Type=TMenuItem
opRecord.Text=������¼
opRecord.Tip=������¼
opRecord.Action=onOpRecord
opRecord.pic=031.gif

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

intervenNurPlat.Type=TMenuItem
intervenNurPlat.Text=���뻤���¼
intervenNurPlat.Tip=���뻤���¼
intervenNurPlat.Action=onPrint
intervenNurPlat.pic=spreadout.gif

consent.Type=TMenuItem
consent.Text=֪��ͬ����
consent.Tip=֪��ͬ����
consent.Action=onConsent
consent.pic=spreadout.gif

