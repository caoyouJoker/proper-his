#############################################
# <p>Title:���ʹ���Menu </p>
#
# <p>Description:���ʹ���Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author zhangh 2013.08.20
# @version 1.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;delete;|;clear;|;print;|;excel;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;query;|;delete;|;clear;|;print;|;excel;|;close


clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�ر�
close.Tip=�ر�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif


save.Type=TMenuItem
save.Text=����
save.Tip=����(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ(Ctrl+P)
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

excel.Type=TMenuItem
excel.Text=��Excel
excel.Tip=��Excel(Ctrl+E)
excel.M=E
excel.key=Ctrl+E
excel.Action=onExcel
excel.pic=exportexcel.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��(Ctrl+D)
delete.M=D
delete.key=Ctrl+D
delete.Action=onDelete
delete.pic=delete.gif