<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;query;EKT;clear;|;paster;code;elecCaseHistory;queryDrug;checkDrugHand;|;close

//dispenseDrugList;

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;delete;Refresh;query;paster;queryDrug;checkDrugHand;|;clear;|;close

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

paster.Type=TMenuItem
paster.Text=配药单
paster.Tip=配药单
paster.M=PA
paster.Action=onPaster
paster.pic=print.gif

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

dispenseDrugList.Type=TMenuItem
dispenseDrugList.Text=配药单
dispenseDrugList.Tip=配药单
dispenseDrugList.M=DD
dispenseDrugList.Action=onDispenseDrugList
dispenseDrugList.pic=print.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

EKT.Type=TMenuItem
EKT.Text=读医疗卡
EKT.Tip=读医疗卡
EKT.M=E
EKT.key=Ctrl+E
EKT.Action=onEKT
EKT.pic=042.gif

