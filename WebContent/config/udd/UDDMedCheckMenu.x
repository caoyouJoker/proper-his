<Type=TMenuBar>
UI.Item=File;Window;phaWork;report/result
UI.button=save;delete;query;clear;|;EKT;|;queryDrug;checkDrugHand;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;delete;Refresh;query;|;clear;|;queryDrug;checkDrugHand;|;close

phaWork.Type=TMenu
phaWork.Text=药房业务
phaWork.M=N
phaWork.Item=queryDrug

report/result.Type=TMenu
report/result.Text=报告/结果
report/result.M=R
report/result.Item=labReport;imageReport;eccReport;xtReport;bgReport


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

delete.Type=TMenuItem
delete.Text=刷新
delete.Tip=刷新
delete.M=R
delete.key=F5
delete.Action=onDelete
delete.pic=delete.gif

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
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

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
