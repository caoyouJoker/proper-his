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
UI.button=save;quary;clear;addFile;generateReceipt;printReceipt;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W 
Window.Item=Refresh   
 
File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;quary;clear;addFile;generateReceipt;printReceipt;|;close

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

generateReceipt.Type=TMenuItem
generateReceipt.Text=������ⵥ
generateReceipt.Tip=������ⵥ 
generateReceipt.M=G
generateReceipt.key=Ctrl+G
generateReceipt.Action=onGenerateReceipt
generateReceipt.pic=037.gif

printReceipt.Type=TMenuItem
printReceipt.Text=��ӡ��ⵥ
printReceipt.Tip=��ӡ��ⵥ 
printReceipt.M=P
printReceipt.key=Ctrl+P
printReceipt.Action=onPrintReceipt 
printReceipt.pic=print.gif

barcodePrint.Type=TMenuItem
barcodePrint.Text=�����ӡ
barcodePrint.Tip=�����ӡ
barcodePrint.M=B
barcodePrint.key=Ctrl+B
barcodePrint.Action=onBarcodePrint
barcodePrint.pic=barcode.gif

printRFID.Type=TMenuItem
printRFID.Text=RFID�����ӡ
printRFID.Tip=RFID�����ӡ
printRFID.M=R
printRFID.key=Ctrl+R
printRFID.Action=onPrintRFID
printRFID.pic=012.gif


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