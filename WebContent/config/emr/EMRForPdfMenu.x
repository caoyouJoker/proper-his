<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;photo;|;dbfConvert;|;upload;|;preview;|;delete;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;photo;|;dbfConvert;|;upload;|;preview;|;delete;|;clear;|;close

query.Type=TMenuItem
query.Text=������ѯ
query.Tip=������ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=search-1.gif

photo.Type=TMenuItem
photo.Text=����
photo.Tip=����
photo.M=Q
photo.key=Ctrl+F
photo.Action=onPhoto
photo.pic=print-1.gif

dbfConvert.Type=TMenuItem
dbfConvert.Text=�ϴ�
dbfConvert.Tip=�ϴ�
dbfConvert.M=P
dbfConvert.Action=onCombination
dbfConvert.pic=046.gif

upload.Type=TMenuItem
upload.Text=�ύ
upload.Tip=�ύ
upload.M=I
upload.key=Ctrl+D
upload.Action=onUpload
upload.pic=Commit.gif

preview.Type=TMenuItem
preview.Text=�����������
preview.Tip=�����������
preview.M=I
preview.key=Ctrl+D
preview.Action=onReadSubmit
preview.pic=012.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��
delete.M=D
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

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

