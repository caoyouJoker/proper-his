<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;ektCard;|;print;|;tablePrint;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;ektCard;|;print;|;tablePrint;|;clear;|;close

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

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=F
print.key=Ctrl+P
print.Action=onPrint
print.pic=Print.gif

ektCard.Type=TMenuItem
ektCard.Text=��ҽ�ƿ�
ektCard.Tip=��ҽ�ƿ�
ektCard.M=
ektCard.key=F6
ektCard.Action=onEKT
ektCard.pic=042.gif

tablePrint.Type=TMenuItem
tablePrint.Text=������ݴ�ӡ
tablePrint.Tip=������ݴ�ӡ
tablePrint.M=F
tablePrint.key=Ctrl+P
tablePrint.Action=onTablePrint
tablePrint.pic=Print.gif

