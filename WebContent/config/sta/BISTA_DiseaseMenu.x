<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;save;|;disExcel;|;BmsExcel;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;save;|;disExcel;|;BmsExcel;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

save.Type=TMenuItem
save.Text=����
save.Tip=����(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

disExcel.Type=TMenuItem
disExcel.Text=���벡������
disExcel.Tip=���벡������(Ctrl+D)
disExcel.M=D
disExcel.key=Ctrl+D
disExcel.Action=onDisExcel
disExcel.pic=045.gif

BmsExcel.Type=TMenuItem
BmsExcel.Text=������Ѫ����
BmsExcel.Tip=������Ѫ����(Ctrl+B)
BmsExcel.M=B
BmsExcel.key=Ctrl+D
BmsExcel.Action=onBmsExcel
BmsExcel.pic=export.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��(F5)
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif


close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
