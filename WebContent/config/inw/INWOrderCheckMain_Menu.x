<Type=TMenuBar>
UI.Item=File;Window;nurseWork;report/result
UI.button=save;|;query;|;clear;|;Newprint;|;medPrint;|;medApplyNo;|;send;|;AMI;|;pay;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;delete;Refresh;query;Newprint;medPrint;|;medApplyNo;|;send;|;clear;|;pay;|;close

nurseWork.Type=TMenu
nurseWork.Text=����ҵ��
nurseWork.M=N
nurseWork.Item=record;nursingRecord;tpr;newtpr

report/result.Type=TMenu
report/result.Text=����/���
report/result.M=R
report/result.Item=labReport;imageReport;eccReport;xtReport;bgReport;intensiveCare;militaryRecord

//===========================================


AMI.Type=TMenuItem
AMI.Text=��ʹ����
AMI.Tip=��ʹ����
AMI.M=
AMI.key=
AMI.Action=onAMI
AMI.pic=query.gif


record.Type=TMenuItem
record.Text=�ṹ������
record.zhText=�ṹ������
record.enText=record
record.Tip=�ṹ������
record.zhTip=�ṹ������
record.enTip=record
record.M=RE
record.Action=onEmrWrite
record.pic=emr.gif

nursingRecord.Type=TMenuItem
nursingRecord.Text=�����¼
nursingRecord.zhText=�����¼
nursingRecord.enText=nursingRecord
nursingRecord.Tip=�����¼
nursingRecord.zhTip=�����¼
nursingRecord.enTip=nursingRecord
nursingRecord.M=N
nursingRecord.Action=onHLSel
nursingRecord.pic=nurse-1.gif

tpr.Type=TMenuItem
tpr.Text=���µ�
tpr.Tip=���µ�
tpr.M=J
tpr.key=Ctrl+T
tpr.Action=onVitalSign
tpr.pic=023.gif

newtpr.Type=TMenuItem
newtpr.Text=��ͯ�����µ�
newtpr.Tip=��ͯ�����µ�
newtpr.M=J
newtpr.key=Ctrl+P
newtpr.Action=onNewArrival
newtpr.pic=035.gif

//=================================================
labReport.Type=TMenuItem
labReport.Text=���鱨��
labReport.zhText=���鱨��
labReport.enText=labReport
labReport.Tip=���鱨��
labReport.zhTip=���鱨��
labReport.enTip=labReport
labReport.M=L
labReport.Action=onLis
labReport.pic=LIS.gif

imageReport.Type=TMenuItem
imageReport.Text=��鱨��
imageReport.zhText=��鱨��
imageReport.enText=imageReport
imageReport.Tip=��鱨��
imageReport.zhTip=��鱨��
imageReport.enTip=imageReport
imageReport.M=I
imageReport.Action=onRis
imageReport.pic=RIS.gif

eccReport.Type=TMenuItem
eccReport.Text=�ĵ籨��
eccReport.zhText=�ĵ籨��
eccReport.enText=eccReport
eccReport.Tip=�ĵ籨��
eccReport.zhTip=�ĵ籨��
eccReport.enTip=eccReport
eccReport.M=C
eccReport.Action=getPdfReport
eccReport.pic=PicData01.jpg

xtReport.Type=TMenuItem
xtReport.Text=Ѫ�Ǳ���(NOVA)
xtReport.zhText=Ѫ�Ǳ���(NOVA)
xtReport.enText=xtReport
xtReport.Tip=Ѫ�Ǳ���(NOVA)
xtReport.zhTip=Ѫ�Ǳ���(NOVA)
xtReport.enTip=xtReport
xtReport.M=T
xtReport.Action=getXTReport
xtReport.pic=Retrieve.gif

bgReport.Type=TMenuItem
bgReport.Text=Ѫ�Ǳ���(ǿ��)
bgReport.zhText=Ѫ�Ǳ���(ǿ��)
bgReport.enText=bgReport
bgReport.Tip=Ѫ�Ǳ���(ǿ��)
bgReport.zhTip=Ѫ�Ǳ���(ǿ��)
bgReport.enTip=bgReport
bgReport.M=J
bgReport.Action=getBgReport
bgReport.pic=Retrieve.gif

intensiveCare.Type=TMenuItem
intensiveCare.Text=��֢�໤
intensiveCare.zhText=��֢�໤
intensiveCare.enText=intensiveCare
intensiveCare.Tip=��֢�໤
intensiveCare.zhTip=��֢�໤
intensiveCare.enTip=intensiveCare
intensiveCare.M=N
intensiveCare.Action=getCCEmrData
intensiveCare.pic=013.gif

militaryRecord.Type=TMenuItem
militaryRecord.Text=���鲡��
militaryRecord.zhText=���鲡��
militaryRecord.enText=militaryRecord
militaryRecord.Tip=���鲡��
militaryRecord.zhTip=���鲡��
militaryRecord.enTip=militaryRecord
militaryRecord.M=Q
militaryRecord.Action=getOpeMrData
militaryRecord.pic=048.gif

//===============================================

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

send.Type=TMenuItem
send.Text=����
send.Tip=����
send.M=O
send.key=Ctrl+O
send.Action=onReSendGYPha
send.pic=Commit.gif


query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Newprint.Type=TMenuItem
Newprint.Text=���˴�ӡ
Newprint.Tip=���˴�ӡ
Newprint.M=PN
Newprint.Action=onPrintExe
Newprint.pic=print-1.gif

medPrint.Type=TMenuItem
medPrint.Text=ȡҩ����ӡ
medPrint.Tip=ȡҩ����ӡ
medPrint.Action=onDispenseSheet
medPrint.pic=print-2.gif



clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClosePanel
close.pic=close.gif

medApplyNo.Type=TMenuItem
medApplyNo.Text=��������
medApplyNo.Tip=��������
medApplyNo.M=C
medApplyNo.Action=onMedApplyPrint
medApplyNo.pic=barCode.gif

pay.Type=TMenuItem
pay.Text=����Ʒ�
pay.Tip=����Ʒ�
pay.M=P
pay.Action=onPay
pay.pic=bill-1.gif
