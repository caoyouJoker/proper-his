# 
#  Title:�������
# 
#  Description:�������
# 
#  Copyright: Copyright (c) Javahis 2015
# 
#  author wangl 2015.9.22
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=new;Query;tempsave;Wrist;clear;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=Refresh;clear;|;close

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

new.Type=TMenuItem
new.Text=����
new.Tip=����(Ctrl+N)
new.M=N
new.key=Ctrl+N
new.Action=onNew
new.pic=New.gif

Query.Type=TMenuItem
Query.Text=��ѯ
Query.Tip=��ѯ(Ctrl+Q)
Query.M=Q
Query.key=Ctrl+Q
Query.Action=onQuery
Query.pic=Query.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=E
clear.key=Ctrl+E
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�ر�
close.Tip=�ر�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

Wrist.Type=TMenuItem
Wrist.Text=����
Wrist.Tip=����
Wrist.M=W
Wrist.key=Ctrl+W
Wrist.Action=onWrist
Wrist.pic=print-1.gif

tempsave.Type=TMenuItem
tempsave.Text=�����޸�
tempsave.zhText=�����޸�
tempsave.enText=Pending
tempsave.Tip=�����޸�
tempsave.zhTip=�����޸�
tempsave.enTip=Pending
tempsave.M=T
tempsave.key=Ctrl+T
tempsave.Action=onUpdate
tempsave.pic=tempsave.gif
