<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;save;|;print;|;losePrint;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;clear;save;print;|;Refresh;|;losePrint;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=S
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

save.Type=TMenuItem
save.Text=�վ��˷�
save.Tip=�վ��˷�
save.M=P
save.Action=onSave
save.pic=030.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=B
print.Action=onPrint
print.pic=print.gif

losePrint.Type=TMenuItem
losePrint.Text=Ʊ����ʧ��ӡ
losePrint.Tip=Ʊ����ʧ��ӡ
losePrint.M=
losePrint.Action=onLosePrint
losePrint.pic=print_red.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

