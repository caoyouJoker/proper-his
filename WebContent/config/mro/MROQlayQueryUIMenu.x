#############################################
# <p>Title:�Զ��ʿ�Menu </p>
#
# <p>Description:�Զ��ʿ�Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangY 2011.05.16
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;clear;|;close

execute.Type=TMenuItem
execute.Text=�ʿ�
execute.Tip=�ʿ�
save.M=S
execute.key=
execute.Action=onQlayControlAction
execute.pic=execute.gif

board.Type=TMenuItem
board.Text=����������
board.Tip=����������
board.M=N
board.key=
board.Action=onBoardMessage
board.pic=040.gif

email.Type=TMenuItem
email.Text=�����ʼ�
email.Tip=�����ʼ�
email.M=N
email.key=
email.Action=onSendMessage
email.pic=emr-1.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��(F5)
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
