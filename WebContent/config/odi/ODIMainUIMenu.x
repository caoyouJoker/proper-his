<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;bedcard;|;onSign;|;card;|;print;|;erdTriage;|;tpr;|;newtpr;|;pdf;printLis;tnb;|;showpat;cxMrshow;|;exportxml;|;create;|;transfer;|;evalution;|;opeNursingRecord;|;clear;|;close
Window.Type=TMenu
Window.Text=����
Window.zhText=����
Window.enText=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.zhText=�ļ�
File.enText=File
File.M=F
File.Item=query;|;card;|;print;|;bedcard;|;tpr;|;newtpr;|;pdf;|;printLis;|;tnb;|;showpat;cxMrshow|;exportxml;|;create;|;transfer;|;evalution;|;opeNursingRecord;|;clear;|;close

onSign.Type=TMenuItem
onSign.Text=����ǩ��
onSign.zhText=����ǩ��
onSign.enText=onSign
onSign.Tip=����ǩ��
onSign.zhTip=����ǩ��
onSign.enTip=onSign
onSign.M=
onSign.Action=onSign
onSign.pic=clear.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.zhText=ˢ��
Refresh.enText=Refresh
Refresh.Tip=ˢ��
Refresh.zhTip=ˢ��
Refresh.enTip=Refresh
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

query.Type=TMenuItem
query.Text=��ѯ
query.zhText=��ѯ
query.enText=Query
query.Tip=��ѯ
query.zhTip=��ѯ
query.enTip=Query
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

card.Type=TMenuItem
card.Text=��ͷ��
card.zhText=��ͷ��
card.enText=bed card
card.Tip=��ͷ��
card.zhTip=��ͷ��
card.enTip=bed card
card.M=B
card.Action=onBedCard
card.pic=card.gif

clear.Type=TMenuItem
clear.Text=���
clear.zhText=���
clear.enText=Clear
clear.Tip=���
clear.zhTip=���
clear.enTip=Clear
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.zhText=�˳�
close.enText=Quit
close.Tip=�˳�
close.zhTip=�˳�
close.enTip=Quit
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif



bedcard.Type=TMenuItem
bedcard.Text=����
bedcard.zhText=����
bedcard.enText=Pat Info
bedcard.Tip=������Ϣ
bedcard.zhTip=������Ϣ
bedcard.enTip=Pat Info
bedcard.M=P
bedcard.Action=onPatInfo
bedcard.pic=bedcard.gif

tpr.Type=TMenuItem
tpr.Text=���µ�
tpr.Tip=���µ�
tpr.M=J
tpr.key=Ctrl+T
tpr.Action=onVitalSign
tpr.pic=023.gif

newtpr.Type=TMenuItem
newtpr.Text=���������µ�
newtpr.Tip=���������µ�
newtpr.M=J
newtpr.key=Ctrl+P
newtpr.Action=onNewArrival
newtpr.pic=035.gif

pdf.Type=TMenuItem
pdf.Text=��������
pdf.zhText=��������
pdf.enText=��������
pdf.Tip=��������
pdf.zhTip=��������
pdf.enTip=��������
pdf.M=X
pdf.Action=onSubmitPDF
pdf.pic=005.gif

tnb.Type=TMenuItem
tnb.Text=Ѫ�Ǳ���
tnb.zhText=Ѫ�Ǳ���
tnb.enText=Ѫ�Ǳ���
tnb.Tip=Ѫ�Ǳ���
tnb.zhTip=Ѫ�Ǳ���
tnb.enTip=Ѫ�Ǳ���
tnb.M=S
tnb.Action=onTnb
tnb.pic=modify.gif

exportxml.Type=TMenuItem
exportxml.Text=����
exportxml.Tip=����
exportxml.M=P
exportxml.Action=onExport
exportxml.pic=export.gif

print.Type=TMenuItem
print.Text=��ͷ����ӡ
print.Tip=��ͷ����ӡ
print.M=F
print.key=Ctrl+P
print.Action=onPrintO
print.pic=Print.gif

printLis.Type=TMenuItem
printLis.Text=LIS����
printLis.Tip=��ӡLIS����
printLis.M=L
printLis.key=Ctrl+L
printLis.Action=onPrintLis
printLis.pic=print-1.gif


nis.Type=TMenuItem
nis.Text=�����
nis.zhText=�����
nis.enText=Form
nis.Tip=�����
nis.zhTip=�����
nis.enTip=Quit
nis.M=N
nis.key=Ctrl+N
nis.Action=onHLSel
nis.pic=Column.gif

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

erdTriage.Type=TMenuItem
erdTriage.Text=��������
erdTriage.Tip=��������
erdTriage.M=
erdTriage.key=
erdTriage.Action=onErdTriage
erdTriage.pic=emr-2.gif

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

evalution.Type=TMenuItem
evalution.Text=����һ����
evalution.Tip=����һ����
evalution.M=X
evalution.key=Alt+F4
evalution.Action=onEvalutionRecordOpen
evalution.pic=correct.gif

opeNursingRecord.Type=TMenuItem
opeNursingRecord.Text=���뻤���¼
opeNursingRecord.zhText=���뻤���¼
opeNursingRecord.enText=
opeNursingRecord.Tip=���뻤���¼
opeNursingRecord.zhTip=���뻤���¼
opeNursingRecord.enTip=
opeNursingRecord.M=
opeNursingRecord.key=
opeNursingRecord.Action=onOpeNursingRecord
opeNursingRecord.pic=query.gif