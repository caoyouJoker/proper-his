#
# TBuilder Config File 
#
# Title:��������ļ��˵���
#
# Company:JavaHis
#
# Author:wangb 2016.07.04
#
# version 1.0
#

<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;return;clear;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;return;clear;close

return.Type=TMenuItem
return.Text=����
return.Tip=����(Ctrl+R)
return.M=
return.key=Ctrl+R
return.Action=onReturn
return.pic=054.gif

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
