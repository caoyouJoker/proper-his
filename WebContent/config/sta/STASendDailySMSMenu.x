# 
#  Title:�սᱨ����ŷ���ƽ̨menu
# 
#  Description:�սᱨ����ŷ���ƽ̨menu
# 
#  Copyright: Copyright (c) Javahis 2014
# 
#  author wangbin 2014.7.14
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=generate|;sms|;clear|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=generate|;sms|;clear|;close


generate.Type=TMenuItem
generate.Text=��������
generate.Tip=��������(Alt+G)
generate.M=G
generate.key=Alt+G
generate.Action=onGenerate
generate.pic=inscon.gif


sms.Type=TMenuItem
sms.Text=������
sms.Tip=������
sms.M=M
sms.key=Ctrl+M
sms.Action=onSendSMS
sms.pic=014.gif


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

