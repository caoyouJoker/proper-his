 #
  # Title: �ɹ��ƻ�
  #
  # Description:�ɹ��ƻ�
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2009-4-28
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;delete;|;query;|;clear;|;export;|;make;|;plan;|;analyse;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;delete;|;query;|;clear;|;export;|;make;|;plan;|;analyse;|;close

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

export.Type=TMenuItem
export.Text=����
export.Tip=����
execrpt.M=E
export.Action=onExport
export.pic=export.gif

make.Type=TMenuItem
make.Text=����
make.Tip=����
make.M=M
make.Action=onMake
make.pic=045.gif

plan.Type=TMenuItem
plan.Text=�ƻ���
plan.Tip=�ƻ���
plan.M=P
plan.Action=onPlan
plan.pic=047.gif

analyse.Type=TMenuItem
analyse.Text=�������
analyse.Tip=�������
analyse.M=A
analyse.Action=onAnalyse
analyse.pic=039.gif