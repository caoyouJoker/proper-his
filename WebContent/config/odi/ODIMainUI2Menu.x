<Type=TMenuBar>
UI.Item=File;Doctor;Nurse;Report;Window
UI.button=query;|;bedcard;|;onSign;|;erdTriage;|;card;|;cln;|;emr;bab;pdf;|;twd;|;assessReport;|;hos;|;reg;|;ibs;|;daysurgery;|;clear;|;communicate;|;unlock;|;close

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
File.Item=query;export;|;card;|;bedcard;|;daysurgery;|;clear;|;close

Doctor.Type=TMenu
Doctor.Text=ҽʦ����
Doctor.zhText=ҽʦ����
Doctor.enText=ҽʦ����
Doctor.M=D
Doctor.Item=cxMrshow;|;showpat;|;cln;|;emr;bab;opd;hrm;|;bas;|;smz;res;|;res1;|;hos;|;reg;pathology

Nurse.Type=TMenu
Nurse.Text=��ʿ����
Nurse.zhText=��ʿ����
Nurse.enText=��ʿ����
Nurse.M=N
Nurse.Item=sel;twd;hl;|;printLis;pdf;opeNursingRecord

Report.Type=TMenu
Report.Text=��������
Report.zhText=��������
Report.enText=PatReport
Report.M=R
Report.Item=lis;ris;tnb;bgReport;printLis


pathology.Type=TMenuItem
pathology.Text=��������
pathology.zhText=��������
pathology.enText=PathologyType
pathology.Tip=��������
pathology.zhTip=��������
pathology.enTip=PathologyType
pathology.Action=onPathologyType
pathology.pic=049.gif

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

assessReport.Type=TMenuItem
assessReport.Text=������
assessReport.zhText=������
assessReport.enText=assessReport
assessReport.Tip=������
assessReport.zhTip=������
assessReport.enTip=assessReport
assessReport.M=R
assessReport.Action=onEvalutionRecordOpen
assessReport.pic=Group.gif

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

cln.Type=TMenuItem
cln.Text=�ٴ����
cln.zhText=�ٴ����
cln.enText=�ٴ����
cln.Tip=�ٴ����
cln.zhTip=�ٴ����
cln.enTip=�ٴ����
cln.M=Q
cln.Action=onAddCLNCPath
cln.pic=009.gif

emr.Type=TMenuItem
emr.Text=д����
emr.zhText=д����
emr.enText=д����
emr.Tip=д����
emr.zhTip=д����
emr.enTip=д����
emr.M=S
emr.Action=onAddEmrWrite
emr.pic=emr-1.gif

bas.Type=TMenuItem
bas.Text=����
bas.Tip=����
bas.zhTip=������Ŀ
bas.enTip=������Ŀ
bas.M=A
bas.Action=onAddBASY
bas.pic=012.gif


bab.Type=TMenuItem
bab.Text=����
bab.zhText=����
bab.enText=����
bab.Tip=�������
bab.zhTip=�������
bab.enTip=�������
bab.M=S
bab.Action=onBABM
bab.pic=029.gif

sel.Type=TMenuItem
sel.Text=ҽ����
sel.zhText=ҽ����
sel.enText=ҽ����
sel.Tip=ҽ����
sel.zhTip=ҽ����
sel.enTip=ҽ����
sel.M=S
sel.Action=onSelYZD
sel.pic=017.gif

twd.Type=TMenuItem
twd.Text=���µ�
twd.zhText=���µ�
twd.enText=���µ�
twd.Tip=���µ�
twd.zhTip=���µ�
twd.enTip=���µ�
twd.M=S
twd.Action=onSelTWD
twd.pic=037.gif

hl.Type=TMenuItem
hl.Text=�����¼
hl.zhText=�����¼
hl.enText=�����¼
hl.Tip=�����¼
hl.zhTip=�����¼
hl.enTip=�����¼
hl.M=S
hl.Action=onNisFormList
hl.pic=048.gif

smz.Type=TMenuItem
smz.Text=����
smz.zhText=����
smz.enText=����
smz.Tip=��������
smz.zhTip=��������
smz.enTip=��������
smz.M=S
smz.Action=onSSMZ
smz.pic=051.gif

res.Type=TMenuItem
res.Text=��Ѫ
res.zhText=��Ѫ
res.enText=��Ѫ
res.Tip=��Ѫ
res.zhTip=��Ѫ
res.enTip=��Ѫ
res.M=S
res.Action=onBXResult
res.pic=blood.gif

res1.Type=TMenuItem
res1.Text=ȡѪ
res1.zhText=ȡѪ
res1.enText=ȡѪ
res1.Tip=ȡѪ
res1.zhTip=ȡѪ
res1.enTip=ȡѪ
res1.M=S
res1.Action=onQXResult
res1.pic=blood.gif


opd.Type=TMenuItem
opd.Text=�ż�����
opd.zhText=�ż�����
opd.enText=�ż�����
opd.Tip=�ż��ﲡ��
opd.zhTip=�ż��ﲡ��
opd.enTip=�ż��ﲡ��
opd.M=S
opd.Action=onOpdBL
opd.pic=032.gif

lis.Type=TMenuItem
lis.Text=���鱨��
lis.zhText=���鱨��
lis.enText=���鱨��
lis.Tip=���鱨��
lis.zhTip=���鱨��
lis.enTip=���鱨��
lis.M=S
lis.Action=onLis
lis.pic=LIS.gif

ris.Type=TMenuItem
ris.Text=��鱨��
ris.zhText=��鱨��
ris.enText=��鱨��
ris.Tip=��鱨��
ris.zhTip=��鱨��
ris.enTip=��鱨��
ris.M=S
ris.Action=onRis
ris.pic=RIS.gif

hos.Type=TMenuItem
hos.Text=��Ժ
hos.zhText=��Ժ
hos.enText=��Ժ
hos.Tip=��Ժ֪ͨ
hos.zhTip=��Ժ֪ͨ
hos.enTip=��Ժ֪ͨ
hos.M=S
hos.Action=onOutHosp
hos.pic=015.gif

ibs.Type=TMenuItem
ibs.Text=����
ibs.zhText=����
ibs.enText=����
ibs.Tip=���ò�ѯ
ibs.zhTip=���ò�ѯ
ibs.enTip=���ò�ѯ
ibs.M=S
ibs.Action=onSelIbs
ibs.pic=fee.gif

tnb.Type=TMenuItem
tnb.Text=Ѫ�Ǳ���(NOVA)
tnb.zhText=Ѫ�Ǳ���(NOVA)
tnb.enText=Ѫ�Ǳ���(NOVA)
tnb.Tip=Ѫ�Ǳ���(NOVA)
tnb.zhTip=Ѫ�Ǳ���(NOVA)
tnb.enTip=Ѫ�Ǳ���(NOVA)
tnb.M=S
tnb.Action=onTnb
tnb.pic=modify.gif

bgReport.Type=TMenuItem
bgReport.Text=Ѫ�Ǳ���(ǿ��)
bgReport.zhText=Ѫ�Ǳ���(ǿ��)
bgReport.enText=bgReport
bgReport.Tip=Ѫ�Ǳ���(ǿ��)
bgReport.zhTip=Ѫ�Ǳ���(ǿ��)
bgReport.enTip=bgReport
bgReport.M=J
bgReport.Action=getBgReport
bgReport.pic=modify.gif

export.Type=TMenuItem
export.Text=����
export.Tip=����
export.M=E
export.key=Ctrl+E
export.Action=onExport
export.pic=export.gif

reg.Type=TMenuItem
reg.Text=ԤԼ
reg.zhText=ԤԼ
reg.enText=Appointment of registered
reg.Tip=ԤԼ�Һ�
reg.zhTip=ԤԼ�Һ�
reg.enTip=Appointment of registered
reg.Action=onReg
reg.pic=date.gif

printLis.Type=TMenuItem
printLis.Text=��ӡLIS����
printLis.Tip=��ӡLIS����
printLis.zhText=��ӡLIS����
printLis.M=L
printLis.key=Ctrl+L
printLis.Action=onPrintLis
printLis.pic=print-1.gif

cxMrshow.Type=TMenuItem
cxMrshow.Text=ʱ���Ს��
cxMrshow.Tip=ʱ���Ს��(Ctrl+Q)
cxMrshow.M=Q
cxMrshow.key=Ctrl+Q
cxMrshow.Action=onCxShow
cxMrshow.pic=038.gif

showpat.Type=TMenuItem
showpat.Text=���ξ���
showpat.zhText=���ξ���
showpat.enText=Pat Info
showpat.Tip=CDR
showpat.zhTip=CDR
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

hrm.Type=TMenuItem
hrm.Text=�����ܼ�
hrm.zhText=�����ܼ�
hrm.Tip=�����ܼ�
hrm.zhTip=�����ܼ�
hrm.Action=onHrmEmr
hrm.pic=039.gif

daysurgery.Type=TMenuItem
daysurgery.Text=�ռ��������
daysurgery.zhText=�ռ��������
daysurgery.enText=�ռ��������
daysurgery.Tip=�ռ��������
daysurgery.zhTip=�ռ��������
daysurgery.enTip=�ռ��������
daysurgery.M=R
daysurgery.key=
daysurgery.Action=onDaySurgery
daysurgery.pic=025.gif

communicate.Type=TMenuItem
communicate.Text=��ͨ
communicate.Tip=��ͨ
communicate.M=F
communicate.key=F6
communicate.Action=onCommunicate
communicate.pic=AlignWidth.GIF


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



