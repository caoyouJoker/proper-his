#
# TBuilder Config File 
#
# Title:������ִ�в�ѯ
#
# Company:JavaHis
#
# Author:wangb 2016.11.01
#
# version 1.0
#

<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;clear;export;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;clear;export;close


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

export.Type=TMenuItem
export.Text=����Excel
export.Tip=����(Ctrl+O)
export.M=O
export.key=Ctrl+O
export.Action=onExport
export.pic=exportexcel.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
