<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;query;clear;print;delete;creatPuro;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;Refresh;query;|;clear;print;delete;creatPuro;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=N
print.key=Delete
print.Action=onPrint
print.pic=print.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif


query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

creatPuro.Type=TMenuItem
creatPuro.Text=���ɶ�����
creatPuro.Tip=���ɶ�����
creatPuro.M=C
creatPuro.key=Ctrl+I
creatPuro.Action=onCreatPuro
creatPuro.pic=039.gif

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
