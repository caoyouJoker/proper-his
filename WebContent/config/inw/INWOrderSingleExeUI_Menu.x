<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;sendRe;|;unExecQuery;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;delete;Refresh;query;|;unExecQuery;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=F2
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

sendRe.Type=TMenuItem
sendRe.Text=����
sendRe.Tip=����
sendRe.M=S
sendRe.key=Ctrl+S
sendRe.Action=onSendRe
sendRe.pic=008.gif

unExecQuery
unExecQuery.Type=TMenuItem
unExecQuery.Text=δִ��ҽ����ѯ
unExecQuery.Tip=δִ��ҽ����ѯ
unExecQuery.M=S
unExecQuery.key=Ctrl+S
unExecQuery.Action=onUnExecQuery
unExecQuery.pic=query.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif