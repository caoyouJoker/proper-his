<Type=TMenuBar>
UI.Item=File;Doctor;Nurse;Report;Window
UI.button=query;|;bedcard;|;onSign;|;erdTriage;|;card;|;cln;|;emr;bab;pdf;|;twd;|;assessReport;|;hos;|;reg;|;ibs;|;daysurgery;|;clear;|;communicate;|;unlock;|;close

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
File.Item=query;export;|;card;|;bedcard;|;daysurgery;|;clear;|;close

Doctor.Type=TMenu
Doctor.Text=医师工具
Doctor.zhText=医师工具
Doctor.enText=医师工具
Doctor.M=D
Doctor.Item=cxMrshow;|;showpat;|;cln;|;emr;bab;opd;hrm;|;bas;|;smz;res;|;res1;|;hos;|;reg;pathology

Nurse.Type=TMenu
Nurse.Text=护士工具
Nurse.zhText=护士工具
Nurse.enText=护士工具
Nurse.M=N
Nurse.Item=sel;twd;hl;|;printLis;pdf;opeNursingRecord

Report.Type=TMenu
Report.Text=病患报告
Report.zhText=病患报告
Report.enText=PatReport
Report.M=R
Report.Item=lis;ris;tnb;bgReport;printLis


pathology.Type=TMenuItem
pathology.Text=病理类型
pathology.zhText=病理类型
pathology.enText=PathologyType
pathology.Tip=病理类型
pathology.zhTip=病理类型
pathology.enTip=PathologyType
pathology.Action=onPathologyType
pathology.pic=049.gif

onSign.Type=TMenuItem
onSign.Text=病患签名
onSign.zhText=病患签名
onSign.enText=onSign
onSign.Tip=病患签名
onSign.zhTip=病患签名
onSign.enTip=onSign
onSign.M=
onSign.Action=onSign
onSign.pic=clear.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.zhText=刷新
Refresh.enText=Refresh
Refresh.Tip=刷新
Refresh.zhTip=刷新
Refresh.enTip=Refresh
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

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

card.Type=TMenuItem
card.Text=床头卡
card.zhText=床头卡
card.enText=bed card
card.Tip=床头卡
card.zhTip=床头卡
card.enTip=bed card
card.M=B
card.Action=onBedCard
card.pic=card.gif

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

assessReport.Type=TMenuItem
assessReport.Text=评估单
assessReport.zhText=评估单
assessReport.enText=assessReport
assessReport.Tip=评估单
assessReport.zhTip=评估单
assessReport.enTip=assessReport
assessReport.M=R
assessReport.Action=onEvalutionRecordOpen
assessReport.pic=Group.gif

nis.Type=TMenuItem
nis.Text=护理表单
nis.zhText=护理表单
nis.enText=Form
nis.Tip=护理表单
nis.zhTip=护理表单
nis.enTip=Quit
nis.M=N
nis.key=Ctrl+N
nis.Action=onHLSel
nis.pic=Column.gif

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

bedcard.Type=TMenuItem
bedcard.Text=病患
bedcard.zhText=病患
bedcard.enText=Pat Info
bedcard.Tip=病患信息
bedcard.zhTip=病患信息
bedcard.enTip=Pat Info
bedcard.M=P
bedcard.Action=onPatInfo
bedcard.pic=bedcard.gif


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

cln.Type=TMenuItem
cln.Text=临床诊断
cln.zhText=临床诊断
cln.enText=临床诊断
cln.Tip=临床诊断
cln.zhTip=临床诊断
cln.enTip=临床诊断
cln.M=Q
cln.Action=onAddCLNCPath
cln.pic=009.gif

emr.Type=TMenuItem
emr.Text=写病历
emr.zhText=写病历
emr.enText=写病历
emr.Tip=写病历
emr.zhTip=写病历
emr.enTip=写病历
emr.M=S
emr.Action=onAddEmrWrite
emr.pic=emr-1.gif

bas.Type=TMenuItem
bas.Text=病案
bas.Tip=病案
bas.zhTip=病案编目
bas.enTip=病案编目
bas.M=A
bas.Action=onAddBASY
bas.pic=012.gif


bab.Type=TMenuItem
bab.Text=审病历
bab.zhText=审病历
bab.enText=审病历
bab.Tip=病历审查
bab.zhTip=病历审查
bab.enTip=病历审查
bab.M=S
bab.Action=onBABM
bab.pic=029.gif

sel.Type=TMenuItem
sel.Text=医嘱单
sel.zhText=医嘱单
sel.enText=医嘱单
sel.Tip=医嘱单
sel.zhTip=医嘱单
sel.enTip=医嘱单
sel.M=S
sel.Action=onSelYZD
sel.pic=017.gif

twd.Type=TMenuItem
twd.Text=体温单
twd.zhText=体温单
twd.enText=体温单
twd.Tip=体温单
twd.zhTip=体温单
twd.enTip=体温单
twd.M=S
twd.Action=onSelTWD
twd.pic=037.gif

hl.Type=TMenuItem
hl.Text=护理记录
hl.zhText=护理记录
hl.enText=护理记录
hl.Tip=护理记录
hl.zhTip=护理记录
hl.enTip=护理记录
hl.M=S
hl.Action=onNisFormList
hl.pic=048.gif

smz.Type=TMenuItem
smz.Text=手麻
smz.zhText=手麻
smz.enText=手麻
smz.Tip=手术麻醉
smz.zhTip=手术麻醉
smz.enTip=手术麻醉
smz.M=S
smz.Action=onSSMZ
smz.pic=051.gif

res.Type=TMenuItem
res.Text=备血
res.zhText=备血
res.enText=备血
res.Tip=备血
res.zhTip=备血
res.enTip=备血
res.M=S
res.Action=onBXResult
res.pic=blood.gif

res1.Type=TMenuItem
res1.Text=取血
res1.zhText=取血
res1.enText=取血
res1.Tip=取血
res1.zhTip=取血
res1.enTip=取血
res1.M=S
res1.Action=onQXResult
res1.pic=blood.gif


opd.Type=TMenuItem
opd.Text=门急病历
opd.zhText=门急病历
opd.enText=门急病历
opd.Tip=门急诊病历
opd.zhTip=门急诊病历
opd.enTip=门急诊病历
opd.M=S
opd.Action=onOpdBL
opd.pic=032.gif

lis.Type=TMenuItem
lis.Text=检验报告
lis.zhText=检验报告
lis.enText=检验报告
lis.Tip=检验报告
lis.zhTip=检验报告
lis.enTip=检验报告
lis.M=S
lis.Action=onLis
lis.pic=LIS.gif

ris.Type=TMenuItem
ris.Text=检查报告
ris.zhText=检查报告
ris.enText=检查报告
ris.Tip=检查报告
ris.zhTip=检查报告
ris.enTip=检查报告
ris.M=S
ris.Action=onRis
ris.pic=RIS.gif

hos.Type=TMenuItem
hos.Text=出院
hos.zhText=出院
hos.enText=出院
hos.Tip=出院通知
hos.zhTip=出院通知
hos.enTip=出院通知
hos.M=S
hos.Action=onOutHosp
hos.pic=015.gif

ibs.Type=TMenuItem
ibs.Text=费用
ibs.zhText=费用
ibs.enText=费用
ibs.Tip=费用查询
ibs.zhTip=费用查询
ibs.enTip=费用查询
ibs.M=S
ibs.Action=onSelIbs
ibs.pic=fee.gif

tnb.Type=TMenuItem
tnb.Text=血糖报告(NOVA)
tnb.zhText=血糖报告(NOVA)
tnb.enText=血糖报告(NOVA)
tnb.Tip=血糖报告(NOVA)
tnb.zhTip=血糖报告(NOVA)
tnb.enTip=血糖报告(NOVA)
tnb.M=S
tnb.Action=onTnb
tnb.pic=modify.gif

bgReport.Type=TMenuItem
bgReport.Text=血糖报告(强生)
bgReport.zhText=血糖报告(强生)
bgReport.enText=bgReport
bgReport.Tip=血糖报告(强生)
bgReport.zhTip=血糖报告(强生)
bgReport.enTip=bgReport
bgReport.M=J
bgReport.Action=getBgReport
bgReport.pic=modify.gif

export.Type=TMenuItem
export.Text=导出
export.Tip=导出
export.M=E
export.key=Ctrl+E
export.Action=onExport
export.pic=export.gif

reg.Type=TMenuItem
reg.Text=预约
reg.zhText=预约
reg.enText=Appointment of registered
reg.Tip=预约挂号
reg.zhTip=预约挂号
reg.enTip=Appointment of registered
reg.Action=onReg
reg.pic=date.gif

printLis.Type=TMenuItem
printLis.Text=打印LIS报告
printLis.Tip=打印LIS报告
printLis.zhText=打印LIS报告
printLis.M=L
printLis.key=Ctrl+L
printLis.Action=onPrintLis
printLis.pic=print-1.gif

cxMrshow.Type=TMenuItem
cxMrshow.Text=时间轴病历
cxMrshow.Tip=时间轴病历(Ctrl+Q)
cxMrshow.M=Q
cxMrshow.key=Ctrl+Q
cxMrshow.Action=onCxShow
cxMrshow.pic=038.gif

showpat.Type=TMenuItem
showpat.Text=历次就诊
showpat.zhText=历次就诊
showpat.enText=Pat Info
showpat.Tip=CDR
showpat.zhTip=CDR
showpat.enTip=Pat Info
showpat.M=P
showpat.key=Ctrl+P
showpat.Action=onQuerySummaryInfo
showpat.pic=patlist.gif

erdTriage.Type=TMenuItem
erdTriage.Text=检伤评估
erdTriage.Tip=检伤评估
erdTriage.M=
erdTriage.key=
erdTriage.Action=onErdTriage
erdTriage.pic=emr-2.gif

hrm.Type=TMenuItem
hrm.Text=健检总检
hrm.zhText=健检总检
hrm.Tip=健检总检
hrm.zhTip=健检总检
hrm.Action=onHrmEmr
hrm.pic=039.gif

daysurgery.Type=TMenuItem
daysurgery.Text=日间手术变更
daysurgery.zhText=日间手术变更
daysurgery.enText=日间手术变更
daysurgery.Tip=日间手术变更
daysurgery.zhTip=日间手术变更
daysurgery.enTip=日间手术变更
daysurgery.M=R
daysurgery.key=
daysurgery.Action=onDaySurgery
daysurgery.pic=025.gif

communicate.Type=TMenuItem
communicate.Text=沟通
communicate.Tip=沟通
communicate.M=F
communicate.key=F6
communicate.Action=onCommunicate
communicate.pic=AlignWidth.GIF


unlock.Type=TMenuItem
unlock.Text=临时解锁
unlock.zhText=临时解锁
unlock.enText=临时解锁
unlock.Tip=临时解锁
unlock.zhTip=临时解锁
unlock.enTip=临时解锁
unlock.M=G
unlock.key=
unlock.Action=onUnlock
unlock.pic=032.gif

opeNursingRecord.Type=TMenuItem
opeNursingRecord.Text=介入护理记录
opeNursingRecord.zhText=介入护理记录
opeNursingRecord.enText=
opeNursingRecord.Tip=介入护理记录
opeNursingRecord.zhTip=介入护理记录
opeNursingRecord.enTip=
opeNursingRecord.M=
opeNursingRecord.key=
opeNursingRecord.Action=onOpeNursingRecord
opeNursingRecord.pic=query.gif



