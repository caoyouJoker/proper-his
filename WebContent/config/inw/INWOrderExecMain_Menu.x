<Type=TMenuBar>
UI.Item=File;Window;nurseWork;report/result
UI.button=save;query;clear;|;print;|;Newprint;|;code;barCode;|;pasterBottle;|;skiResult;|;schdCode;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;delete;Refresh;query;|;print;|;Newprint;code;pasterBottle;|;checkrep;|;testrep;|;clear;|;skiResult;|;schdCode;|;close

nurseWork.Type=TMenu
nurseWork.Text=����ҵ��
nurseWork.M=N
nurseWork.Item=emr;nursingRecord;nis;tpr;newtpr;charge

report/result.Type=TMenu
report/result.Text=����/���
report/result.M=R
report/result.Item=labReport;imageReport;eccReport;xtReport;bgReport;intensiveCare;militaryRecord;getQiTaPDF

//===========================================
emr.Type=TMenuItem
emr.Text=�ṹ������
emr.Tip=�ṹ������
emr.M=J
emr.key=Ctrl+J
emr.Action=onEmr
emr.pic=emr-2.gif

getQiTaPDF.Type=TMenuItem
getQiTaPDF.Text=��������
getQiTaPDF.zhText=��������
getQiTaPDF.enText=getQiTaPDF
getQiTaPDF.Tip=��������
getQiTaPDF.zhTip=��������
getQiTaPDF.enTip=getQiTaPDF
getQiTaPDF.M=C
getQiTaPDF.Action=getPDFQiTa
getQiTaPDF.pic=PicData01.jpg

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

charge.Type=TMenuItem
charge.Text=����Ʒ�
charge.Tip=����Ʒ�
charge.M=H
charge.key=Ctrl+H
charge.Action=onCharge
charge.pic=bill-1.gif

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

//====================================================
save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

skiResult.Type=TMenuItem
skiResult.Text=Ƥ�Խ��
skiResult.Tip=Ƥ�Խ��
skiResult.M=P
skiResult.Action=onSkiResult
skiResult.pic=032.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

print.Type=TMenuItem
print.Text=����ִ�е�
print.Tip=����ִ�е�
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

Newprint.Type=TMenuItem
Newprint.Text=����ִ�е�
Newprint.Tip=����ִ�е�
Newprint.M=PN
Newprint.Action=onPrintExe
Newprint.pic=print-1.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

code.Type=TMenuItem
code.Text=��������
code.Tip=��������
code.M=CO
code.Action=onBarCode
code.pic=barcode.gif

paster.Type=TMenuItem
paster.Text=��ӡ��ֽ
paster.Tip=��ӡ��ֽ
paster.Action=onPrintPaster
paster.pic=048.gif

schdCode.Type=TMenuItem
schdCode.Text=·��ʱ��
schdCode.Tip=·��ʱ��
schdCode.M=IS
schdCode.Action=onChangeSchd
schdCode.pic=convert.gif

pasterBottle.Type=TMenuItem
pasterBottle.Text=ƿǩ��ֽ
pasterBottle.Tip=ƿǩ��ֽ
pasterBottle.Action=onPrintPasterBottle
pasterBottle.pic=048.gif

barCode.Type=TMenuItem
barCode.Text=ҩƷ����
barCode.Tip=ҩƷ����
barCode.Action=GeneratPhaBarcode
barCode.pic=PHL.gif


checkrep.Type=TMenuItem
checkrep.Text=���鱨��
checkrep.Tip=���鱨��
checkrep.M=
checkrep.key=
checkrep.Action=onCheckrep
checkrep.pic=Lis.gif

testrep.Type=TMenuItem
testrep.Text=��鱨��
testrep.Tip=��鱨��
testrep.M=
testrep.key=
testrep.Action=onTestrep
testrep.pic=emr-2.gif

nis.Type=TMenuItem
nis.Text=����ƻ�
nis.Tip=����ƻ�
nis.M=
nis.key=
nis.Action=onNis
nis.pic=emr-2.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClosePanel
close.pic=close.gif