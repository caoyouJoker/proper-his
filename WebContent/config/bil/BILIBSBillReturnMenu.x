<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;clear;|;returnFee;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;clear;returnFee;|;Refresh;|;close

save.Type=TMenuItem
save.Text=��ѯ
save.Tip=��ѯ
save.M=S
save.key=Ctrl+F
save.Action=onQuery
save.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif



clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

returnFee.Type=TMenuItem
returnFee.Text=�����˵�
returnFee.Tip=�����˵�
returnFee.M=P
returnFee.Action=onReturnFee
returnFee.pic=030.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

