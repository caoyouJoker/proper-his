#
# TBuilder Config File 
#
# Title:
#
# Company:JavaHis
#
# Author:sunqy 2014.05.12
#
# version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;delete;|;order;|;print;|;onPrintBAE;|;transfer;|;create;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;delete;|;order;|;print;|;onPrintBAE;|;transfer;|;create;|;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(query)
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
clear.Tip=���(clear)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

order.Type=TMenuItem
order.Text=����ҽ��¼��
order.Tip=����ҽ��¼��
order.M=S
order.key=Ctrl+S
order.Action=onOpeOrder
order.pic=Create.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(close)
close.M=X
close.key=Alt+F4
close.Action=onClosePanel
close.pic=close.gif

print.Type=TMenuItem
print.Text=��ӡ
print.zhText=��ӡ
print.enText=Print
print.Tip=��ӡ(Ctrl+P)
print.zhTip=��ӡ
print.enTip=Print
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif


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

onPrintBAE.Type=TMenuItem
onPrintBAE.Text=���밲ȫ�˲鵥
onPrintBAE.Tip=���밲ȫ�˲鵥
onPrintBAE.M=C
onPrintBAE.key=
onPrintBAE.Action=onPrintBAE
onPrintBAE.pic=print.gif

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