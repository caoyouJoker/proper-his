<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;return;|;delete;|;query;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;delete;|;query;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=Q
save.key=Ctrl+F
save.Action=onSave
save.pic=save.gif

return.Type=TMenuItem
return.Text=����
return.Tip=����
return.M=Q
return.key=Ctrl+F
return.Action=onReturnAllergy
return.pic=054.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��
delete.M=Q
delete.key=Ctrl+F
delete.Action=onDelete
delete.pic=delete.gif


query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

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

