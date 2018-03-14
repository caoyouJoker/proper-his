<Type=TMenuBar>
UI.Item=File;PRE;INS;ClinicSPC;Window;Package;Clinical;Report;Clp;Emr;Other
UI.button=save;|;tempsave;|;delete;|;clear;|;ekt;|;CallNumber;|;showpat;|;History;|;showPatDetail;|;mrshow;|;toTemplate;|;ClearMenu;|;searchFee;|;fee;|;onUploadPrescription;|;reg;|;regDetail;|;outReturn;|;emeHospital;|;cancelEmeHospital;|;onwOrder;|;close
 
Window.Type=TMenu
Window.Text=窗口
Window.zhText=窗口
Window.enText=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.zhText=文件
File.enText=File
File.M=F
File.Item=Refresh;close

ClinicSPC.Type=TMenu
ClinicSPC.Text=门特
ClinicSPC.zhText=门特
ClinicSPC.enText=mente
ClinicSPC.M=M
ClinicSPC.Item=insMTRegister;INSDrQuery

PRE.Type=TMenu
PRE.Text=预约
PRE.zhText=预约
PRE.enText=yuue
PRE.M=P
PRE.Item=reg;regDetail;AbnormalReg

INS.Type=TMenu
INS.Text=医保
INS.zhText=医保
INS.enText=File
INS.M=I
INS.Item=SpecialCase;query

Package.Type=TMenu
Package.Text=模板套餐
Package.zhText=模板套餐
Package.enText=Package
Package.M=P
Package.Item=deptPack;drPack;deptOrder;drOrder;deptEmr;drEmr;quoteSheet;applyForm;History;resonablemed

Clinical.Type=TMenu
Clinical.Text=临床业务
Clinical.zhText=临床业务
Clinical.enText=Clinical
Clinical.M=C
Clinical.Item=record;printOrderSheet;opeApply;bloodApply;diag;Observation;discharge;dcis

Report.Type=TMenu
Report.Text=报告/结果
Report.zhText=报告/结果
Report.enText=Report
Report.M=R
// add by wangqing 20180124 新增regFallAndPainReport，查看急诊跌倒、疼痛评估表
Report.Item=erdTriage;orderSheet;opeReport;orderList;temperRpt;nursingRecord;labReport;imageReport;eccReport;xtReport;contagionReport;getQiTaPDF;regFallAndPainReport

Emr.Type=TMenu
Emr.Text=EMR
Emr.zhText=EMR
Emr.enText=EMR
Emr.M=E
Emr.Item=showpat1;cxMrshow

Other.Type=TMenu
Other.Text=其他
Other.zhText=其他
Other.enText=Other
Other.M=O
Other.Item=expend;babm

//==============================================

deptPack.Type=TMenuItem
deptPack.Text=科室诊断
deptPack.zhText=科室诊断
deptPack.enText=deptPack
deptPack.Tip=科室诊断
deptPack.zhTip=科室诊断
deptPack.enTip=deptPack
deptPack.M=P
deptPack.Action=onDeptDiag
deptPack.pic=bill-3.gif

getQiTaPDF.Type=TMenuItem
getQiTaPDF.Text=其他报告
getQiTaPDF.zhText=其他报告
getQiTaPDF.enText=getQiTaPDF
getQiTaPDF.Tip=其他报告
getQiTaPDF.zhTip=其他报告
getQiTaPDF.enTip=getQiTaPDF
getQiTaPDF.M=C
getQiTaPDF.Action=getPDFQiTa
getQiTaPDF.pic=PicData01.jpg

drPack.Type=TMenuItem
drPack.Text=医师诊断
drPack.zhText=医师诊断
drPack.enText=drPack
drPack.Tip=医师诊断
drPack.zhTip=医师诊断
drPack.enTip=drPack
drPack.M=K
drPack.Action=onDrDiag
drPack.pic=sta.gif

deptOrder.Type=TMenuItem
deptOrder.Text=科室医嘱
deptOrder.zhText=科室医嘱
deptOrder.enText=DeptOrder
deptOrder.Tip=科室医嘱
deptOrder.zhTip=科室医嘱
deptOrder.enTip=DeptOrder
deptOrder.M=D
deptOrder.Action=onDeptOrder
deptOrder.pic=inscon.gif

drOrder.Type=TMenuItem
drOrder.Text=医师医嘱
drOrder.zhText=医师医嘱
drOrder.enText=drOrder
drOrder.Tip=医师医嘱
drOrder.zhTip=医师医嘱
drOrder.enTip=drOrder
drOrder.M=Y
drOrder.Action=onDrOrder
drOrder.pic=Right.gif

deptEmr.Type=TMenuItem
deptEmr.Text=科室模板
deptEmr.zhText=科室模板
deptEmr.enText=deptEmr
deptEmr.Tip=科室模板
deptEmr.zhTip=科室模板
deptEmr.enTip=applyPha
deptEmr.M=H
deptEmr.Action=onDeptPack
deptEmr.pic=027.gif

drEmr.Type=TMenuItem
drEmr.Text=医师模板
drEmr.zhText=医师模板
drEmr.enText=examList
drEmr.Tip=医师模板
drEmr.zhTip=医师模板
drEmr.enTip=examList
drEmr.M=X
drEmr.Action=onDrPack
drEmr.pic=detail-1.gif
    
quoteSheet.Type=TMenuItem
quoteSheet.Text=检验检查
quoteSheet.zhText=检验检查
quoteSheet.enText=quoteSheet
quoteSheet.Tip=检验检查
quoteSheet.zhTip=检验检查
quoteSheet.enTip=quoteSheet
quoteSheet.M=Q
quoteSheet.Action=onShowQuoteSheet
quoteSheet.pic=RIS-1.gif

applyForm.Type=TMenuItem
applyForm.Text=申请单
applyForm.zhText=申请单
applyForm.enText=applyForm
applyForm.Tip=申请单
applyForm.zhTip=申请单
applyForm.enTip=applyForm
applyForm.M=A
applyForm.Action=onApplyList
applyForm.pic=RIS.gif

//===================================================

record.Type=TMenuItem
record.Text=病历书写
record.zhText=病历书写
record.enText=record
record.Tip=病历书写
record.zhTip=病历书写
record.enTip=record
record.M=RE
record.Action=onEmrWrite
record.pic=emr.gif

printOrderSheet.Type=TMenuItem
printOrderSheet.Text=补打处方
printOrderSheet.zhText=补打处方
printOrderSheet.enText=printOrderSheet
printOrderSheet.Tip=补打处方
printOrderSheet.zhTip=补打处方
printOrderSheet.enTip=printOrderSheet
printOrderSheet.M=Q
printOrderSheet.Action=onCaseSheet
printOrderSheet.pic=Commit.gif

opeApply.Type=TMenuItem
opeApply.Text=手术申请
opeApply.zhText=手术申请
opeApply.enText=opeApply
opeApply.Tip=手术申请
opeApply.zhTip=手术申请
opeApply.enTip=opeApply
opeApply.M=SS
opeApply.Action=onOpApply
opeApply.pic=odidrimg.gif

bloodApply.Type=TMenuItem
bloodApply.Text=备血申请
bloodApply.zhText=备血申请
bloodApply.enText=bloodApply
bloodApply.Tip=备血申请
bloodApply.zhTip=备血申请
bloodApply.enTip=bloodApply
bloodApply.M=BL
bloodApply.Action=onBXResult
bloodApply.pic=blood.gif

diag.Type=TMenuItem
diag.Text=诊断证明
diag.zhText=诊断证明
diag.enText=bloodApply
diag.Tip=诊断证明
diag.zhTip=诊断证明
diag.enTip=diag
diag.M=BL
diag.Action=onDiag
diag.pic=026.gif

Observation.Type=TMenuItem
Observation.Text=急诊抢救
Observation.zhText=急诊抢救
Observation.enText=Observation
Observation.Tip=急诊抢救
Observation.zhTip=急诊抢救
Observation.enTip=Observation
Observation.M=OB
Observation.Action=onErd
Observation.pic=pharm.gif

dcis.Type=TMenuItem
dcis.Text=急诊体征检测
dcis.zhText=急诊体征检测
dcis.enText=DICS
dcis.Tip=急诊体征检测
dcis.zhTip=急诊体征检测
dcis.enTip=急诊体征检测
dcis.M=DC
dcis.Action=onERDCISQuery
dcis.pic=search-1.gif


discharge.Type=TMenuItem
discharge.Text=住院通知
discharge.zhText=住院通知
discharge.enText=discharge
discharge.Tip=住院通知
discharge.zhTip=住院通知
discharge.enTip=discharge
discharge.M=Q
discharge.Action=onPreDate
discharge.pic=044.gif

//===================================================


orderSheet.Type=TMenuItem
orderSheet.Text=门诊病历
orderSheet.zhText=门诊病历
orderSheet.enText=orderSheet
orderSheet.Tip=门诊病历
orderSheet.zhTip=门诊病历
orderSheet.enTip=orderSheet
orderSheet.M=O
orderSheet.Action=onPrintCase
orderSheet.pic=010.gif

opeReport.Type=TMenuItem
opeReport.Text=手术记录
opeReport.zhText=手术记录
opeReport.enText=opeReport
opeReport.Tip=手术记录
opeReport.zhTip=手术记录
opeReport.enTip=opeReport
opeReport.M=Q
opeReport.Action=onOpRecord
opeReport.pic=048.gif

temperRpt.Type=TMenuItem
temperRpt.Text=体温单
temperRpt.zhText=体温单
temperRpt.enText=opeReport
temperRpt.Tip=体温单
temperRpt.zhTip=体温单
opeReport.enTip=opeReport
temperRpt.M=TE
temperRpt.Action=onBodyTemp
temperRpt.pic=emr-1.gif

orderList.Type=TMenuItem
orderList.Text=医嘱单
orderList.zhText=医嘱单
orderList.enText=opeReport
orderList.Tip=医嘱单
orderList.zhTip=医嘱单
orderList.enTip=orderList
orderList.M=OR
orderList.Action=onOrderSheet
orderList.pic=emr-2.gif

nursingRecord.Type=TMenuItem
nursingRecord.Text=护理记录
nursingRecord.zhText=护理记录
nursingRecord.enText=nursingRecord
nursingRecord.Tip=护理记录
nursingRecord.zhTip=护理记录
nursingRecord.enTip=nursingRecord
nursingRecord.M=N
nursingRecord.Action=onHLSel
nursingRecord.pic=nurse-1.gif

labReport.Type=TMenuItem
labReport.Text=检验报告
labReport.zhText=检验报告
labReport.enText=labReport
labReport.Tip=检验报告
labReport.zhTip=检验报告
labReport.enTip=labReport
labReport.M=L
labReport.Action=onLisReport
labReport.pic=LIS.gif

imageReport.Type=TMenuItem
imageReport.Text=检查报告
imageReport.zhText=检查报告
imageReport.enText=imageReport
imageReport.Tip=检查报告
imageReport.zhTip=检查报告
imageReport.enTip=imageReport
imageReport.M=I
imageReport.Action=onRisReport
imageReport.pic=RIS.gif

eccReport.Type=TMenuItem
eccReport.Text=心电报告
eccReport.zhText=心电报告
eccReport.enText=eccReport
eccReport.Tip=心电报告
eccReport.zhTip=心电报告
eccReport.enTip=eccReport
eccReport.M=C
eccReport.Action=getPdfReport
eccReport.pic=PicData01.jpg

xtReport.Type=TMenuItem
xtReport.Text=血糖报告
xtReport.zhText=血糖报告
xtReport.enText=xtReport
xtReport.Tip=血糖报告
xtReport.zhTip=血糖报告
xtReport.enTip=xtReport
xtReport.M=T
xtReport.Action=getXTReport
xtReport.pic=Retrieve.gif

contagionReport.Type=TMenuItem
contagionReport.Text=疾病报告
contagionReport.zhText=疾病报告
contagionReport.enText=contagionReport
contagionReport.Tip=疾病报告
contagionReport.zhTip=疾病报告
contagionReport.enTip=contagionReport
contagionReport.M=N
contagionReport.Action=onContagionReport
contagionReport.pic=013.gif

//===================================================
save.Type=TMenuItem
save.Text=保存
save.zhText=保存
save.enText=Save
save.Tip=保存
save.zhTip=保存
save.enTip=Save
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

fee.Type=TMenuItem
fee.Text=收费
fee.zhText=收费
fee.enText=Fee
fee.Tip=收费
fee.zhTip=收费
fee.enTip=Fee
fee.M=F
fee.key=Ctrl+F
fee.Action=onFee
fee.pic=openbill-2.gif

searchFee.Type=TMenuItem
searchFee.Text=试算
searchFee.zhText=试算
searchFee.enText=searchFee
searchFee.Tip=试算
searchFee.zhTip=试算
searchFee.enTip=searchFee
searchFee.Action=onMrSearchFee
searchFee.pic=bill.gif

showpat.Type=TMenuItem
showpat.Text=病患
showpat.zhText=病患
showpat.enText=Pat Info
showpat.Tip=病患
showpat.zhTip=病患
showpat.enTip=Pat Info
showpat.M=P
showpat.key=Ctrl+P
showpat.Action=onPat
showpat.pic=patlist.gif

showPatDetail.Type=TMenuItem
showPatDetail.Text=病患详情
showPatDetail.zhText=病患详情
showPatDetail.enText=Pat Profile
showPatDetail.Tip=病患详情
showPatDetail.zhTip=病患详情
showPatDetail.enTip=Pat Profile
showPatDetail.M=
showPatDetail.key=
showPatDetail.Action=onPatDetail
showPatDetail.pic=pat.gif

tempsave.Type=TMenuItem
tempsave.Text=暂存
tempsave.zhText=暂存
tempsave.enText=Pending
tempsave.Tip=暂存
tempsave.zhTip=暂存
tempsave.enTip=Pending
tempsave.M=T
tempsave.key=Ctrl+T
tempsave.Action=onFee
tempsave.pic=tempsave.gif

delete.Type=TMenuItem
delete.Text=删除
delete.zhText=删除
delete.enText=Delete
delete.Tip=删除
delete.zhTip=删除
delete.enTip=Delete
delete.M=N
delete.key=Delete
delete.Action=deleteRow
delete.pic=delete.gif

reportstatus.Type=TMenuItem
reportstatus.Text=报告状态
reportstatus.zhText=报告状态
reportstatus.enText=Report Status
reportstatus.Tip=报告状态
reportstatus.zhTip=报告状态
reportstatus.enTip=Report Status
reportstatus.M=B
reportstatus.key=Ctrl+B
reportstatus.Action=onReportStatus
reportstatus.pic=detail-1.gif

casehistory.Type=TMenuItem
casehistory.Text=病历
casehistory.zhText=病历
casehistory.enText=Medical Records
casehistory.Tip=病历
casehistory.zhTip=病历
casehistory.enTip=Medical Records
casehistory.M=C
casehistory.key=Ctrl+C
casehistory.Action=onCaseHistory
casehistory.pic=emr.gif

toTemplate.Type=TMenuItem
toTemplate.Text=存模板
toTemplate.zhText=存模板
toTemplate.enText=Save Tmpl
toTemplate.Tip=存模板
toTemplate.zhTip=存模板
toTemplate.enTip=Save Tmpl
toTemplate.M=
toTemplate.key=
toTemplate.Action=onSaveTemplate
toTemplate.pic=sta-1.gif

appointment.Type=TMenuItem
appointment.Text=预约
appointment.zhText=预约
appointment.enText=Appoint
appointment.Tip=预约
appointment.zhTip=预约
appointment.enTip=Appoint
appointment.M=A
appointment.key=Ctrl+A
appointment.Action=onAppointMent
appointment.pic=time.gif

mainline.Type=TMenuItem
mainline.Text=静点床位
mainline.Tip=静点床位
mainline.M=M
mainline.key=Ctrl+M
mainline.Action=onMainLine
mainline.pic=phl.gif

resonablemed.Type=TMenuItem
resonablemed.Text=合理用药
resonablemed.Tip=合理用药
resonablemed.M=Y
resonablemed.key=Ctrl+Y
resonablemed.Action=onResonablemed
resonablemed.pic=sta-4.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.enText=Refresh
Refresh.enTip=Refresh
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=清空
clear.zhText=清空
clear.enText=Empty
clear.Tip=清空
clear.zhTip=清空
clear.enTip=Empty
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

History.Type=TMenuItem
History.Text=就诊记录
History.Tip=就诊记录
History.zhTip=就诊记录
History.M=C
History.Action=onCaseHistory
History.pic=032.gif

close.Type=TMenuItem
close.Text=退出
close.zhText=退出
close.enText=Quit
close.Tip=退出
close.zhTip=退出
close.enTip=Quit
close.M=X 
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

reg.Type=TMenuItem
reg.Text=预约
reg.zhText=预约
reg.enText=Appointment of registered
reg.Tip=预约挂号
reg.zhTip=预约挂号
reg.enTip=Appointment of registered
reg.Action=onReg
reg.pic=date.gif

regDetail.Type=TMenuItem
regDetail.Text=预约明细
regDetail.zhText=预约明细
regDetail.enText=Appointment of registered detail
regDetail.Tip=预约挂号明细
regDetail.zhTip=预约挂号明细
regDetail.enTip=Appointment of registered detail 
regDetail.Action=onRegDetail
regDetail.pic=search-2.gif


ekt.Type=TMenuItem
ekt.Text=读卡
ekt.zhText=读卡
ekt.enText=
ekt.Tip=医疗卡读卡
ekt.zhTip=医疗卡读卡
ekt.enTip=
ekt.Action=onEKT
ekt.pic=042.gif

mrshow.Type=TMenuItem
mrshow.Text=病历
mrshow.Tip=病历
mrshow.M=W
mrshow.key=Ctrl+W
mrshow.Action=onShow
mrshow.pic=012.gif

AbnormalReg.Type=TMenuItem
AbnormalReg.Text=非常态
AbnormalReg.zhText=非常态
AbnormalReg.enText=
AbnormalReg.Tip=
AbnormalReg.zhTip=非常态门诊
AbnormalReg.enTip=
AbnormalReg.Action=onAbnormalReg
AbnormalReg.pic=nurse.gif

CallNumber.Type=TMenuItem
CallNumber.Text=叫号
CallNumber.zhText=叫号
CallNumber.enText=NextCall
CallNumber.Tip=下一个
CallNumber.zhTip=下一个
CallNumber.enTip=
CallNumber.Action=onNextCallNo
CallNumber.pic=044.gif

SpecialCase.Type=TMenuItem
SpecialCase.Text=医保特殊情况
SpecialCase.zhText=医保特殊情况
SpecialCase.enText=
SpecialCase.Tip=
SpecialCase.zhTip=医保特殊情况
SpecialCase.enTip=
SpecialCase.Action=onSpecialCase
SpecialCase.pic=053.gif

INSDrQuery.Type=TMenuItem
INSDrQuery.Text=门特处方
INSDrQuery.zhText=门特处方
INSDrQuery.enText=INS Dr Query
INSDrQuery.Tip=门特处方查询
INSDrQuery.zhTip=门特处方查询
INSDrQuery.enTip=INS Dr Query
INSDrQuery.M=Y
INSDrQuery.Action=onINSDrQuery
INSDrQuery.Key=Ctrl+I
INSDrQuery.pic=search-1.gif

ClearMenu.Type=TMenuItem
ClearMenu.Text=清剪贴
ClearMenu.Tip=清剪贴板
ClearMenu.M=v
ClearMenu.Action=onClearMenu
ClearMenu.Key=
ClearMenu.pic=001.gif

insMTRegister.Type=TMenuItem
insMTRegister.Text=门特登记
insMTRegister.zhText=门特登记
insMTRegister.enText=INS Dr Query
insMTRegister.Tip=门特登记
insMTRegister.zhTip=门特登记
insMTRegister.enTip=INS Dr Query
insMTRegister.M=Y
insMTRegister.Action=onMTRegister
insMTRegister.pic=exportword.gif

singledise.Type=TMenuItem
singledise.Text=单病种
singledise.Tip=单病种准入
singledise.M=S
singledise.Action=onSingleDise
singledise.pic=emr-1.gif

query.Type=TMenuItem
query.Text=医保共享
query.Tip=医保共享信息查询
query.M=Q
query.key=Ctrl+F
query.Action=onINSShareQuery
query.pic=query.gif

onUploadPrescription.Type=TMenuItem
onUploadPrescription.Text=外购
onUploadPrescription.Tip=外购处方上传
onUploadPrescription.M=Q
onUploadPrescription.key=Ctrl+F
onUploadPrescription.Action=onINSPrescription
onUploadPrescription.pic=new.gif

cxMrshow.Type=TMenuItem
cxMrshow.Text=时间轴病历
cxMrshow.Tip=时间轴病历
cxMrshow.M=Q
cxMrshow.key=Ctrl+Q
cxMrshow.Action=onCxShow
cxMrshow.pic=038.gif

cisQuery.Type=TMenuItem
cisQuery.Text=体征
cisQuery.Tip=体征数据查看
cisQuery.M=I
cisQuery.key=Ctrl+I
cisQuery.Action=onERDCISQuery
cisQuery.pic=053.gif

showpat1.Type=TMenuItem
showpat1.Text=历次就诊
showpat1.zhText=历次就诊
showpat1.enText=Pat Info
showpat1.Tip=历次就诊
showpat1.zhTip=历次就诊
showpat1.enTip=Pat Info
showpat1.M=P
showpat1.key=Ctrl+P
showpat1.Action=onQuerySummaryInfo
showpat1.pic=patlist.gif

outReturn.Type=TMenuItem
outReturn.Text=离院
outReturn.Tip=离院
outReturn.M=R
outReturn.key=Ctrl+R
outReturn.Action=onOutReutrn
outReturn.pic=030.gif

emeHospital.Type=TMenuItem
emeHospital.Text=紧急住院
emeHospital.Tip=紧急住院
emeHospital.M=R
emeHospital.Action=onEmeHospital


cancelEmeHospital.Type=TMenuItem
cancelEmeHospital.Text=住院取消
cancelEmeHospital.Tip=住院取消
cancelEmeHospital.M=R
cancelEmeHospital.Action=onCancelEmeHospital



erdTriage.Type=TMenuItem
erdTriage.Text=检伤评估
erdTriage.Tip=检伤评估
erdTriage.M=
erdTriage.key=
erdTriage.Action=onErdTriage
erdTriage.pic=emr-2.gif

// add by wangqing 20171026 口头医嘱
onwOrder.Type=TMenuItem
onwOrder.Text=口头医嘱
onwOrder.Tip=口头医嘱
onwOrder.M=
onwOrder.key=
onwOrder.Action=onOnwOrder
onwOrder.pic=emr-2.gif

// add by wangqing 20180124 新增regFallAndPainReport，查看急诊跌倒、疼痛评估表
regFallAndPainReport.Type=TMenuItem
regFallAndPainReport.Text=急诊跌倒、疼痛评估表
regFallAndPainReport.Tip=急诊跌倒、疼痛评估表
regFallAndPainReport.M=
regFallAndPainReport.key=
regFallAndPainReport.Action=onFallAndPainAssessment
regFallAndPainReport.pic=emr-2.gif


