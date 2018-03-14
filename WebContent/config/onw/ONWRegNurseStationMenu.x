#############################################
# <p>Title:门急诊护士工作站Menu </p>
#
# <p>Description:门急诊护士工作站Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window;nurseWork;report/result
UI.button=query;|;clear;|;detach;|;patdata;|;erd;|;body;|;barcode;|;planrep;|;docplan;|;supcharge;|;showpat;|;erdLevel;|;create;|;transfer;|;close;

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=Refresh;query;|;clear;|;close;

nurseWork.Type=TMenu
nurseWork.Text=护士业务
nurseWork.M=N
nurseWork.Item=detach;|;body;|;supcharge

report/result.Type=TMenu
report/result.Text=报告/结果
report/result.M=R\

// add by wangqing 20180124 新增regFallAndPainReport，查看急诊跌倒、疼痛评估表
report/result.Item=checkrep;testrep;|;eccReport;|;xtReport;|;getQiTaPDF;regFallAndPainReport

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

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

detach.Type=TMenuItem
detach.Text=分诊
detach.Tip=分诊
detach.M=
detach.key=
detach.Action=onDetach
detach.pic=convert.gif

patdata.Type=TMenuItem
patdata.Text=病患资料
patdata.Tip=病患资料
patdata.M=
patdata.key=
patdata.Action=onPatdata
patdata.pic=038.gif

barcode.Type=TMenuItem
barcode.Text=检验条码
barcode.Tip=检验条码
barcode.M=X
barcode.key=
barcode.Action=onBarcode
barcode.pic=barcode.gif

body.Type=TMenuItem
body.Text=体征采集
body.Tip=体征采集
body.M=X
body.key=
body.Action=onBody
body.pic=new.gif

planrep.Type=TMenuItem
planrep.Text=报告进度
planrep.Tip=报告进度
planrep.M=
planrep.key=
planrep.Action=onPlanrep
planrep.pic=detail-1.gif

docplan.Type=TMenuItem
docplan.Text=医技进度
docplan.Tip=医技进度
docplan.M=
docplan.key=
docplan.Action=onDocplan
docplan.pic=detail.gif

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

supcharge.Type=TMenuItem
supcharge.Text=补充计价
supcharge.Tip=补充计价
supcharge.M=
supcharge.key=
supcharge.Action=onSupcharge
supcharge.pic=bill.gif

psmanage.Type=TMenuItem
psmanage.Text=皮试
psmanage.Tip=皮试
psmanage.M=
psmanage.key=
psmanage.Action=onPSManage
psmanage.pic=phl.gif

opdrecord.Type=TMenuItem
opdrecord.Text=就诊记录
opdrecord.Tip=就诊记录
opdrecord.M=
opdrecord.key=
opdrecord.Action=onOPDRecord
opdrecord.pic=010.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
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

erdLevel.Type=TMenuItem
erdLevel.Text=检伤补录
erdLevel.Tip=检伤补录
erdLevel.M=X
erdLevel.key=
erdLevel.Action=onErdLevel
erdLevel.pic=new.gif

erd.Type=TMenuItem
erd.Text=检伤评估
erd.Tip=检伤评估
erd.M=
erd.key=
erd.Action=onErdTriage
erd.pic=emr-2.gif

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

bloodEmr.Type=TMenuItem
bloodEmr.Text=采血病历
bloodEmr.Tip=采血病历
bloodEmr.M=
bloodEmr.key=
bloodEmr.Action=onBloodEmr
bloodEmr.pic=correct.gif

preInfo.Type=TMenuItem
preInfo.Text=院前信息
preInfo.Tip=院前信息
preInfo.M=
preInfo.key=
preInfo.Action=onPreInfo
preInfo.pic=correct.gif

rescueRecord.Type=TMenuItem
rescueRecord.Text=抢救记录
rescueRecord.Tip=抢救记录
rescueRecord.M=
rescueRecord.key=
rescueRecord.Action=onRescueRecord
rescueRecord.pic=correct.gif

// add by wangqing 20180124 新增regFallAndPainReport，查看急诊跌倒、疼痛评估表
regFallAndPainReport.Type=TMenuItem
regFallAndPainReport.Text=急诊跌倒、疼痛评估表
regFallAndPainReport.Tip=急诊跌倒、疼痛评估表
regFallAndPainReport.M=
regFallAndPainReport.key=
regFallAndPainReport.Action=onFallAndPainAssessment
regFallAndPainReport.pic=emr-2.gif

