#
  # Title: ��������벿ƽ̨��������
  #
  # Description:��������벿ƽ̨��������
  # Copyright: ProperSoft(c) 2017
  # @version 2.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;account;|;download;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;account;|;download;|;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+Q
query.Action=onQuery
query.pic=query.gif

account.Type=TMenuItem
account.Text=����
account.Tip=����
account.M=A
account.Action=onAccount
account.pic=sta-1.gif

download.Type=TMenuItem
download.Text=����
download.Tip=����
download.M=U
download.Action=onDownload
download.pic=Commit.gif

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

