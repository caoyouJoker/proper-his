#############################################
# <p>Title:���ҽʦ�����ʽ��ϸMenu </p>
#
# <p>Description:���ҽʦ�����ʽ��ϸMenu </p>
#
# <p>Copyright: Copyright (c) 2012</p>
#
# <p>Company: bluecore</p>
#
# @author wangb 2014.07.24
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;export|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.enText=Window
Window.enTip=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;export|;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

export.Type=TMenuItem
export.Text=����Excel
export.Tip=����Excel
export.M=E
export.key=Ctrl+E
export.Action=onExport
export.pic=export.gif

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

