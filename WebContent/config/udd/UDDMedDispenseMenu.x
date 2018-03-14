<Type=TMenuBar>
UI.Item=File;Window;phaWork;report/result
UI.button=save;delete;query;clear;|;EKT;|;barCode;|;pasterBottle;|;dispense;|;unDispense;|;dispenseCtrl;|;ATC;|;printDispenseBarCode;|;close;
  
Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=

phaWork.Type=TMenu
phaWork.Text=药房业务
phaWork.M=N
phaWork.Item=barCode;pasterBottle;printDispenseBarCode

report/result.Type=TMenu
report/result.Text=报告/结果
report/result.M=R
report/result.Item=dispense;unDispense;dispenseCtrl;labReport;imageReport;eccReport;xtReport;bgReport


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

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

barCode.Type=TMenuItem
barCode.Text=针剂瓶签
barCode.Tip=针剂瓶签
barCode.Action=GeneratPhaBarcode
barCode.pic=PHL.gif

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
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

ATC.Type=TMenuItem
ATC.Text=送包药机
ATC.Tip=送包药机
ATC.M=A
ATC.key=Ctrl+G
ATC.Action=onGenATCFile
ATC.pic=nurse.gif

unDispense.Type=TMenuItem
unDispense.Text=统药单
unDispense.Tip=统药单
unDispense.Action=onUnDispense
unDispense.pic=pharm.GIF

dispense.Type=TMenuItem
dispense.Text=配药确认单
dispense.Tip=配药确认单
dispense.Action=onDispenseSheet
dispense.pic=inwimg.gif


pasterBottle.Type=TMenuItem
pasterBottle.Text=输液瓶签
pasterBottle.Tip=输液瓶签
pasterBottle.Action=onPrintPasterBottle
pasterBottle.pic=048.gif

EKT.Type=TMenuItem
EKT.Text=读医疗卡
EKT.Tip=读医疗卡
EKT.M=E
EKT.key=Ctrl+E
EKT.Action=onEKT
EKT.pic=042.gif

dispenseCtrl.Type=TMenuItem
dispenseCtrl.Text=麻精配药确认单
dispenseCtrl.Tip=麻精配药确认单
dispenseCtrl.Action=onPrintCtrlDispenseSheet
dispenseCtrl.pic=bank.gif

printDispenseBarCode.Type=TMenuItem
printDispenseBarCode.Text=配药条码
printDispenseBarCode.Tip=配药条码
printDispenseBarCode.Action=printDispenseBarCode
printDispenseBarCode.pic=barcode.gif