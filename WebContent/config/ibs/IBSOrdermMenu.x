<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;delete;|;clear;|;selFee;|;nbwPackage;|;deptPackage;|;showpat;|;operation;|;schdCode;|;clpOrderQuote;|;clpOrderReSchdCode;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;delete;clear;selFee;|;nbwPackage;|;deptPackage;showpat;|;operation;|;schdCode;|;Refresh;|;clpOrderQuote;|;clpOrderReSchdCode;|;close

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


Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

selFee.Type=TMenuItem
selFee.Text=���ò�ѯ
selFee.Tip=���ò�ѯ
selFee.M=IS
selFee.Action=onSelFee
selFee.pic=inscon.gif

schdCode.Type=TMenuItem
schdCode.Text=·��ʱ��
schdCode.Tip=·��ʱ��
schdCode.M=IS
schdCode.Action=onChangeSchd
schdCode.pic=convert.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

showpat.Type=TMenuItem
showpat.Text=�����б�
showpat.Tip=�����б�
showpat.M=P
showpat.Action=onShowPat
showpat.pic=patlist.gif

operation.Type=TMenuItem
operation.Text=�����ҼƷ�
operation.Tip=�����ҼƷ�
operation.M=P
operation.Action=onOperation
operation.pic=operation.gif

nbwPackage.Type=TMenuItem
nbwPackage.Text=��ʿ�ײ�
nbwPackage.Tip=��ʿ�ײ�
nbwPackage.M=
nbwPackage.Action=onNbwPackage
nbwPackage.pic=019.gif

deptPackage.Type=TMenuItem
deptPackage.Text=�����ײ�
deptPackage.Tip=�����ײ�
deptPackage.M=
deptPackage.Action=onDeptPackage
deptPackage.pic=012.gif

clpOrderQuote.Type=TMenuItem
clpOrderQuote.Text=����·��
clpOrderQuote.Tip=����·��
clpOrderQuote.M=
clpOrderQuote.Action=onAddCLNCPath
clpOrderQuote.pic=054.gif

clpOrderReSchdCode.Type=TMenuItem
clpOrderReSchdCode.Text=����ʱ���޸�
clpOrderReSchdCode.Tip=����ʱ���޸�
clpOrderReSchdCode.M=
clpOrderReSchdCode.Action=onClpOrderReSchdCode
clpOrderReSchdCode.pic=046.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClosePanel
//close.Action=onClose
close.pic=close.gif

