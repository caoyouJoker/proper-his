#
# TBuilder Config File 
#
# Title:�⹺����
#
# Company: ProperSoft
#
# Author:yufh 2014.04.08
#
# version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;upload;|;delete;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;upload;|;delete;|;clear;|;close


query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

upload.Type=TMenuItem
upload.Text=�ϴ�
upload.Tip=�ϴ�
upload.M=S
upload.key=Ctrl+S
upload.Action=onUpload
upload.pic=save.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
