<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;new;defeasance;query;print;nis;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;new;defeasance;Refresh;query;print;|;nis;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

new.Type=TMenuItem
new.Text=����
new.Tip=����
new.M=N
new.key=Ctrl+N
new.Action=onNew
new.pic=039.gif

defeasance.Type=TMenuItem
defeasance.Text=����
defeasance.Tip=����
defeasance.M=D
defeasance.key=Ctrl+D
defeasance.Action=onDefeasance
defeasance.pic=004.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

#modify by lili 20180401
#nis.Type=TMenuItem
#nis.Text=������
#nis.Tip=������
#nis.M=N
#nis.key=Ctrl+N
#nis.Action=onNisVitalSign
#nis.pic=operation.gif

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