 #
  # Title: ����л���ҽ�Ʊ��տ���Ԥ����֧����
  #
  # Description:����л���ҽ�Ʊ��տ���Ԥ����֧����
  #
  # Copyright: JavaHis (c) 2009
<Type=TMenuBar>
UI.Item=File;Window
UI.button=download;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=download;|;clear;|;close

download.Type=TMenuItem
download.Text=����
download.Tip=����
download.M=E
download.Action=onDownload
download.pic=030.gif


clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif


close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
