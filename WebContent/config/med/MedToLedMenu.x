 #
  # Title: ������к�
  #
  # Description: ������к�
  #
  # Copyright: JavaHis (c) 
  #
  # @author luhai 2012.01.25
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;read;|;regist;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;clear;|;close


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

read.Type=TMenuItem
read.Text=����
read.Tip=����
read.M=P
read.Action=onRead
read.pic=042.gif

regist.Type=TMenuItem
regist.Text=����
regist.Tip=����
regist.M=E
regist.Action=onRegist
regist.pic=operation.gif