<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;commit;|;deleteRow;|;delete;|;query;|;operation;|;clpOrderReSchdCode;|;clearTable;|;clear;|;close

Window.Type=TMenu
Window.Text=����                 
Window.M=W
Window.Item=Refresh                   
                                
File.Type=TMenu    
File.Text=�ļ�
File.M=F
File.Item=query;|;Refresh;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

commit.Type=TMenuItem
commit.Text=�ύ�Ʒ�
commit.Tip=�ύ�Ʒ�
commit.M=S
commit.key=Ctrl+S
commit.Action=onCommit
commit.pic=fee.gif

deleteRow.Type=TMenuItem
deleteRow.Text=ɾ����
deleteRow.Tip=ɾ����(Delete Row)
deleteRow.M=N
deleteRow.key=Delete
deleteRow.Action=onDeleteRow
deleteRow.pic=closebill-2.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��(Delete)
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

operation.Type=TMenuItem
operation.Text=�������Ʒ�
operation.Tip=�������Ʒ�
operation.M=P
operation.Action=onOperation
operation.pic=operation.gif  

clearTable.Type=TMenuItem
clearTable.Text=��ձ��
clearTable.Tip=��ձ��
clearTable.M=C
clearTable.Action=onClearTable
clearTable.pic=clear.gif

clear.Type=TMenuItem
clear.Text=ȫ�����
clear.Tip=ȫ�����
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

clpOrderQuote.Type=TMenuItem
clpOrderQuote.Text=����·��
clpOrderQuote.Tip=����·��
clpOrderQuote.M=
clpOrderQuote.Action=onAddCLNCPath
clpOrderQuote.pic=054.gif


clpOrderReSchdCode.Type=TMenuItem
clpOrderReSchdCode.Text=����ʱ���޸�
clpOrderReSchdCode.Tip=����ʱ���޸�
clpOrderReSchdCode.M=
clpOrderReSchdCode.Action=onClpOrderReSchdCode
clpOrderReSchdCode.pic=046.gif

close.Type=TMenuItem
close.Text=�˳�  
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif
