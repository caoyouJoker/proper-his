#
# TBuilder Config File 
#
# Title:��������ļ��˵���
#
# Company:JavaHis
#
# Author:GUANGL 2016.06.08
#
# version 1.0
#

<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;query;delete;clear;|;import;export;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;query;delete;clear;|;import;export;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����(Save)
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

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��(Delete)
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

import.Type=TMenuItem
import.Text=������ļ��Ϣ
import.Tip=������ļ��Ϣ
import.M=Q
import.key=
import.Action=onImport
import.pic=002.gif

export.Type=TMenuItem
export.Text=������ļ��Ϣ
export.Tip=������ļ��Ϣ
export.M=S
export.key=Ctrl+S
export.Action=onExport
export.pic=export.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
