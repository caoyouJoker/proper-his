<Type=TMenuBar>
UI.Item=File;Window;phaWork;report/result
UI.button=save;query;EKT;clear;|;elecCaseHistory;bingli;queryDrug;checkDrugHand;|;call;|;close

//dispenseDrugList;

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;delete;Refresh;query;queryDrug;checkDrugHand;|;clear;|;call;|;close

phaWork.Type=TMenu
phaWork.Text=药房业务
phaWork.M=N
phaWork.Item=queryDrug

report/result.Type=TMenu
report/result.Text=报告/结果
report/result.M=R
report/result.Item=labReport;imageReport;eccReport;xtReport


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
xtReport.Text=血糖报告
xtReport.zhText=血糖报告
xtReport.enText=xtReport
xtReport.Tip=血糖报告
xtReport.zhTip=血糖报告
xtReport.enTip=xtReport
xtReport.M=T
xtReport.Action=getXTReport
xtReport.pic=Retrieve.gif

//====================================================

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

EKT.Type=TMenuItem
EKT.Text=读医疗卡
EKT.Tip=读医疗卡
EKT.M=E
EKT.key=Ctrl+E
EKT.Action=onEKT
EKT.pic=042.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

//code.Type=TMenuItem
//code.Text=取药号码牌
//code.Tip=取药号码牌
//code.M=CO
//code.key=Ctrl+H
//code.Action=onCode
//code.pic=barcode.gif

elecCaseHistory.Type=TMenuItem
elecCaseHistory.Text=电子病历
elecCaseHistory.Tip=电子病历
elecCaseHistory.M=EL
elecCaseHistory.Action=onElecCaseHistory
elecCaseHistory.pic=emr.gif

queryDrug.Type=TMenuItem
queryDrug.Text=药品信息
queryDrug.Tip=药品信息
queryDrug.M=QD
queryDrug.Action=queryDrug
queryDrug.pic=sta-4.gif

checkDrugHand.Type=TMenuItem
checkDrugHand.Text=合理用药
checkDrugHand.Tip=合理用药
checkDrugHand.M=CD
checkDrugHand.Action=checkDrugHand
checkDrugHand.pic=051.gif

//pasterSwab.Type=TMenuItem
//pasterSwab.Text=打印药签
//pasterSwab.Tip=打印药签
//pasterSwab.M=PAS
//pasterSwab.Action=onPasterSwab
//pasterSwab.pic=print-2.gif

dispenseDrugList.Type=TMenuItem
dispenseDrugList.Text=配药单
dispenseDrugList.Tip=配药单
dispenseDrugList.M=DD
dispenseDrugList.Action=onDispenseDrugList
dispenseDrugList.pic=print.gif

pasterSwab.Type=TMenuItem
pasterSwab.Text=打印药签
pasterSwab.Tip=打印药签
pasterSwab.M=PAS
pasterSwab.Action=onPasterSwab
pasterSwab.pic=print-2.gif


close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

bingli.Type=TMenuItem
bingli.Text=病历
bingli.Tip=病历
bingli.M=
bingli.key=
bingli.Action=onErdSheet
bingli.pic=search.gif


call.Type=TMenuItem
call.Text=叫号
call.Tip=叫号
call.M=CA
call.Action=onCall
call.pic=tel.gif