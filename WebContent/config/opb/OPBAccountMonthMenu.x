 #
  # Title: �����ս�
  #
  # Description:�����ս�
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author fudw
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;print;|;export;|;detailprint;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;print;|;export;|;detailprint;|;close

save.Type=TMenuItem
save.Text=�ս�
save.Tip=�ս�
save.M=S
save.key=Ctrl+S
save.Action=onUpdate
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif


query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
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
print.M=F
print.key=Ctrl+P
print.Action=onPrint
print.pic=Print.gif

export.Type=TMenuItem
export.Text=���
export.Tip=���
export.M=E
export.key=F4
export.Action=onExport
export.pic=exportexcel.gif


detailprint.Type=TMenuItem
detailprint.Text=ҽ����ϸ��ӡ
detailprint.Tip=ҽ����ϸ��ӡ
detailprint.M=F
detailprint.key=Ctrl+P
detailprint.Action=onDetailPrint
detailprint.pic=Print.gif