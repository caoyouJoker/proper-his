#############################################
# <p>Title:�ż��ﻤʿ����վMenu </p>
#
# <p>Description:�ż��ﻤʿ����վMenu </p>
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
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=Refresh;query;|;clear;|;close;

nurseWork.Type=TMenu
nurseWork.Text=��ʿҵ��
nurseWork.M=N
nurseWork.Item=detach;|;body;|;supcharge

report/result.Type=TMenu
report/result.Text=����/���
report/result.M=R\

// add by wangqing 20180124 ����regFallAndPainReport���鿴�����������ʹ������
report/result.Item=checkrep;testrep;|;eccReport;|;xtReport;|;getQiTaPDF;regFallAndPainReport

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

getQiTaPDF.Type=TMenuItem
getQiTaPDF.Text=��������
getQiTaPDF.zhText=��������
getQiTaPDF.enText=getQiTaPDF
getQiTaPDF.Tip=��������
getQiTaPDF.zhTip=��������
getQiTaPDF.enTip=getQiTaPDF
getQiTaPDF.M=C
getQiTaPDF.Action=getPDFQiTa
getQiTaPDF.pic=PicData01.jpg

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif



clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

detach.Type=TMenuItem
detach.Text=����
detach.Tip=����
detach.M=
detach.key=
detach.Action=onDetach
detach.pic=convert.gif

patdata.Type=TMenuItem
patdata.Text=��������
patdata.Tip=��������
patdata.M=
patdata.key=
patdata.Action=onPatdata
patdata.pic=038.gif

barcode.Type=TMenuItem
barcode.Text=��������
barcode.Tip=��������
barcode.M=X
barcode.key=
barcode.Action=onBarcode
barcode.pic=barcode.gif

body.Type=TMenuItem
body.Text=�����ɼ�
body.Tip=�����ɼ�
body.M=X
body.key=
body.Action=onBody
body.pic=new.gif

planrep.Type=TMenuItem
planrep.Text=�������
planrep.Tip=�������
planrep.M=
planrep.key=
planrep.Action=onPlanrep
planrep.pic=detail-1.gif

docplan.Type=TMenuItem
docplan.Text=ҽ������
docplan.Tip=ҽ������
docplan.M=
docplan.key=
docplan.Action=onDocplan
docplan.pic=detail.gif

checkrep.Type=TMenuItem
checkrep.Text=���鱨��
checkrep.Tip=���鱨��
checkrep.M=
checkrep.key=
checkrep.Action=onCheckrep
checkrep.pic=Lis.gif

testrep.Type=TMenuItem
testrep.Text=��鱨��
testrep.Tip=��鱨��
testrep.M=
testrep.key=
testrep.Action=onTestrep
testrep.pic=emr-2.gif

supcharge.Type=TMenuItem
supcharge.Text=����Ƽ�
supcharge.Tip=����Ƽ�
supcharge.M=
supcharge.key=
supcharge.Action=onSupcharge
supcharge.pic=bill.gif

psmanage.Type=TMenuItem
psmanage.Text=Ƥ��
psmanage.Tip=Ƥ��
psmanage.M=
psmanage.key=
psmanage.Action=onPSManage
psmanage.pic=phl.gif

opdrecord.Type=TMenuItem
opdrecord.Text=�����¼
opdrecord.Tip=�����¼
opdrecord.M=
opdrecord.key=
opdrecord.Action=onOPDRecord
opdrecord.pic=010.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

cxMrshow.Type=TMenuItem
cxMrshow.Text=ʱ���Ს��
cxMrshow.Tip=ʱ���Ს��
cxMrshow.M=Q
cxMrshow.key=Ctrl+Q
cxMrshow.Action=onCxShow
cxMrshow.pic=038.gif

showpat.Type=TMenuItem
showpat.Text=���ξ���
showpat.zhText=���ξ���
showpat.enText=Pat Info
showpat.Tip=���ξ���
showpat.zhTip=���ξ���
showpat.enTip=Pat Info
showpat.M=P
showpat.key=Ctrl+P
showpat.Action=onQuerySummaryInfo
showpat.pic=patlist.gif

erdLevel.Type=TMenuItem
erdLevel.Text=���˲�¼
erdLevel.Tip=���˲�¼
erdLevel.M=X
erdLevel.key=
erdLevel.Action=onErdLevel
erdLevel.pic=new.gif

erd.Type=TMenuItem
erd.Text=��������
erd.Tip=��������
erd.M=
erd.key=
erd.Action=onErdTriage
erd.pic=emr-2.gif

eccReport.Type=TMenuItem
eccReport.Text=�ĵ籨��
eccReport.zhText=�ĵ籨��
eccReport.enText=eccReport
eccReport.Tip=�ĵ籨��
eccReport.zhTip=�ĵ籨��
eccReport.enTip=eccReport
eccReport.M=C
eccReport.Action=getPdfReport
eccReport.pic=PicData01.jpg

xtReport.Type=TMenuItem
xtReport.Text=Ѫ�Ǳ���
xtReport.zhText=Ѫ�Ǳ���
xtReport.enText=xtReport
xtReport.Tip=Ѫ�Ǳ���
xtReport.zhTip=Ѫ�Ǳ���
xtReport.enTip=xtReport
xtReport.M=T
xtReport.Action=getXTReport
xtReport.pic=Retrieve.gif

create.Type=TMenuItem
create.Text=���ɽ��ӵ�
create.Tip=���ɽ��ӵ�
create.M=X
create.key=Alt+F4
create.Action=onCreate
create.pic=save.gif

transfer.Type=TMenuItem
transfer.Text=����һ����
transfer.Tip=����һ����
transfer.M=X
transfer.key=Alt+F4
transfer.Action=onTransfer
transfer.pic=correct.gif

bloodEmr.Type=TMenuItem
bloodEmr.Text=��Ѫ����
bloodEmr.Tip=��Ѫ����
bloodEmr.M=
bloodEmr.key=
bloodEmr.Action=onBloodEmr
bloodEmr.pic=correct.gif

preInfo.Type=TMenuItem
preInfo.Text=Ժǰ��Ϣ
preInfo.Tip=Ժǰ��Ϣ
preInfo.M=
preInfo.key=
preInfo.Action=onPreInfo
preInfo.pic=correct.gif

rescueRecord.Type=TMenuItem
rescueRecord.Text=���ȼ�¼
rescueRecord.Tip=���ȼ�¼
rescueRecord.M=
rescueRecord.key=
rescueRecord.Action=onRescueRecord
rescueRecord.pic=correct.gif

// add by wangqing 20180124 ����regFallAndPainReport���鿴�����������ʹ������
regFallAndPainReport.Type=TMenuItem
regFallAndPainReport.Text=�����������ʹ������
regFallAndPainReport.Tip=�����������ʹ������
regFallAndPainReport.M=
regFallAndPainReport.key=
regFallAndPainReport.Action=onFallAndPainAssessment
regFallAndPainReport.pic=emr-2.gif

