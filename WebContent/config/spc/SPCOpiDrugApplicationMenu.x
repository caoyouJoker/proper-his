 #
  # Title: ���������龫��ҩ����Menu
  #
  # Description:���������龫��ҩ����Menu
  #
  # Copyright: ProperSoft (c) 2008
  #
  # @author shendr 2013.07.29
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;clear;|;print;|;printD;|;exportD;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;query;|;clear;|;print;|;printD;|;exportD;|;close

save.Type=TMenuItem
save.Text=�������쵥
save.Tip=�������쵥
save.M=C
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

print.Type=TMenuItem
print.Text=���ܴ�ӡ
print.Tip=���ܴ�ӡ
print.M=P
print.Action=onPrint
print.pic=print.gif

printD.Type=TMenuItem
printD.Text=��ϸ��ӡ
printD.Tip=��ϸ��ӡ
printD.M=P
printD.Action=onPrintD
printD.pic=print.gif

exportD.Type=TMenuItem
exportD.Text=��ϸ���
exportD.Tip=��ϸ���
exportD.M=P
exportD.Action=onExportD
exportD.pic=045.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif