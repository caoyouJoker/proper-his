 #
  # Title: �����շ��˴β�ѯ
  #
  # Description:�����շ��˴β�ѯ
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author yanmm
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;print;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;query;print;|;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=N
query.key=F5
query.Action=onQuery
query.pic=query.gif


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

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=X
print.key=Alt+F4
print.Action=onPrint
print.pic=print.gif