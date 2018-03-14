<Type=TMenuBar>
UI.Item=File;Window;Package;Clinical;Report;Clp;Emr;Other
UI.button=save;query;clear;|;delTableRow;|;cxMrshow;|;medApplyNo;|;charge;|;cdss;|;batchModOrderDate;|;communicate;|;close

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
File.Item=save;query;clear;|;delTableRow;|;cxMrshow;|;medApplyNo;|;charge;|;close

Package.Type=TMenu
Package.Text=模板套餐
Package.zhText=模板套餐
Package.enText=Package
Package.M=P
Package.Item=deptOrder;drOrder;deptPack;drPack;clpPack;examList;applyForm;applyPha;infec;drugAlert

Clinical.Type=TMenu
Clinical.Text=临床业务
Clinical.zhText=临床业务
Clinical.enText=Clinical
Clinical.M=C
Clinical.Item=clpDiag;record;consApply;surgeryApply;bloodApply;getBloodApp;discharge;basy

Report.Type=TMenu
Report.Text=报告/结果
Report.zhText=报告/结果
Report.enText=Report
Report.M=R
Report.Item=orderSheet;vitalSign;assessReport;nursingRecord;labReport;imageReport;eccReport;getQiTaPDF;xtReport;bgReport;intensiveCare;militaryRecord

Clp.Type=TMenu
Clp.Text=临床路径
Clp.zhText=临床路径
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
Other.Text=其他
Other.zhText=其他
Other.enText=Other
Other.M=O
Other.Item=expend;babm

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

deptPack.Type=TMenuItem
deptPack.Text=科室套餐
deptPack.zhText=科室套餐
deptPack.enText=deptPack
deptPack.Tip=科室套餐
deptPack.zhTip=科室套餐
deptPack.enTip=deptPack
deptPack.M=P
deptPack.Action=onDeptPack
deptPack.pic=bill-3.gif

drPack.Type=TMenuItem
drPack.Text=医师套餐
drPack.zhText=医师套餐
drPack.enText=drPack
drPack.Tip=医师套餐
drPack.zhTip=医师套餐
drPack.enTip=drPack
drPack.M=K
drPack.Action=onDrPack
drPack.pic=sta.gif

clpPack.Type=TMenuItem
clpPack.Text=路径套餐
clpPack.zhText=路径套餐
clpPack.enText=clpPack
clpPack.Tip=路径套餐
clpPack.zhTip=路径套餐
clpPack.enTip=clpPack
clpPack.M=F
clpPack.Action=onClpPack
clpPack.pic=org.gif

examList.Type=TMenuItem
examList.Text=检验检查
examList.zhText=检验检查
examList.enText=examList
examList.Tip=检验检查
examList.zhTip=检验检查
examList.enTip=examList
examList.M=X
examList.Action=onInputList
examList.pic=RIS-1.gif

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

applyPha.Type=TMenuItem
applyPha.Text=抗菌药申请单
applyPha.zhText=抗菌药申请单
applyPha.enText=applyPha
applyPha.Tip=抗菌药申请单
applyPha.zhTip=抗菌药申请单
applyPha.enTip=applyPha
applyPha.M=H
applyPha.Action=onApplyListPha
applyPha.pic=027.gif

infec.Type=TMenuItem
infec.Text=抗菌药物
infec.zhText=抗菌药物
infec.enText=infec
infec.Tip=抗菌药物
infec.zhTip=抗菌药物
infec.enTip=infec
infec.M=Q
infec.Action=onInfecPack
infec.pic=051.gif

drugAlert.Type=TMenuItem
drugAlert.Text=合理用药
drugAlert.zhText=合理用药
drugAlert.enText=drugAlert
drugAlert.Tip=合理用药
drugAlert.zhTip=合理用药
drugAlert.enTip=drugAlert
drugAlert.M=Q
drugAlert.Action=onRational
drugAlert.pic=openbil-1.gif
//=========================

clpDiag.Type=TMenuItem
clpDiag.Text=临床诊断
clpDiag.zhText=临床诊断
clpDiag.enText=clpDiag
clpDiag.Tip=临床诊断
clpDiag.zhTip=临床诊断
clpDiag.enTip=clpDiag
clpDiag.M=clp
clpDiag.Action=onLcICD
clpDiag.pic=sta-1.gif

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

consApply.Type=TMenuItem
consApply.Text=会诊申请
consApply.zhText=会诊申请
consApply.enText=consApply
consApply.Tip=会诊申请
consApply.zhTip=会诊申请
consApply.enTip=consApply
consApply.M=Q
consApply.Action=onConsApply
consApply.pic=Commit.gif

surgeryApply.Type=TMenuItem
surgeryApply.Text=手术麻醉
surgeryApply.zhText=手术麻醉
surgeryApply.enText=surgeryApply
surgeryApply.Tip=手术麻醉
surgeryApply.zhTip=手术麻醉
surgeryApply.enTip=surgeryApply
surgeryApply.M=SS
surgeryApply.Action=onSSMZ
surgeryApply.pic=odidrimg.gif

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

getBloodApp.Type=TMenuItem
getBloodApp.Text=取血申请
getBloodApp.zhText=取血申请
getBloodApp.enText=getBloodApp
getBloodApp.Tip=取血申请
getBloodApp.zhTip=取血申请
getBloodApp.enTip=getBloodApp
getBloodApp.M=GBA
getBloodApp.Action=onQXResult
getBloodApp.pic=blood.gif

discharge.Type=TMenuItem
discharge.Text=出院通知
discharge.zhText=出院通知
discharge.enText=discharge
discharge.Tip=出院通知
discharge.zhTip=出院通知
discharge.enTip=discharge
discharge.M=Q
discharge.Action=onOutHosp
discharge.pic=044.gif

basy.Type=TMenuItem
basy.Text=病案编目
basy.zhText=病案编目
basy.enText=basy
basy.Tip=病案编目
basy.zhTip=病案编目
basy.enTip=basy
basy.M=Q
basy.Action=onBASY
basy.pic=037.gif
//==========================
orderSheet.Type=TMenuItem
orderSheet.Text=医嘱单
orderSheet.zhText=医嘱单
orderSheet.enText=orderSheet
orderSheet.Tip=医嘱单
orderSheet.zhTip=医嘱单
orderSheet.enTip=orderSheet
orderSheet.M=O
orderSheet.Action=onSelYZD
orderSheet.pic=010.gif



vitalSign.Type=TMenuItem
vitalSign.Text=体温单
vitalSign.zhText=体温单
vitalSign.enText=vitalSign
vitalSign.Tip=体温单
vitalSign.zhTip=体温单
vitalSign.enTip=vitalSign
vitalSign.M=V
vitalSign.Action=onSelTWD
vitalSign.pic=patlist.gif

assessReport.Type=TMenuItem
assessReport.Text=评估单
assessReport.zhText=评估单
assessReport.enText=assessReport
assessReport.Tip=评估单
assessReport.zhTip=评估单
assessReport.enTip=assessReport
assessReport.M=R
assessReport.Action=assessReport
assessReport.pic=Group.gif

nursingRecord.Type=TMenuItem
nursingRecord.Text=护理记录
nursingRecord.zhText=护理记录
nursingRecord.enText=nursingRecord
nursingRecord.Tip=护理记录
nursingRecord.zhTip=护理记录
nursingRecord.enTip=nursingRecord
nursingRecord.M=N
nursingRecord.Action=onNisFormList
nursingRecord.pic=nurse-1.gif

labReport.Type=TMenuItem
labReport.Text=检验报告
labReport.zhText=检验报告
labReport.enText=labReport
labReport.Tip=检验报告
labReport.zhTip=检验报告
labReport.enTip=labReport
labReport.M=L
labReport.Action=onLis
labReport.pic=LIS.gif

imageReport.Type=TMenuItem
imageReport.Text=检查报告
imageReport.zhText=检查报告
imageReport.enText=imageReport
imageReport.Tip=检查报告
imageReport.zhTip=检查报告
imageReport.enTip=imageReport
imageReport.M=I
imageReport.Action=onRis
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

xtReport.Type=TMenuItem
xtReport.Text=血糖报告(NOVA)
xtReport.zhText=血糖报告(NOVA)
xtReport.enText=xtReport
xtReport.Tip=血糖报告(NOVA)
xtReport.zhTip=血糖报告(NOVA)
xtReport.enTip=xtReport
xtReport.M=T
xtReport.Action=getXTReport
xtReport.pic=Retrieve.gif

bgReport.Type=TMenuItem
bgReport.Text=血糖报告(强生)
bgReport.zhText=血糖报告(强生)
bgReport.enText=bgReport
bgReport.Tip=血糖报告(强生)
bgReport.zhTip=血糖报告(强生)
bgReport.enTip=bgReport
bgReport.M=J
bgReport.Action=getBgReport
bgReport.pic=Retrieve.gif

intensiveCare.Type=TMenuItem
intensiveCare.Text=重症监护
intensiveCare.zhText=重症监护
intensiveCare.enText=intensiveCare
intensiveCare.Tip=重症监护
intensiveCare.zhTip=重症监护
intensiveCare.enTip=intensiveCare
intensiveCare.M=N
intensiveCare.Action=getCCEmrData
intensiveCare.pic=013.gif

militaryRecord.Type=TMenuItem
militaryRecord.Text=手麻病历
militaryRecord.zhText=手麻病历
militaryRecord.enText=militaryRecord
militaryRecord.Tip=手麻病历
militaryRecord.zhTip=手麻病历
militaryRecord.enTip=militaryRecord
militaryRecord.M=Q
militaryRecord.Action=getOpeMrData
militaryRecord.pic=048.gif


//==========================
clpMain.Type=TMenuItem
clpMain.Text=路径准入
clpMain.zhText=路径准入
clpMain.enText=clpMain
clpMain.Tip=路径准入
clpMain.zhTip=路径准入
clpMain.enTip=clpMain
clpMain.M=M
clpMain.Action=onClpManageM
clpMain.pic=Arrow.gif

addPath.Type=TMenuItem
addPath.Text=引入路径
addPath.zhText=引入路径
addPath.enText=addPath
addPath.Tip=引入路径
addPath.zhTip=引入路径
addPath.enTip=addPath
addPath.M=D
addPath.Action=onAddCLNCPath
addPath.pic=convert.gif

duration.Type=TMenuItem
duration.Text=路径时程
duration.zhText=路径时程
duration.enText=duration
duration.Tip=路径时程
duration.zhTip=路径时程
duration.enTip=duration
duration.M=DU
duration.Action=intoDuration
duration.pic=Preview1.gif

clpVariation.Type=TMenuItem
clpVariation.Text=变异分析
clpVariation.zhText=变异分析
clpVariation.enText=drugAlert
clpVariation.Tip=变异分析
clpVariation.zhTip=变异分析
clpVariation.enTip=drugAlert
clpVariation.M=Q
clpVariation.Action=onClpVariation
clpVariation.pic=search-2.gif
//==========================
opRecord.Type=TMenuItem
opRecord.Text=门急诊病历
opRecord.zhText=门急诊病历
opRecord.enText=opRecord
opRecord.Tip=门急诊病历
opRecord.zhTip=门急诊病历
opRecord.enTip=opRecord
opRecord.M=Q
opRecord.Action=onOpdBL
opRecord.pic=spreadout.gif


//============================
expend.Type=TMenuItem
expend.Text=费用查询
expend.zhText=费用查询
expend.enText=expend
expend.Tip=费用查询
expend.zhTip=费用查询
expend.enTip=expend
expend.M=E
expend.Action=onSelIbs
expend.pic=fee.gif

babm.Type=TMenuItem
babm.Text=病历审查
babm.zhText=病历审查
babm.enText=babm
babm.Tip=病历审查
babm.zhTip=病历审查
babm.enTip=babm
babm.M=Q
babm.Action=onBABM
babm.pic=034.gif
//================================
query.Type=TMenuItem
query.Text=查询
query.zhText=查询
query.enText=Query
query.Tip=查询
query.zhTip=查询
query.enTip=Query
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

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

clear.Type=TMenuItem
clear.Text=清空
clear.zhText=清空
clear.enText=Clear
clear.Tip=清空
clear.zhTip=清空
clear.enTip=Clear
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

delTableRow.Type=TMenuItem
delTableRow.Text=删除医嘱
delTableRow.zhText=删除医嘱
delTableRow.enText=Delete
delTableRow.Tip=删除医嘱
delTableRow.zhTip=删除医嘱
delTableRow.enTip=Delete
delTableRow.M=D
delTableRow.Action=onDelRow
delTableRow.pic=delete.gif

mrshow.Type=TMenuItem
mrshow.Text=病历浏览
mrshow.Tip=病历浏览(Ctrl+W)
mrshow.M=W
mrshow.key=Ctrl+W
mrshow.Action=onShow
mrshow.pic=012.gif

cxMrshow.Type=TMenuItem
cxMrshow.Text=时间轴病历
cxMrshow.Tip=时间轴病历(Ctrl+Q)
cxMrshow.M=Q
cxMrshow.key=Ctrl+Q
cxMrshow.Action=onCxShow
cxMrshow.pic=038.gif

close.Type=TMenuItem
close.Text=退出
close.zhText=退出
close.enText=Quit
close.Tip=退出
close.zhTip=退出
close.enTip=Quit
close.M=X
close.key=Alt+F4
close.Action=onClosePanel
close.pic=close.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

pdf.Type=TMenuItem
pdf.Text=病历整合
pdf.zhText=病历整合
pdf.enText=病历整合
pdf.Tip=病历整合
pdf.zhTip=病历整合
pdf.enTip=病历整合
pdf.M=X
pdf.Action=onSubmitPDF
pdf.pic=005.gif

merge.Type=TMenuItem
merge.Text=单病种合并
merge.Tip=单病种合并
merge.M=M
merge.key=
merge.Action=onMerge
merge.pic=sta-1.gif

singledise.Type=TMenuItem
singledise.Text=单病种准入
singledise.Tip=单病种准入
singledise.M=S
singledise.Action=onSingleDise
singledise.pic=emr-1.gif

cdss.Type=TMenuItem
cdss.Text=智能剂量引入
cdss.Tip=智能剂量引入
cdss.M=c
cdss.Action=onCdssCal
cdss.pic=warm-3.gif

showpat.Type=TMenuItem
showpat.Text=历次就诊
showpat.zhText=历次就诊
showpat.enText=Pat Info
showpat.Tip=CDR
showpat.zhTip=历次就诊
showpat.enTip=Pat Info
showpat.M=P
showpat.key=Ctrl+P
showpat.Action=onQuerySummaryInfo
showpat.pic=patlist.gif

medApplyNo.Type=TMenuItem
medApplyNo.Text=检验条码
medApplyNo.Tip=打印条码
medApplyNo.M=C
medApplyNo.Action=onMedApplyPrint
medApplyNo.pic=barCode.gif

charge.Type=TMenuItem
charge.Text=补充计费
charge.Tip=补充计费
charge.M=W
charge.Action=onCharge
charge.pic=modify.gif

clpOrder.Type=TMenuItem
clpOrder.Text=费用时程修改
clpOrder.Tip=费用时程修改
clpOrder.M=P
clpOrder.Action=onClpOrderReSchdCode
clpOrder.pic=modify.gif

batchModOrderDate.Type=TMenuItem
batchModOrderDate.Text=批量修改启用时间
batchModOrderDate.Tip=批量修改启用时间
batchModOrderDate.M=c
batchModOrderDate.Action=onBatchModOrderDate
batchModOrderDate.pic=change.gif

communicate.Type=TMenuItem
communicate.Text=沟通
communicate.Tip=沟通
communicate.M=F
communicate.key=F6
communicate.Action=onCommunicate
communicate.pic=AlignWidth.GIF