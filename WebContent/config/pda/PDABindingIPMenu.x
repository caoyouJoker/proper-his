<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;delete;|;remove;|;query;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;delete;|;remove;|;query;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

remove.Type=TMenuItem
remove.Text=���
remove.Tip=���(Ctrl+Z)
remove.M=J
remove.key=Ctrl+J
remove.Action=onRemove
remove.pic=lock-1.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��(Ctrl+Z)
delete.M=J
delete.key=Ctrl+J
delete.Action=onDelete
delete.pic=delete.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Q)
clear.M=C
clear.key=Ctrl+Q
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif