<Type=TMenuBar>
UI.Item=File;Window;Package;Clinical;Report;Clp;Emr;Other
UI.button=save;query;clear;|;delTableRow;|;cxMrshow;|;medApplyNo;|;charge;|;cdss;|;batchModOrderDate;|;communicate;|;close

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
File.Item=save;query;clear;|;delTableRow;|;cxMrshow;|;medApplyNo;|;charge;|;close

Package.Type=TMenu
Package.Text=ģ���ײ�
Package.zhText=ģ���ײ�
Package.enText=Package
Package.M=P
Package.Item=deptOrder;drOrder;deptPack;drPack;clpPack;examList;applyForm;applyPha;infec;drugAlert

Clinical.Type=TMenu
Clinical.Text=�ٴ�ҵ��
Clinical.zhText=�ٴ�ҵ��
Clinical.enText=Clinical
Clinical.M=C
Clinical.Item=clpDiag;record;consApply;surgeryApply;bloodApply;getBloodApp;discharge;basy

Report.Type=TMenu
Report.Text=����/���
Report.zhText=����/���
Report.enText=Report
Report.M=R
Report.Item=orderSheet;vitalSign;assessReport;nursingRecord;labReport;imageReport;eccReport;getQiTaPDF;xtReport;bgReport;intensiveCare;militaryRecord

Clp.Type=TMenu
Clp.Text=�ٴ�·��
Clp.zhText=�ٴ�·��
Clp.enText=Clp
Clp.M=L
Clp.Item=clpMain;addPath;duration;clpVariation;clpOrder

Emr.Type=TMenu
Emr.Text=EMR
Emr.zhText=EMR
Emr.enText=EMR
Emr.M=E
Emr.Item=opRecord;showpat;cxMrshow;pdf

Other.Type=TMenu
Other.Text=����
Other.zhText=����
Other.enText=Other
Other.M=O
Other.Item=expend;babm

deptOrder.Type=TMenuItem
deptOrder.Text=����ҽ��
deptOrder.zhText=����ҽ��
deptOrder.enText=DeptOrder
deptOrder.Tip=����ҽ��
deptOrder.zhTip=����ҽ��
deptOrder.enTip=DeptOrder
deptOrder.M=D
deptOrder.Action=onDeptOrder
deptOrder.pic=inscon.gif

drOrder.Type=TMenuItem
drOrder.Text=ҽʦҽ��
drOrder.zhText=ҽʦҽ��
drOrder.enText=drOrder
drOrder.Tip=ҽʦҽ��
drOrder.zhTip=ҽʦҽ��
drOrder.enTip=drOrder
drOrder.M=Y
drOrder.Action=onDrOrder
drOrder.pic=Right.gif

deptPack.Type=TMenuItem
deptPack.Text=�����ײ�
deptPack.zhText=�����ײ�
deptPack.enText=deptPack
deptPack.Tip=�����ײ�
deptPack.zhTip=�����ײ�
deptPack.enTip=deptPack
deptPack.M=P
deptPack.Action=onDeptPack
deptPack.pic=bill-3.gif

drPack.Type=TMenuItem
drPack.Text=ҽʦ�ײ�
drPack.zhText=ҽʦ�ײ�
drPack.enText=drPack
drPack.Tip=ҽʦ�ײ�
drPack.zhTip=ҽʦ�ײ�
drPack.enTip=drPack
drPack.M=K
drPack.Action=onDrPack
drPack.pic=sta.gif

clpPack.Type=TMenuItem
clpPack.Text=·���ײ�
clpPack.zhText=·���ײ�
clpPack.enText=clpPack
clpPack.Tip=·���ײ�
clpPack.zhTip=·���ײ�
clpPack.enTip=clpPack
clpPack.M=F
clpPack.Action=onClpPack
clpPack.pic=org.gif

examList.Type=TMenuItem
examList.Text=������
examList.zhText=������
examList.enText=examList
examList.Tip=������
examList.zhTip=������
examList.enTip=examList
examList.M=X
examList.Action=onInputList
examList.pic=RIS-1.gif

applyForm.Type=TMenuItem
applyForm.Text=���뵥
applyForm.zhText=���뵥
applyForm.enText=applyForm
applyForm.Tip=���뵥
applyForm.zhTip=���뵥
applyForm.enTip=applyForm
applyForm.M=A
applyForm.Action=onApplyList
applyForm.pic=RIS.gif

applyPha.Type=TMenuItem
applyPha.Text=����ҩ���뵥
applyPha.zhText=����ҩ���뵥
applyPha.enText=applyPha
applyPha.Tip=����ҩ���뵥
applyPha.zhTip=����ҩ���뵥
applyPha.enTip=applyPha
applyPha.M=H
applyPha.Action=onApplyListPha
applyPha.pic=027.gif

infec.Type=TMenuItem
infec.Text=����ҩ��
infec.zhText=����ҩ��
infec.enText=infec
infec.Tip=����ҩ��
infec.zhTip=����ҩ��
infec.enTip=infec
infec.M=Q
infec.Action=onInfecPack
infec.pic=051.gif

drugAlert.Type=TMenuItem
drugAlert.Text=������ҩ
drugAlert.zhText=������ҩ
drugAlert.enText=drugAlert
drugAlert.Tip=������ҩ
drugAlert.zhTip=������ҩ
drugAlert.enTip=drugAlert
drugAlert.M=Q
drugAlert.Action=onRational
drugAlert.pic=openbil-1.gif
//=========================

clpDiag.Type=TMenuItem
clpDiag.Text=�ٴ����
clpDiag.zhText=�ٴ����
clpDiag.enText=clpDiag
clpDiag.Tip=�ٴ����
clpDiag.zhTip=�ٴ����
clpDiag.enTip=clpDiag
clpDiag.M=clp
clpDiag.Action=onLcICD
clpDiag.pic=sta-1.gif

record.Type=TMenuItem
record.Text=������д
record.zhText=������д
record.enText=record
record.Tip=������д
record.zhTip=������д
record.enTip=record
record.M=RE
record.Action=onEmrWrite
record.pic=emr.gif

consApply.Type=TMenuItem
consApply.Text=��������
consApply.zhText=��������
consApply.enText=consApply
consApply.Tip=��������
consApply.zhTip=��������
consApply.enTip=consApply
consApply.M=Q
consApply.Action=onConsApply
consApply.pic=Commit.gif

surgeryApply.Type=TMenuItem
surgeryApply.Text=��������
surgeryApply.zhText=��������
surgeryApply.enText=surgeryApply
surgeryApply.Tip=��������
surgeryApply.zhTip=��������
surgeryApply.enTip=surgeryApply
surgeryApply.M=SS
surgeryApply.Action=onSSMZ
surgeryApply.pic=odidrimg.gif

bloodApply.Type=TMenuItem
bloodApply.Text=��Ѫ����
bloodApply.zhText=��Ѫ����
bloodApply.enText=bloodApply
bloodApply.Tip=��Ѫ����
bloodApply.zhTip=��Ѫ����
bloodApply.enTip=bloodApply
bloodApply.M=BL
bloodApply.Action=onBXResult
bloodApply.pic=blood.gif

getBloodApp.Type=TMenuItem
getBloodApp.Text=ȡѪ����
getBloodApp.zhText=ȡѪ����
getBloodApp.enText=getBloodApp
getBloodApp.Tip=ȡѪ����
getBloodApp.zhTip=ȡѪ����
getBloodApp.enTip=getBloodApp
getBloodApp.M=GBA
getBloodApp.Action=onQXResult
getBloodApp.pic=blood.gif

discharge.Type=TMenuItem
discharge.Text=��Ժ֪ͨ
discharge.zhText=��Ժ֪ͨ
discharge.enText=discharge
discharge.Tip=��Ժ֪ͨ
discharge.zhTip=��Ժ֪ͨ
discharge.enTip=discharge
discharge.M=Q
discharge.Action=onOutHosp
discharge.pic=044.gif

basy.Type=TMenuItem
basy.Text=������Ŀ
basy.zhText=������Ŀ
basy.enText=basy
basy.Tip=������Ŀ
basy.zhTip=������Ŀ
basy.enTip=basy
basy.M=Q
basy.Action=onBASY
basy.pic=037.gif
//==========================
orderSheet.Type=TMenuItem
orderSheet.Text=ҽ����
orderSheet.zhText=ҽ����
orderSheet.enText=orderSheet
orderSheet.Tip=ҽ����
orderSheet.zhTip=ҽ����
orderSheet.enTip=orderSheet
orderSheet.M=O
orderSheet.Action=onSelYZD
orderSheet.pic=010.gif



vitalSign.Type=TMenuItem
vitalSign.Text=���µ�
vitalSign.zhText=���µ�
vitalSign.enText=vitalSign
vitalSign.Tip=���µ�
vitalSign.zhTip=���µ�
vitalSign.enTip=vitalSign
vitalSign.M=V
vitalSign.Action=onSelTWD
vitalSign.pic=patlist.gif

assessReport.Type=TMenuItem
assessReport.Text=������
assessReport.zhText=������
assessReport.enText=assessReport
assessReport.Tip=������
assessReport.zhTip=������
assessReport.enTip=assessReport
assessReport.M=R
assessReport.Action=assessReport
assessReport.pic=Group.gif

nursingRecord.Type=TMenuItem
nursingRecord.Text=�����¼
nursingRecord.zhText=�����¼
nursingRecord.enText=nursingRecord
nursingRecord.Tip=�����¼
nursingRecord.zhTip=�����¼
nursingRecord.enTip=nursingRecord
nursingRecord.M=N
nursingRecord.Action=onNisFormList
nursingRecord.pic=nurse-1.gif

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


//==========================
clpMain.Type=TMenuItem
clpMain.Text=·��׼��
clpMain.zhText=·��׼��
clpMain.enText=clpMain
clpMain.Tip=·��׼��
clpMain.zhTip=·��׼��
clpMain.enTip=clpMain
clpMain.M=M
clpMain.Action=onClpManageM
clpMain.pic=Arrow.gif

addPath.Type=TMenuItem
addPath.Text=����·��
addPath.zhText=����·��
addPath.enText=addPath
addPath.Tip=����·��
addPath.zhTip=����·��
addPath.enTip=addPath
addPath.M=D
addPath.Action=onAddCLNCPath
addPath.pic=convert.gif

duration.Type=TMenuItem
duration.Text=·��ʱ��
duration.zhText=·��ʱ��
duration.enText=duration
duration.Tip=·��ʱ��
duration.zhTip=·��ʱ��
duration.enTip=duration
duration.M=DU
duration.Action=intoDuration
duration.pic=Preview1.gif

clpVariation.Type=TMenuItem
clpVariation.Text=�������
clpVariation.zhText=�������
clpVariation.enText=drugAlert
clpVariation.Tip=�������
clpVariation.zhTip=�������
clpVariation.enTip=drugAlert
clpVariation.M=Q
clpVariation.Action=onClpVariation
clpVariation.pic=search-2.gif
//==========================
opRecord.Type=TMenuItem
opRecord.Text=�ż��ﲡ��
opRecord.zhText=�ż��ﲡ��
opRecord.enText=opRecord
opRecord.Tip=�ż��ﲡ��
opRecord.zhTip=�ż��ﲡ��
opRecord.enTip=opRecord
opRecord.M=Q
opRecord.Action=onOpdBL
opRecord.pic=spreadout.gif


//============================
expend.Type=TMenuItem
expend.Text=���ò�ѯ
expend.zhText=���ò�ѯ
expend.enText=expend
expend.Tip=���ò�ѯ
expend.zhTip=���ò�ѯ
expend.enTip=expend
expend.M=E
expend.Action=onSelIbs
expend.pic=fee.gif

babm.Type=TMenuItem
babm.Text=�������
babm.zhText=�������
babm.enText=babm
babm.Tip=�������
babm.zhTip=�������
babm.enTip=babm
babm.M=Q
babm.Action=onBABM
babm.pic=034.gif
//================================
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

save.Type=TMenuItem
save.Text=����
save.zhText=����
save.enText=Save
save.Tip=����
save.zhTip=����
save.enTip=Save
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

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

delTableRow.Type=TMenuItem
delTableRow.Text=ɾ��ҽ��
delTableRow.zhText=ɾ��ҽ��
delTableRow.enText=Delete
delTableRow.Tip=ɾ��ҽ��
delTableRow.zhTip=ɾ��ҽ��
delTableRow.enTip=Delete
delTableRow.M=D
delTableRow.Action=onDelRow
delTableRow.pic=delete.gif

mrshow.Type=TMenuItem
mrshow.Text=�������
mrshow.Tip=�������(Ctrl+W)
mrshow.M=W
mrshow.key=Ctrl+W
mrshow.Action=onShow
mrshow.pic=012.gif

cxMrshow.Type=TMenuItem
cxMrshow.Text=ʱ���Ს��
cxMrshow.Tip=ʱ���Ს��(Ctrl+Q)
cxMrshow.M=Q
cxMrshow.key=Ctrl+Q
cxMrshow.Action=onCxShow
cxMrshow.pic=038.gif

close.Type=TMenuItem
close.Text=�˳�
close.zhText=�˳�
close.enText=Quit
close.Tip=�˳�
close.zhTip=�˳�
close.enTip=Quit
close.M=X
close.key=Alt+F4
close.Action=onClosePanel
close.pic=close.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

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

merge.Type=TMenuItem
merge.Text=�����ֺϲ�
merge.Tip=�����ֺϲ�
merge.M=M
merge.key=
merge.Action=onMerge
merge.pic=sta-1.gif

singledise.Type=TMenuItem
singledise.Text=������׼��
singledise.Tip=������׼��
singledise.M=S
singledise.Action=onSingleDise
singledise.pic=emr-1.gif

cdss.Type=TMenuItem
cdss.Text=���ܼ�������
cdss.Tip=���ܼ�������
cdss.M=c
cdss.Action=onCdssCal
cdss.pic=warm-3.gif

showpat.Type=TMenuItem
showpat.Text=���ξ���
showpat.zhText=���ξ���
showpat.enText=Pat Info
showpat.Tip=CDR
showpat.zhTip=���ξ���
showpat.enTip=Pat Info
showpat.M=P
showpat.key=Ctrl+P
showpat.Action=onQuerySummaryInfo
showpat.pic=patlist.gif

medApplyNo.Type=TMenuItem
medApplyNo.Text=��������
medApplyNo.Tip=��ӡ����
medApplyNo.M=C
medApplyNo.Action=onMedApplyPrint
medApplyNo.pic=barCode.gif

charge.Type=TMenuItem
charge.Text=����Ʒ�
charge.Tip=����Ʒ�
charge.M=W
charge.Action=onCharge
charge.pic=modify.gif

clpOrder.Type=TMenuItem
clpOrder.Text=����ʱ���޸�
clpOrder.Tip=����ʱ���޸�
clpOrder.M=P
clpOrder.Action=onClpOrderReSchdCode
clpOrder.pic=modify.gif

batchModOrderDate.Type=TMenuItem
batchModOrderDate.Text=�����޸�����ʱ��
batchModOrderDate.Tip=�����޸�����ʱ��
batchModOrderDate.M=c
batchModOrderDate.Action=onBatchModOrderDate
batchModOrderDate.pic=change.gif

communicate.Type=TMenuItem
communicate.Text=��ͨ
communicate.Tip=��ͨ
communicate.M=F
communicate.key=F6
communicate.Action=onCommunicate
communicate.pic=AlignWidth.GIF