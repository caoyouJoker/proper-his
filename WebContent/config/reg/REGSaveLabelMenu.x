# 
#  Title:�������ȼ�¼
# 
#  Description:�������ȼ�¼
# 
#  Copyright: Copyright (c) Javahis 2015
# 
#  author WangQing 20170327
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=Query;|;save;|;Refresh;|;clear;|;print;|;sign;|;cancelSign;|;order;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=new;save;Refresh;clear;|;close

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onResets
Refresh.pic=Refresh.gif

new.Type=TMenuItem
new.Text=����
new.Tip=����
new.M=A
new.key=Ctrl+A
new.Action=onNew
new.pic=new.gif

break.Type=TMenuItem
break.Text=ˢ��
break.Tip=ˢ��
break.M=A
break.Action=onBreak
break.pic=tempsave.gif

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

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

Submit.Type=TMenuItem
Submit.Text=�ύ
Submit.Tip=�ύ
Submit.M=W
Submit.Action=onWrist
Submit.pic=015.gif

Wrist.Type=TMenuItem
Wrist.Text=����
Wrist.Tip=����
Wrist.M=W
Wrist.key=Ctrl+W
Wrist.Action=onWrist
Wrist.pic=print-1.gif


print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=P
print.Action=onPrint
print.pic=print.gif

Query.Type=TMenuItem
Query.Text=��ѯ
Query.Tip=��ѯ
Query.M=X
Query.key=Alt+F4
Query.Action=onQuery
Query.pic=Query.gif

sign.Type=TMenuItem
sign.Text=��ʿǩ��
sign.Tip=��ʿǩ��
sign.M=R
sign.key=
sign.Action=onSign
sign.pic=Refresh.gif

cancelSign.Type=TMenuItem
cancelSign.Text=ȡ��ǩ��
cancelSign.Tip=ȡ��ǩ��
cancelSign.M=
cancelSign.key=
cancelSign.Action=onCancelSign
cancelSign.pic=Refresh.gif

order.Type=TMenuItem
order.Text=��ͷҽ��
order.Tip=��ͷҽ��
order.M=
order.key=
order.Action=onOrder
order.pic=Refresh.gif


