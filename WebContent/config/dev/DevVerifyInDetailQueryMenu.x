<Type=TMenuBar>
UI.Item=File
UI.button=query;|;print;|;export;|;clear;|;close

File.Type=TMenu
File.Text=�ļ�
File.M=F 
File.Item=query;|;print;|;export;|;clear;|;close

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


export.Type=TMenuItem
export.Text=���
export.Tip=���
execrpt.M=E
export.Action=onExport
export.pic=exportexcel.gif



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
print.key=Alt+F8
print.Action=onPrint
print.pic=print.gif