<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;upload;|;download;|;resultload;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;upload;|;download;|;resultload;|;clear;|;close


upload.Type=TMenuItem
upload.Text=�����ϴ�
upload.Tip=�����ϴ�
upload.M=I
upload.key=Ctrl+D
upload.Action=onUpload
upload.pic=Save.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

download.Type=TMenuItem
download.Text=��������
download.Tip=��������
download.M=I
download.key=Ctrl+D
download.Action=onDownload
download.pic=Commit.gif

resultload.Type=TMenuItem
resultload.Text=����ϴ�
resultload.Tip=����ϴ�
resultload.M=S
resultload.key=Ctrl+S
resultload.Action=onResultload
resultload.pic=046.gif

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

