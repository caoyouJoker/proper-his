 #
  # Title: �����ײ�
  #
  # Description:�����ײ�
  #
  # Copyright: JavaHis (c) 2013
  #
  # @author wangm
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;export;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=���
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

export.Type=TMenuItem
export.Text=Excel����
export.Tip=Excel����
export.M=Q
export.key=Ctrl+E
export.Action=onExcel
export.pic=export.gif

print.Type=TMenuItem
print.Text=��ӡ��ⵥ
print.Tip=��ӡ��ⵥ
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

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

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��(Delete)
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

new.Type=TMenuItem
new.Text=����
new.Tip=����
new.M=N
new.key=Ctrl+N
new.Action=onNew
new.pic=new.gif