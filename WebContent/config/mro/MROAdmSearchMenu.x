#############################################
# <p>Title:��Ժ������Ϣ��ѯMenu </p>
#
# <p>Description:��Ժ������Ϣ��ѯMenu </p>
#
# <p>Copyright: Copyright (c) 2012</p>
#
# <p>Company: ProperSoft</p>
#
# @author pangben 2012.05.06
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;export|;import;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;export|;import;|;clear;|;close


query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

export.Type=TMenuItem
export.Text=���
export.Tip=���
export.M=E
export.key=F4
export.Action=onExport
export.pic=exportexcel.gif

import.Type=TMenuItem
import.Text=����
import.Tip=����
import.M=E
import.key=F5
import.Action=onDisImportExcel
import.pic=045.gif


clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

