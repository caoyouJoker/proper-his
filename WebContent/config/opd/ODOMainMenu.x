<Type=TMenuBar>
UI.Item=File;PRE;INS;ClinicSPC;Window;Package;Clinical;Report;Clp;Emr;Other
UI.button=save;|;tempsave;|;delete;|;clear;|;ekt;|;CallNumber;|;showpat;|;History;|;showPatDetail;|;mrshow;|;toTemplate;|;ClearMenu;|;searchFee;|;fee;|;onUploadPrescription;|;reg;|;regDetail;|;outReturn;|;emeHospital;|;cancelEmeHospital;|;onwOrder;|;close
 
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
File.Item=Refresh;close

ClinicSPC.Type=TMenu
ClinicSPC.Text=����
ClinicSPC.zhText=����
ClinicSPC.enText=mente
ClinicSPC.M=M
ClinicSPC.Item=insMTRegister;INSDrQuery

PRE.Type=TMenu
PRE.Text=ԤԼ
PRE.zhText=ԤԼ
PRE.enText=yuue
PRE.M=P
PRE.Item=reg;regDetail;AbnormalReg

INS.Type=TMenu
INS.Text=ҽ��
INS.zhText=ҽ��
INS.enText=File
INS.M=I
INS.Item=SpecialCase;query

Package.Type=TMenu
Package.Text=ģ���ײ�
Package.zhText=ģ���ײ�
Package.enText=Package
Package.M=P
Package.Item=deptPack;drPack;deptOrder;drOrder;deptEmr;drEmr;quoteSheet;applyForm;History;resonablemed

Clinical.Type=TMenu
Clinical.Text=�ٴ�ҵ��
Clinical.zhText=�ٴ�ҵ��
Clinical.enText=Clinical
Clinical.M=C
Clinical.Item=record;printOrderSheet;opeApply;bloodApply;diag;Observation;discharge;dcis

Report.Type=TMenu
Report.Text=����/���
Report.zhText=����/���
Report.enText=Report
Report.M=R
// add by wangqing 20180124 ����regFallAndPainReport���鿴�����������ʹ������
Report.Item=erdTriage;orderSheet;opeReport;orderList;temperRpt;nursingRecord;labReport;imageReport;eccReport;xtReport;contagionReport;getQiTaPDF;regFallAndPainReport

Emr.Type=TMenu
Emr.Text=EMR
Emr.zhText=EMR
Emr.enText=EMR
Emr.M=E
Emr.Item=showpat1;cxMrshow

Other.Type=TMenu
Other.Text=����
Other.zhText=����
Other.enText=Other
Other.M=O
Other.Item=expend;babm

//==============================================

deptPack.Type=TMenuItem
deptPack.Text=�������
deptPack.zhText=�������
deptPack.enText=deptPack
deptPack.Tip=�������
deptPack.zhTip=�������
deptPack.enTip=deptPack
deptPack.M=P
deptPack.Action=onDeptDiag
deptPack.pic=bill-3.gif

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

drPack.Type=TMenuItem
drPack.Text=ҽʦ���
drPack.zhText=ҽʦ���
drPack.enText=drPack
drPack.Tip=ҽʦ���
drPack.zhTip=ҽʦ���
drPack.enTip=drPack
drPack.M=K
drPack.Action=onDrDiag
drPack.pic=sta.gif

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

deptEmr.Type=TMenuItem
deptEmr.Text=����ģ��
deptEmr.zhText=����ģ��
deptEmr.enText=deptEmr
deptEmr.Tip=����ģ��
deptEmr.zhTip=����ģ��
deptEmr.enTip=applyPha
deptEmr.M=H
deptEmr.Action=onDeptPack
deptEmr.pic=027.gif

drEmr.Type=TMenuItem
drEmr.Text=ҽʦģ��
drEmr.zhText=ҽʦģ��
drEmr.enText=examList
drEmr.Tip=ҽʦģ��
drEmr.zhTip=ҽʦģ��
drEmr.enTip=examList
drEmr.M=X
drEmr.Action=onDrPack
drEmr.pic=detail-1.gif
    
quoteSheet.Type=TMenuItem
quoteSheet.Text=������
quoteSheet.zhText=������
quoteSheet.enText=quoteSheet
quoteSheet.Tip=������
quoteSheet.zhTip=������
quoteSheet.enTip=quoteSheet
quoteSheet.M=Q
quoteSheet.Action=onShowQuoteSheet
quoteSheet.pic=RIS-1.gif

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

//===================================================

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

printOrderSheet.Type=TMenuItem
printOrderSheet.Text=���򴦷�
printOrderSheet.zhText=���򴦷�
printOrderSheet.enText=printOrderSheet
printOrderSheet.Tip=���򴦷�
printOrderSheet.zhTip=���򴦷�
printOrderSheet.enTip=printOrderSheet
printOrderSheet.M=Q
printOrderSheet.Action=onCaseSheet
printOrderSheet.pic=Commit.gif

opeApply.Type=TMenuItem
opeApply.Text=��������
opeApply.zhText=��������
opeApply.enText=opeApply
opeApply.Tip=��������
opeApply.zhTip=��������
opeApply.enTip=opeApply
opeApply.M=SS
opeApply.Action=onOpApply
opeApply.pic=odidrimg.gif

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

diag.Type=TMenuItem
diag.Text=���֤��
diag.zhText=���֤��
diag.enText=bloodApply
diag.Tip=���֤��
diag.zhTip=���֤��
diag.enTip=diag
diag.M=BL
diag.Action=onDiag
diag.pic=026.gif

Observation.Type=TMenuItem
Observation.Text=��������
Observation.zhText=��������
Observation.enText=Observation
Observation.Tip=��������
Observation.zhTip=��������
Observation.enTip=Observation
Observation.M=OB
Observation.Action=onErd
Observation.pic=pharm.gif

dcis.Type=TMenuItem
dcis.Text=�����������
dcis.zhText=�����������
dcis.enText=DICS
dcis.Tip=�����������
dcis.zhTip=�����������
dcis.enTip=�����������
dcis.M=DC
dcis.Action=onERDCISQuery
dcis.pic=search-1.gif


discharge.Type=TMenuItem
discharge.Text=סԺ֪ͨ
discharge.zhText=סԺ֪ͨ
discharge.enText=discharge
discharge.Tip=סԺ֪ͨ
discharge.zhTip=סԺ֪ͨ
discharge.enTip=discharge
discharge.M=Q
discharge.Action=onPreDate
discharge.pic=044.gif

//===================================================


orderSheet.Type=TMenuItem
orderSheet.Text=���ﲡ��
orderSheet.zhText=���ﲡ��
orderSheet.enText=orderSheet
orderSheet.Tip=���ﲡ��
orderSheet.zhTip=���ﲡ��
orderSheet.enTip=orderSheet
orderSheet.M=O
orderSheet.Action=onPrintCase
orderSheet.pic=010.gif

opeReport.Type=TMenuItem
opeReport.Text=������¼
opeReport.zhText=������¼
opeReport.enText=opeReport
opeReport.Tip=������¼
opeReport.zhTip=������¼
opeReport.enTip=opeReport
opeReport.M=Q
opeReport.Action=onOpRecord
opeReport.pic=048.gif

temperRpt.Type=TMenuItem
temperRpt.Text=���µ�
temperRpt.zhText=���µ�
temperRpt.enText=opeReport
temperRpt.Tip=���µ�
temperRpt.zhTip=���µ�
opeReport.enTip=opeReport
temperRpt.M=TE
temperRpt.Action=onBodyTemp
temperRpt.pic=emr-1.gif

orderList.Type=TMenuItem
orderList.Text=ҽ����
orderList.zhText=ҽ����
orderList.enText=opeReport
orderList.Tip=ҽ����
orderList.zhTip=ҽ����
orderList.enTip=orderList
orderList.M=OR
orderList.Action=onOrderSheet
orderList.pic=emr-2.gif

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

labReport.Type=TMenuItem
labReport.Text=���鱨��
labReport.zhText=���鱨��
labReport.enText=labReport
labReport.Tip=���鱨��
labReport.zhTip=���鱨��
labReport.enTip=labReport
labReport.M=L
labReport.Action=onLisReport
labReport.pic=LIS.gif

imageReport.Type=TMenuItem
imageReport.Text=��鱨��
imageReport.zhText=��鱨��
imageReport.enText=imageReport
imageReport.Tip=��鱨��
imageReport.zhTip=��鱨��
imageReport.enTip=imageReport
imageReport.M=I
imageReport.Action=onRisReport
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
xtReport.Text=Ѫ�Ǳ���
xtReport.zhText=Ѫ�Ǳ���
xtReport.enText=xtReport
xtReport.Tip=Ѫ�Ǳ���
xtReport.zhTip=Ѫ�Ǳ���
xtReport.enTip=xtReport
xtReport.M=T
xtReport.Action=getXTReport
xtReport.pic=Retrieve.gif

contagionReport.Type=TMenuItem
contagionReport.Text=��������
contagionReport.zhText=��������
contagionReport.enText=contagionReport
contagionReport.Tip=��������
contagionReport.zhTip=��������
contagionReport.enTip=contagionReport
contagionReport.M=N
contagionReport.Action=onContagionReport
contagionReport.pic=013.gif

//===================================================
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

fee.Type=TMenuItem
fee.Text=�շ�
fee.zhText=�շ�
fee.enText=Fee
fee.Tip=�շ�
fee.zhTip=�շ�
fee.enTip=Fee
fee.M=F
fee.key=Ctrl+F
fee.Action=onFee
fee.pic=openbill-2.gif

searchFee.Type=TMenuItem
searchFee.Text=����
searchFee.zhText=����
searchFee.enText=searchFee
searchFee.Tip=����
searchFee.zhTip=����
searchFee.enTip=searchFee
searchFee.Action=onMrSearchFee
searchFee.pic=bill.gif

showpat.Type=TMenuItem
showpat.Text=����
showpat.zhText=����
showpat.enText=Pat Info
showpat.Tip=����
showpat.zhTip=����
showpat.enTip=Pat Info
showpat.M=P
showpat.key=Ctrl+P
showpat.Action=onPat
showpat.pic=patlist.gif

showPatDetail.Type=TMenuItem
showPatDetail.Text=��������
showPatDetail.zhText=��������
showPatDetail.enText=Pat Profile
showPatDetail.Tip=��������
showPatDetail.zhTip=��������
showPatDetail.enTip=Pat Profile
showPatDetail.M=
showPatDetail.key=
showPatDetail.Action=onPatDetail
showPatDetail.pic=pat.gif

tempsave.Type=TMenuItem
tempsave.Text=�ݴ�
tempsave.zhText=�ݴ�
tempsave.enText=Pending
tempsave.Tip=�ݴ�
tempsave.zhTip=�ݴ�
tempsave.enTip=Pending
tempsave.M=T
tempsave.key=Ctrl+T
tempsave.Action=onFee
tempsave.pic=tempsave.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.zhText=ɾ��
delete.enText=Delete
delete.Tip=ɾ��
delete.zhTip=ɾ��
delete.enTip=Delete
delete.M=N
delete.key=Delete
delete.Action=deleteRow
delete.pic=delete.gif

reportstatus.Type=TMenuItem
reportstatus.Text=����״̬
reportstatus.zhText=����״̬
reportstatus.enText=Report Status
reportstatus.Tip=����״̬
reportstatus.zhTip=����״̬
reportstatus.enTip=Report Status
reportstatus.M=B
reportstatus.key=Ctrl+B
reportstatus.Action=onReportStatus
reportstatus.pic=detail-1.gif

casehistory.Type=TMenuItem
casehistory.Text=����
casehistory.zhText=����
casehistory.enText=Medical Records
casehistory.Tip=����
casehistory.zhTip=����
casehistory.enTip=Medical Records
casehistory.M=C
casehistory.key=Ctrl+C
casehistory.Action=onCaseHistory
casehistory.pic=emr.gif

toTemplate.Type=TMenuItem
toTemplate.Text=��ģ��
toTemplate.zhText=��ģ��
toTemplate.enText=Save Tmpl
toTemplate.Tip=��ģ��
toTemplate.zhTip=��ģ��
toTemplate.enTip=Save Tmpl
toTemplate.M=
toTemplate.key=
toTemplate.Action=onSaveTemplate
toTemplate.pic=sta-1.gif

appointment.Type=TMenuItem
appointment.Text=ԤԼ
appointment.zhText=ԤԼ
appointment.enText=Appoint
appointment.Tip=ԤԼ
appointment.zhTip=ԤԼ
appointment.enTip=Appoint
appointment.M=A
appointment.key=Ctrl+A
appointment.Action=onAppointMent
appointment.pic=time.gif

mainline.Type=TMenuItem
mainline.Text=���㴲λ
mainline.Tip=���㴲λ
mainline.M=M
mainline.key=Ctrl+M
mainline.Action=onMainLine
mainline.pic=phl.gif

resonablemed.Type=TMenuItem
resonablemed.Text=������ҩ
resonablemed.Tip=������ҩ
resonablemed.M=Y
resonablemed.key=Ctrl+Y
resonablemed.Action=onResonablemed
resonablemed.pic=sta-4.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.enText=Refresh
Refresh.enTip=Refresh
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=���
clear.zhText=���
clear.enText=Empty
clear.Tip=���
clear.zhTip=���
clear.enTip=Empty
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

History.Type=TMenuItem
History.Text=�����¼
History.Tip=�����¼
History.zhTip=�����¼
History.M=C
History.Action=onCaseHistory
History.pic=032.gif

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

reg.Type=TMenuItem
reg.Text=ԤԼ
reg.zhText=ԤԼ
reg.enText=Appointment of registered
reg.Tip=ԤԼ�Һ�
reg.zhTip=ԤԼ�Һ�
reg.enTip=Appointment of registered
reg.Action=onReg
reg.pic=date.gif

regDetail.Type=TMenuItem
regDetail.Text=ԤԼ��ϸ
regDetail.zhText=ԤԼ��ϸ
regDetail.enText=Appointment of registered detail
regDetail.Tip=ԤԼ�Һ���ϸ
regDetail.zhTip=ԤԼ�Һ���ϸ
regDetail.enTip=Appointment of registered detail 
regDetail.Action=onRegDetail
regDetail.pic=search-2.gif


ekt.Type=TMenuItem
ekt.Text=����
ekt.zhText=����
ekt.enText=
ekt.Tip=ҽ�ƿ�����
ekt.zhTip=ҽ�ƿ�����
ekt.enTip=
ekt.Action=onEKT
ekt.pic=042.gif

mrshow.Type=TMenuItem
mrshow.Text=����
mrshow.Tip=����
mrshow.M=W
mrshow.key=Ctrl+W
mrshow.Action=onShow
mrshow.pic=012.gif

AbnormalReg.Type=TMenuItem
AbnormalReg.Text=�ǳ�̬
AbnormalReg.zhText=�ǳ�̬
AbnormalReg.enText=
AbnormalReg.Tip=
AbnormalReg.zhTip=�ǳ�̬����
AbnormalReg.enTip=
AbnormalReg.Action=onAbnormalReg
AbnormalReg.pic=nurse.gif

CallNumber.Type=TMenuItem
CallNumber.Text=�к�
CallNumber.zhText=�к�
CallNumber.enText=NextCall
CallNumber.Tip=��һ��
CallNumber.zhTip=��һ��
CallNumber.enTip=
CallNumber.Action=onNextCallNo
CallNumber.pic=044.gif

SpecialCase.Type=TMenuItem
SpecialCase.Text=ҽ���������
SpecialCase.zhText=ҽ���������
SpecialCase.enText=
SpecialCase.Tip=
SpecialCase.zhTip=ҽ���������
SpecialCase.enTip=
SpecialCase.Action=onSpecialCase
SpecialCase.pic=053.gif

INSDrQuery.Type=TMenuItem
INSDrQuery.Text=���ش���
INSDrQuery.zhText=���ش���
INSDrQuery.enText=INS Dr Query
INSDrQuery.Tip=���ش�����ѯ
INSDrQuery.zhTip=���ش�����ѯ
INSDrQuery.enTip=INS Dr Query
INSDrQuery.M=Y
INSDrQuery.Action=onINSDrQuery
INSDrQuery.Key=Ctrl+I
INSDrQuery.pic=search-1.gif

ClearMenu.Type=TMenuItem
ClearMenu.Text=�����
ClearMenu.Tip=�������
ClearMenu.M=v
ClearMenu.Action=onClearMenu
ClearMenu.Key=
ClearMenu.pic=001.gif

insMTRegister.Type=TMenuItem
insMTRegister.Text=���صǼ�
insMTRegister.zhText=���صǼ�
insMTRegister.enText=INS Dr Query
insMTRegister.Tip=���صǼ�
insMTRegister.zhTip=���صǼ�
insMTRegister.enTip=INS Dr Query
insMTRegister.M=Y
insMTRegister.Action=onMTRegister
insMTRegister.pic=exportword.gif

singledise.Type=TMenuItem
singledise.Text=������
singledise.Tip=������׼��
singledise.M=S
singledise.Action=onSingleDise
singledise.pic=emr-1.gif

query.Type=TMenuItem
query.Text=ҽ������
query.Tip=ҽ��������Ϣ��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onINSShareQuery
query.pic=query.gif

onUploadPrescription.Type=TMenuItem
onUploadPrescription.Text=�⹺
onUploadPrescription.Tip=�⹺�����ϴ�
onUploadPrescription.M=Q
onUploadPrescription.key=Ctrl+F
onUploadPrescription.Action=onINSPrescription
onUploadPrescription.pic=new.gif

cxMrshow.Type=TMenuItem
cxMrshow.Text=ʱ���Ს��
cxMrshow.Tip=ʱ���Ს��
cxMrshow.M=Q
cxMrshow.key=Ctrl+Q
cxMrshow.Action=onCxShow
cxMrshow.pic=038.gif

cisQuery.Type=TMenuItem
cisQuery.Text=����
cisQuery.Tip=�������ݲ鿴
cisQuery.M=I
cisQuery.key=Ctrl+I
cisQuery.Action=onERDCISQuery
cisQuery.pic=053.gif

showpat1.Type=TMenuItem
showpat1.Text=���ξ���
showpat1.zhText=���ξ���
showpat1.enText=Pat Info
showpat1.Tip=���ξ���
showpat1.zhTip=���ξ���
showpat1.enTip=Pat Info
showpat1.M=P
showpat1.key=Ctrl+P
showpat1.Action=onQuerySummaryInfo
showpat1.pic=patlist.gif

outReturn.Type=TMenuItem
outReturn.Text=��Ժ
outReturn.Tip=��Ժ
outReturn.M=R
outReturn.key=Ctrl+R
outReturn.Action=onOutReutrn
outReturn.pic=030.gif

emeHospital.Type=TMenuItem
emeHospital.Text=����סԺ
emeHospital.Tip=����סԺ
emeHospital.M=R
emeHospital.Action=onEmeHospital


cancelEmeHospital.Type=TMenuItem
cancelEmeHospital.Text=סԺȡ��
cancelEmeHospital.Tip=סԺȡ��
cancelEmeHospital.M=R
cancelEmeHospital.Action=onCancelEmeHospital



erdTriage.Type=TMenuItem
erdTriage.Text=��������
erdTriage.Tip=��������
erdTriage.M=
erdTriage.key=
erdTriage.Action=onErdTriage
erdTriage.pic=emr-2.gif

// add by wangqing 20171026 ��ͷҽ��
onwOrder.Type=TMenuItem
onwOrder.Text=��ͷҽ��
onwOrder.Tip=��ͷҽ��
onwOrder.M=
onwOrder.key=
onwOrder.Action=onOnwOrder
onwOrder.pic=emr-2.gif

// add by wangqing 20180124 ����regFallAndPainReport���鿴�����������ʹ������
regFallAndPainReport.Type=TMenuItem
regFallAndPainReport.Text=�����������ʹ������
regFallAndPainReport.Tip=�����������ʹ������
regFallAndPainReport.M=
regFallAndPainReport.key=
regFallAndPainReport.Action=onFallAndPainAssessment
regFallAndPainReport.pic=emr-2.gif


