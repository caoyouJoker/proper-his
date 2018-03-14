<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;opInfo;opRecord;|;cxMrshow;|;showpat;|;clear;|;onPrintOPBook;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

opInfo.Type=TMenuItem
opInfo.Text=手术申请明细
opInfo.Tip=手术申请明细
opInfo.Action=onOpInfo
opInfo.pic=detail-1.gif

opRecord.Type=TMenuItem
opRecord.Text=手术记录
opRecord.Tip=手术记录
opRecord.Action=onOpRecord
opRecord.pic=031.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新(F5)
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

onOpeConnect.Type=TMenuItem
onOpeConnect.Text=手术交接单
onOpeConnect.Tip=手术交接单
onOpeConnect.M=C
onOpeConnect.key=
onOpeConnect.Action=onOpeConnect
onOpeConnect.pic=print.gif

onPrintOPBook.Type=TMenuItem
onPrintOPBook.Text=手术安全核查单
onPrintOPBook.Tip=手术安全核查单
onPrintOPBook.M=C
onPrintOPBook.key=
onPrintOPBook.Action=onPrintOPBook
onPrintOPBook.pic=print.gif

onPrintBAE.Type=TMenuItem
onPrintBAE.Text=介入安全核查单
onPrintBAE.Tip=介入安全核查单
onPrintBAE.M=C
onPrintBAE.key=
onPrintBAE.Action=onPrintBAE
onPrintBAE.pic=print.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif


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