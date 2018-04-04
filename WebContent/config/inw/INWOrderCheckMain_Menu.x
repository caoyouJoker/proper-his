<Type=TMenuBar>
UI.Item=File;Window;nurseWork;report/result
UI.button=save;|;query;|;clear;|;Newprint;|;medPrint;|;medApplyNo;|;send;|;AMI;|;pay;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;delete;Refresh;query;Newprint;medPrint;|;medApplyNo;|;send;|;clear;|;pay;|;close

nurseWork.Type=TMenu
nurseWork.Text=护理业务
nurseWork.M=N
nurseWork.Item=record;nursingRecord;tpr;newtpr

report/result.Type=TMenu
report/result.Text=报告/结果
report/result.M=R
report/result.Item=labReport;imageReport;eccReport;xtReport;bgReport;intensiveCare;militaryRecord

//===========================================


AMI.Type=TMenuItem
AMI.Text=胸痛中心
AMI.Tip=胸痛中心
AMI.M=
AMI.key=
AMI.Action=onAMI
AMI.pic=query.gif


record.Type=TMenuItem
record.Text=结构化病历
record.zhText=结构化病历
record.enText=record
record.Tip=结构化病历
record.zhTip=结构化病历
record.enTip=record
record.M=RE
record.Action=onEmrWrite
record.pic=emr.gif

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

tpr.Type=TMenuItem
tpr.Text=体温单
tpr.Tip=体温单
tpr.M=J
tpr.key=Ctrl+T
tpr.Action=onVitalSign
tpr.pic=023.gif

newtpr.Type=TMenuItem
newtpr.Text=儿童儿体温单
newtpr.Tip=儿童儿体温单
newtpr.M=J
newtpr.key=Ctrl+P
newtpr.Action=onNewArrival
newtpr.pic=035.gif

//=================================================
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

//===============================================

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

send.Type=TMenuItem
send.Text=重送
send.Tip=重送
send.M=O
send.key=Ctrl+O
send.Action=onReSendGYPha
send.pic=Commit.gif


query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Newprint.Type=TMenuItem
Newprint.Text=多人打印
Newprint.Tip=多人打印
Newprint.M=PN
Newprint.Action=onPrintExe
Newprint.pic=print-1.gif

medPrint.Type=TMenuItem
medPrint.Text=取药单打印
medPrint.Tip=取药单打印
medPrint.Action=onDispenseSheet
medPrint.pic=print-2.gif



clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClosePanel
close.pic=close.gif

medApplyNo.Type=TMenuItem
medApplyNo.Text=检验条码
medApplyNo.Tip=检验条码
medApplyNo.M=C
medApplyNo.Action=onMedApplyPrint
medApplyNo.pic=barCode.gif

pay.Type=TMenuItem
pay.Text=补充计费
pay.Tip=补充计费
pay.M=P
pay.Action=onPay
pay.pic=bill-1.gif
