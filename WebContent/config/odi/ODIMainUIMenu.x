<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;bedcard;|;onSign;|;card;|;print;|;erdTriage;|;tpr;|;newtpr;|;pdf;printLis;tnb;|;showpat;cxMrshow;|;exportxml;|;create;|;transfer;|;evalution;|;opeNursingRecord;|;clear;|;close
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
File.Item=query;|;card;|;print;|;bedcard;|;tpr;|;newtpr;|;pdf;|;printLis;|;tnb;|;showpat;cxMrshow|;exportxml;|;create;|;transfer;|;evalution;|;opeNursingRecord;|;clear;|;close

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

tpr.Type=TMenuItem
tpr.Text=体温单
tpr.Tip=体温单
tpr.M=J
tpr.key=Ctrl+T
tpr.Action=onVitalSign
tpr.pic=023.gif

newtpr.Type=TMenuItem
newtpr.Text=新生儿体温单
newtpr.Tip=新生儿体温单
newtpr.M=J
newtpr.key=Ctrl+P
newtpr.Action=onNewArrival
newtpr.pic=035.gif

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

tnb.Type=TMenuItem
tnb.Text=血糖报告
tnb.zhText=血糖报告
tnb.enText=血糖报告
tnb.Tip=血糖报告
tnb.zhTip=血糖报告
tnb.enTip=血糖报告
tnb.M=S
tnb.Action=onTnb
tnb.pic=modify.gif

exportxml.Type=TMenuItem
exportxml.Text=导出
exportxml.Tip=导出
exportxml.M=P
exportxml.Action=onExport
exportxml.pic=export.gif

print.Type=TMenuItem
print.Text=床头卡打印
print.Tip=床头卡打印
print.M=F
print.key=Ctrl+P
print.Action=onPrintO
print.pic=Print.gif

printLis.Type=TMenuItem
printLis.Text=LIS报告
printLis.Tip=打印LIS报告
printLis.M=L
printLis.key=Ctrl+L
printLis.Action=onPrintLis
printLis.pic=print-1.gif


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

cxMrshow.Type=TMenuItem
cxMrshow.Text=时间轴病历
cxMrshow.Tip=时间轴病历
cxMrshow.M=Q
cxMrshow.key=Ctrl+Q
cxMrshow.Action=onCxShow
cxMrshow.pic=038.gif

showpat.Type=TMenuItem
showpat.Text=历次就诊
showpat.zhText=历次就诊
showpat.enText=Pat Info
showpat.Tip=历次就诊
showpat.zhTip=历次就诊
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

create.Type=TMenuItem
create.Text=生成交接单
create.Tip=生成交接单
create.M=X
create.key=Alt+F4
create.Action=onCreate
create.pic=save.gif

transfer.Type=TMenuItem
transfer.Text=交接一览表
transfer.Tip=交接一览表
transfer.M=X
transfer.key=Alt+F4
transfer.Action=onTransfer
transfer.pic=correct.gif

evalution.Type=TMenuItem
evalution.Text=评估一览表
evalution.Tip=评估一览表
evalution.M=X
evalution.key=Alt+F4
evalution.Action=onEvalutionRecordOpen
evalution.pic=correct.gif

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