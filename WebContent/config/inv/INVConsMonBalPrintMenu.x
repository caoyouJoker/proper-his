##################################################
# <p>Title:���������½ᱨ��</p>
#
# <p>Description: ���������½ᱨ��</p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author huangtt 2013-05-03
# @version 1.0
##################################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;print;|;import;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;print;|;import;|;clear;|;close

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=X
clear.key=
clear.Action=onClear
clear.pic=clear.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=
print.key=
print.Action=onPrint
print.pic=print.gif

import.Type=TMenuItem
import.Text=����EXCEL
import.Tip=����EXCEL
import.M=Q
import.key=Ctrl+E
import.Action=onExport
import.pic=export.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif