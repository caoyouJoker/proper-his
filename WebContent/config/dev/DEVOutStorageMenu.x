 #
 # Title: �豸�������趨�趨
 #
 # Description:�豸�������趨�趨
 #
 # Copyright: JavaHis (c) 2008
 #
 # @author sundx
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;quary;clear;creatOutRequest;print;|;close

Window.Type=TMenu
Window.Text=���� 
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;quary;clear;creatOutRequest;print;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

quary.Type=TMenuItem
quary.Text=��ѯ
quary.Tip=��ѯ
quary.M=Q
quary.key=Ctrl+F
quary.Action=onQuery
quary.pic=query.gif


clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

creatOutRequest.Type=TMenuItem
creatOutRequest.Text=�����빺��
creatOutRequest.Tip=�����빺��
creatOutRequest.M=C
creatOutRequest.key=Ctrl+I
creatOutRequest.Action=onGenerateReceipt  
creatOutRequest.pic=039.gif


onReject.Type=TMenuItem
onReject.Text=����
onReject.Tip=����
onReject.M=r
onReject.key=Ctrl+R
onReject.Action=onReject
onReject.pic=Undo.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=P  
print.key=Ctrl+P 
print.Action=onPrint 
print.pic=print.gif 


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