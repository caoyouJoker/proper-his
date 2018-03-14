<Type=TMenuBar>
UI.Item=File;Window;nurseWork;report/result
UI.button=save;query;clear;|;print;|;Newprint;|;code;barCode;|;pasterBottle;|;skiResult;|;schdCode;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;delete;Refresh;query;|;print;|;Newprint;code;pasterBottle;|;checkrep;|;testrep;|;clear;|;skiResult;|;schdCode;|;close

nurseWork.Type=TMenu
nurseWork.Text=护理业务
nurseWork.M=N
nurseWork.Item=emr;nursingRecord;nis;tpr;newtpr;charge

report/result.Type=TMenu
report/result.Text=报告/结果
report/result.M=R
report/result.Item=labReport;imageReport;eccReport;xtReport;bgReport;intensiveCare;militaryRecord;getQiTaPDF

//===========================================
emr.Type=TMenuItem
emr.Text=结构化病历
emr.Tip=结构化病历
emr.M=J
emr.key=Ctrl+J
emr.Action=onEmr
emr.pic=emr-2.gif

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

charge.Type=TMenuItem
charge.Text=补充计费
charge.Tip=补充计费
charge.M=H
charge.key=Ctrl+H
charge.Action=onCharge
charge.pic=bill-1.gif

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

//====================================================
save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

skiResult.Type=TMenuItem
skiResult.Text=皮试结果
skiResult.Tip=皮试结果
skiResult.M=P
skiResult.Action=onSkiResult
skiResult.pic=032.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

print.Type=TMenuItem
print.Text=单人执行单
print.Tip=单人执行单
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

Newprint.Type=TMenuItem
Newprint.Text=多人执行单
Newprint.Tip=多人执行单
Newprint.M=PN
Newprint.Action=onPrintExe
Newprint.pic=print-1.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

code.Type=TMenuItem
code.Text=检验条码
code.Tip=检验条码
code.M=CO
code.Action=onBarCode
code.pic=barcode.gif

paster.Type=TMenuItem
paster.Text=打印贴纸
paster.Tip=打印贴纸
paster.Action=onPrintPaster
paster.pic=048.gif

schdCode.Type=TMenuItem
schdCode.Text=路径时程
schdCode.Tip=路径时程
schdCode.M=IS
schdCode.Action=onChangeSchd
schdCode.pic=convert.gif

pasterBottle.Type=TMenuItem
pasterBottle.Text=瓶签贴纸
pasterBottle.Tip=瓶签贴纸
pasterBottle.Action=onPrintPasterBottle
pasterBottle.pic=048.gif

barCode.Type=TMenuItem
barCode.Text=药品条码
barCode.Tip=药品条码
barCode.Action=GeneratPhaBarcode
barCode.pic=PHL.gif


checkrep.Type=TMenuItem
checkrep.Text=检验报告
checkrep.Tip=检验报告
checkrep.M=
checkrep.key=
checkrep.Action=onCheckrep
checkrep.pic=Lis.gif

testrep.Type=TMenuItem
testrep.Text=检查报告
testrep.Tip=检查报告
testrep.M=
testrep.key=
testrep.Action=onTestrep
testrep.pic=emr-2.gif

nis.Type=TMenuItem
nis.Text=护理计划
nis.Tip=护理计划
nis.M=
nis.key=
nis.Action=onNis
nis.pic=emr-2.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClosePanel
close.pic=close.gif