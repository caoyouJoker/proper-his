 #
  # Title: ��תԺ��
  #
  # Description: ��תԺ��
  #
  # Copyright: ProperSoft (c) 2012
  #
  # @author shibl
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;Excel;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;Excel;|;clear;|;close



Excel.Type=TMenuItem
Excel.Text=��������
Excel.Tip=��������(Ctrl+D)
Excel.M=D
Excel.key=Ctrl+D
Excel.Action=onExport
Excel.pic=045.gif


query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

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