<Type=TMenuBar>
UI.Item=File;Window;phaWork;report/result
UI.button=save;delete;query;clear;|;EKT;|;barCode;|;pasterBottle;|;dispense;|;unDispense;|;dispenseCtrl;|;ATC;|;printDispenseBarCode;|;close;
  
Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=

phaWork.Type=TMenu
phaWork.Text=ҩ��ҵ��
phaWork.M=N
phaWork.Item=barCode;pasterBottle;printDispenseBarCode

report/result.Type=TMenu
report/result.Text=����/���
report/result.M=R
report/result.Item=dispense;unDispense;dispenseCtrl;labReport;imageReport;eccReport;xtReport;bgReport


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
xtReport.Text=Ѫ�Ǳ���(NOVA)
xtReport.zhText=Ѫ�Ǳ���(NOVA)
xtReport.enText=xtReport
xtReport.Tip=Ѫ�Ǳ���(NOVA)
xtReport.zhTip=Ѫ�Ǳ���(NOVA)
xtReport.enTip=xtReport
xtReport.M=T
xtReport.Action=getXTReport
xtReport.pic=Retrieve.gif

bgReport.Type=TMenuItem
bgReport.Text=Ѫ�Ǳ���(ǿ��)
bgReport.zhText=Ѫ�Ǳ���(ǿ��)
bgReport.enText=bgReport
bgReport.Tip=Ѫ�Ǳ���(ǿ��)
bgReport.zhTip=Ѫ�Ǳ���(ǿ��)
bgReport.enTip=bgReport
bgReport.M=J
bgReport.Action=getBgReport
bgReport.pic=Retrieve.gif

//====================================================

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

barCode.Type=TMenuItem
barCode.Text=���ƿǩ
barCode.Tip=���ƿǩ
barCode.Action=GeneratPhaBarcode
barCode.pic=PHL.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

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

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

ATC.Type=TMenuItem
ATC.Text=�Ͱ�ҩ��
ATC.Tip=�Ͱ�ҩ��
ATC.M=A
ATC.key=Ctrl+G
ATC.Action=onGenATCFile
ATC.pic=nurse.gif

unDispense.Type=TMenuItem
unDispense.Text=ͳҩ��
unDispense.Tip=ͳҩ��
unDispense.Action=onUnDispense
unDispense.pic=pharm.GIF

dispense.Type=TMenuItem
dispense.Text=��ҩȷ�ϵ�
dispense.Tip=��ҩȷ�ϵ�
dispense.Action=onDispenseSheet
dispense.pic=inwimg.gif


pasterBottle.Type=TMenuItem
pasterBottle.Text=��Һƿǩ
pasterBottle.Tip=��Һƿǩ
pasterBottle.Action=onPrintPasterBottle
pasterBottle.pic=048.gif

EKT.Type=TMenuItem
EKT.Text=��ҽ�ƿ�
EKT.Tip=��ҽ�ƿ�
EKT.M=E
EKT.key=Ctrl+E
EKT.Action=onEKT
EKT.pic=042.gif

dispenseCtrl.Type=TMenuItem
dispenseCtrl.Text=�龫��ҩȷ�ϵ�
dispenseCtrl.Tip=�龫��ҩȷ�ϵ�
dispenseCtrl.Action=onPrintCtrlDispenseSheet
dispenseCtrl.pic=bank.gif

printDispenseBarCode.Type=TMenuItem
printDispenseBarCode.Text=��ҩ����
printDispenseBarCode.Tip=��ҩ����
printDispenseBarCode.Action=printDispenseBarCode
printDispenseBarCode.pic=barcode.gif