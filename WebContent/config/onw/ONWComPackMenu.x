#############################################
# <p>Title:�������ȿ�ͷҽ���ײ�Menu </p>
#
# <p>Description:�������ȿ�ͷҽ���ײ�Menu </p>
#
# <p>Copyright: Copyright (c) 2017</p>
#
# <p>Company: Javahis</p>
#
# @author wangqing 2017.08.31
# @version 5.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;clear;|;close

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
File.Item=save;|;Refresh;|;query;|;clear;|;close

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.zhText=ˢ��
Refresh.enText=Refresh
Refresh.Tip=ˢ��
Refresh.zhTip=ˢ��
Refresh.enTip=Refresh
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=��ѯ
query.zhText=��ѯ
query.enText=Query
query.Tip=��ѯ
query.zhTip=��ѯ
query.enTip=Query
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

test.Type=TMenuItem
test.Text=����
test.Tip=����
test.M=
test.Action=onTest
test.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

