#############################################
# <p>Title:������¼Menu </p>
#
# <p>Description:������¼Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2009.09.28
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=Query;|;opstmp;|;print;|;export;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.zhText=����
Window.enText=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.zhText=�ļ�
File.enText=File
File.M=F
File.Item=query;|;opstmp;|;print;|;export;|;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.zhText=��ѯ
query.enText=Query
query.Tip=��ѯ(Ctrl+F)
query.zhTip=����(Ctrl+F)
query.enTip=Save(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.zhText=ˢ��
Refresh.enText=Refresh
Refresh.Tip=ˢ��(F5)
Refresh.zhTip=ˢ��
Refresh.enTip=Refresh
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=���
clear.zhText=���
clear.enText=Empty
clear.Tip=���(Ctrl+Z)
clear.zhTip=���
clear.enTip=Empty
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.zhText=�˳�
close.enText=Quit
close.Tip=�˳�(Alt+F4)
close.zhTip=�˳�
close.enTip=Quit
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

opstmp.Type=TMenuItem
opstmp.Text=�޸���Ϣ
opstmp.zhText=�޸���Ϣ
opstmp.enText=Operation Template
opstmp.Tip=�޸���Ϣ
opstmp.zhTip=�޸���Ϣ
opstmp.enTip=Operation Template
opstmp.Action=onChange
opstmp.pic=new.gif

export.Type=TMenuItem
export.Text=���
export.Tip=���
export.M=E
export.key=F4
export.Action=onExport
export.pic=exportexcel.gif


print.Type=TMenuItem
print.Text=סԺ֤��ӡ
print.Tip=סԺ֤��ӡ
print.M=
print.key=
print.Action=onPrint
print.pic=print.gif