 #
  # Title: ����Ӫ��ȷ�ϲ˵�
  #
  # Description:
  #
  # Copyright: JavaHis (c) 2015
  #
  # @author wangb 2015.2.26
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;printReady;|;printHandOver;|;send;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;clear;|;printReady;|;printHandOver;|;send;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

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

printReady.Type=TMenuItem
printReady.Text=��ӡ�����嵥
printReady.Tip=��ӡ�����嵥(Ctrl+P)
printReady.M=P
printReady.key=Ctrl+P
printReady.Action=onPrintReady
printReady.pic=print.gif

printHandOver.Type=TMenuItem
printHandOver.Text=��ӡ���ӵ�
printHandOver.Tip=��ӡ���ӵ�(Ctrl+K)
printHandOver.M=K
printHandOver.key=Ctrl+K
printHandOver.Action=onPrintHandOver
printHandOver.pic=print_red.gif

send.Type=TMenuItem
send.Text=��ӡ�����ǩ
send.Tip=��ӡ�����ǩ(Ctrl+T)
send.M=
send.key=Ctrl+T
send.Action=onPrintENBarCode
send.pic=barcode.gif