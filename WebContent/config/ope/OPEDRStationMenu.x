<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;opInfo;opRecord;|;cxMrshow;|;showpat;|;remove;|;unlock;|;clear;|;close

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

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��(F5)
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

remove.Type=TMenuItem
remove.Text=���
remove.Tip=���(Ctrl+Z)
remove.M=J
remove.key=Ctrl+J
remove.Action=onRemove
remove.pic=delete.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif


cxMrshow.Type=TMenuItem
cxMrshow.Text=ʱ���Ს��
cxMrshow.Tip=ʱ���Ს��
cxMrshow.M=Q
cxMrshow.key=Ctrl+Q
cxMrshow.Action=onCxShow
cxMrshow.pic=038.gif

showpat.Type=TMenuItem
showpat.Text=���ξ���
showpat.zhText=���ξ���
showpat.enText=Pat Info
showpat.Tip=���ξ���
showpat.zhTip=���ξ���
showpat.enTip=Pat Info
showpat.M=P
showpat.key=Ctrl+P
showpat.Action=onQuerySummaryInfo
showpat.pic=patlist.gif


unlock.Type=TMenuItem
unlock.Text=��ʱ����
unlock.zhText=��ʱ����
unlock.enText=��ʱ����
unlock.Tip=��ʱ����
unlock.zhTip=��ʱ����
unlock.enTip=��ʱ����
unlock.M=G
unlock.key=
unlock.Action=onUnlock
unlock.pic=032.gif

