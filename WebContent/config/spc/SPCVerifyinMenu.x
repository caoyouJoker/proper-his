 #
  # Title: ����������
  #
  # Description:����������
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2009-05-06
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;delete;|;query;|;clear;|;onExport;|;print;|;printcos;|;printnss;|;onExport2;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;delete;|;query;|;clear;|;onExport;|;print;|;printcos;|;printnss;|;onExport2;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��(Delete)
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

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

onExport.Type=TMenuItem
onExport.Text=���ö���
onExport.Tip=���ö���
onExport.M=E
onExport.Action=onExport
onExport.pic=045.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=P
print.Action=onPrint
print.pic=print.gif

printcos.Type=TMenuItem
printcos.Text=���ĲĴ�ӡ
printcos.Tip=���ĲĴ�ӡ
printcos.M=T
printcos.Action=onPrintcos
printcos.pic=print.gif

printnss.Type=TMenuItem
printnss.Text=��ʳ��ӡ
printnss.Tip=��ʳ��ӡ
printnss.M=U
printnss.Action=onPrintnss
printnss.pic=print.gif


onExport2.Type=TMenuItem
onExport2.Text=����
onExport2.Tip=����(Ctrl+A)
onExport2.M=A
onExport2.key=Ctrl+A
onExport2.Action=onExport2
onExport2.pic=export.gif