#############################################
# <p>Title:���ʹ���Menu </p>
#
# <p>Description:���ʹ���Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author zhangh 2013.05.3
# @version 1.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;printUp;|;printDown;|;excelUp;|;excelDown;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;print;|;excel;|;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

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

printUp.Type=TMenuItem
printUp.Text=��ӡ�ϱ�
printUp.Tip=��ӡ�ϱ�
printUp.M=P
printUp.key=Ctrl+P
printUp.Action=onPrintUp
printUp.pic=print.gif

printDown.Type=TMenuItem
printDown.Text=��ӡ�±�
printDown.Tip=��ӡ�±�
printDown.M=P
printDown.key=Ctrl+P
printDown.Action=onPrintDown
printDown.pic=print-1.gif

excelUp.Type=TMenuItem
excelUp.Text=���ϱ�Excel
excelUp.Tip=���ϱ�Excel
excelUp.M=E
excelUp.key=Ctrl+E
excelUp.Action=onExeclUp
excelUp.pic=exportexcel.gif

excelDown.Type=TMenuItem
excelDown.Text=���±�Excel
excelDown.Tip=���±�Excel
excelDown.M=E
excelDown.key=Ctrl+E
excelDown.Action=onExeclDown
excelDown.pic=export.gif