#
  # Title: סԺ�渶������ϸ�ϴ�
  # Description:סԺ�渶������ϸ�ϴ�
  # Copyright: ProperSoft(c) 2017
  # @version 2.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;download;|;sumdetail;|;upload;|;cancel;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;download;|;sumdetail;|;upload;|;cancel;|;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+Q
query.Action=onQuery
query.pic=query.gif

download.Type=TMenuItem
download.Text=��Ϣ����
download.Tip=��Ϣ����
download.M=U
download.Action=onDownload
download.pic=Commit.gif

sumdetail.Type=TMenuItem
sumdetail.Text=��ϸ����
sumdetail.Tip=��ϸ����
sumdetail.M=A
sumdetail.Action=onSumdetail
sumdetail.pic=sta-1.gif


upload.Type=TMenuItem
upload.Text=�����ϴ�
upload.Tip=�����ϴ�
upload.M=U
upload.Action=onUpload
upload.pic=016.gif


cancel.Type=TMenuItem
cancel.Text=���ó���
cancel.Tip=���ó���
cancel.M=U
cancel.Action=onCancel
cancel.pic=030.gif

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

