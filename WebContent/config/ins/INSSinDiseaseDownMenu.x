 #
  # Title: ��ͷ�����ָ������������Ϣ����
  #
  # Description: ��ͷ�����ָ������������Ϣ����
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author lim 2016.12.05
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;print;|;sinprint;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;print;|;sinprint;|;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.Action=onQuery
query.pic=query.gif

print.Type=TMenuItem
print.Text=���������
print.Tip=���������
print.M=P
print.key=Ctrl+P
print.Action=DataDown_zjkd_N
print.pic=print.gif

sinprint.Type=TMenuItem
sinprint.Text=���ֻ��ܱ�
sinprint.Tip=���ֻ��ܱ�
sinprint.M=P
sinprint.key=Ctrl+S
sinprint.Action=onSinPrint
sinprint.pic=print.gif

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
