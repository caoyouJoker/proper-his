<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;delRow;|;operation;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;delRow;|;operation;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delRow.Type=TMenuItem
delRow.Text=ɾ��ҽ��
delRow.Tip=ɾ��ҽ��
delRow.M=D
delRow.Action=onDelRow
delRow.pic=delete.gif

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

operation.Type=TMenuItem
operation.Text=�����ҼƷ�
operation.Tip=�����ҼƷ�
operation.M=P
operation.Action=onOperation
operation.pic=operation.gif
