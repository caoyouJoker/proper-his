<Type=TMenuBar>
UI.Item=File;Window;phaWork;report/result
UI.button=save;query;EKT;clear;elecCaseHistory;bingli;pasterSwab;paster;ATC;sendbox;call;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;delete;Refresh;query;EKT;ATC;|;clear;|;close

phaWork.Type=TMenu
phaWork.Text=ҩ��ҵ��
phaWork.M=N
phaWork.Item=pasterSwab;paster

report/result.Type=TMenu
report/result.Text=����/���
report/result.M=R
report/result.Item=labReport;imageReport;eccReport;xtReport


//=================================================
labReport.Type=TMenuItem
labReport.Text=���鱨��
labReport.zhText=���鱨��
labReport.enText=labReport
labReport.Tip=���鱨��
labReport.zhTip=���鱨��
labReport.enTip=labReport
labReport.M=L
labReport.Action=onLis
labReport.pic=LIS.gif

imageReport.Type=TMenuItem
imageReport.Text=��鱨��
imageReport.zhText=��鱨��
imageReport.enText=imageReport
imageReport.Tip=��鱨��
imageReport.zhTip=��鱨��
imageReport.enTip=imageReport
imageReport.M=I
imageReport.Action=onRis
imageReport.pic=RIS.gif

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

//====================================================

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

EKT.Type=TMenuItem
EKT.Text=��ҽ�ƿ�
EKT.Tip=��ҽ�ƿ�
EKT.M=E
EKT.key=Ctrl+E
EKT.Action=onEKT
EKT.pic=042.gif

ATC.Type=TMenuItem
ATC.Text=�Ͱ�ҩ��
ATC.Tip=�Ͱ�ҩ��
ATC.M=A
ATC.key=Ctrl+G
ATC.Action=onGenATCFile
ATC.pic=nurse.gif

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

call.Type=TMenuItem
call.Text=�к�
call.Tip=�к�
call.M=CA
call.Action=onCall
call.pic=tel.gif

elecCaseHistory.Type=TMenuItem
elecCaseHistory.Text=���Ӳ���
elecCaseHistory.Tip=���Ӳ���
elecCaseHistory.M=EL
elecCaseHistory.Action=onElecCaseHistory
elecCaseHistory.pic=emr.gif

paster.Type=TMenuItem
paster.Text=��ҩ��
paster.Tip=��ҩ��
paster.M=PA
paster.Action=onPaster
paster.pic=print.gif

//dispenseDrugList.Type=TMenuItem
//dispenseDrugList.Text=��ҩ��
//dispenseDrugList.Tip=��ҩ��
//dispenseDrugList.M=DD
//dispenseDrugList.Action=onDispenseDrugList
//dispenseDrugList.pic=print.gif

pasterSwab.Type=TMenuItem
pasterSwab.Text=��ӡҩǩ
pasterSwab.Tip=��ӡҩǩ
pasterSwab.M=PAS
pasterSwab.Action=onPasterSwab
pasterSwab.pic=print-2.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

bingli.Type=TMenuItem
bingli.Text=����
bingli.Tip=����
bingli.M=
bingli.key=
bingli.Action=onErdSheet
bingli.pic=search.gif


sendbox.Type=TMenuItem
sendbox.Text=�ͺ�װ��ҩ��
sendbox.Tip=�ͺ�װ��ҩ��
sendbox.M=A
sendbox.key=Ctrl+G
sendbox.Action=onDispenseBoxMachine
sendbox.pic=bank.gif


test.Type=TMenuItem
test.Text=�ͺ�װ��ҩ��
test.Tip=�ͺ�װ��ҩ��
test.M=A
test.key=Ctrl+G
test.Action=onTest
test.pic=bank.gif