 #
  # Title: ����������
  #
  # Description:����������
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2009-05-06
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;updateData;|;delete;|;query;|;clear;|;export;|;toExcel1;|printNo;|;print;|;printBarcode;|;printBarcodeBatch;|;printBarcodeAll;|;printList;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;updateData;|;delete;|;query;|;clear;|;export;|;toExcel1;|printNo;|;print;|;printBarcode;|;printBarcodeBatch;|;printBarcodeAll;|;printList;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����(Ctrl+S)
save.M=S   
save.key=Ctrl+S
save.Action=onSave  
save.pic=save.gif
updateData


updateData.Type=TMenuItem  
updateData.Text=Ч��
updateData.Tip=Ч��(Ctrl+P)
updateData.M=S
updateData.key=Ctrl+P
updateData.Action=onChangeData
updateData.pic=save.gif



delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��(Delete)
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
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

export.Type=TMenuItem
export.Text=���ö���
export.Tip=���ö���
execrpt.M=E
export.Action=onExport
export.pic=045.gif


toExcel1.Type=TMenuItem
toExcel1.Text=�������ʵ�
toExcel1.Tip=�������ʵ�
toExcel1.zhTip=�������ʵ�
toExcel1.enTip=�������ʵ�
toExcel1.M=S
toExcel1.Action=onImpExcel
toExcel1.pic=export.gif  

printNo.Type=TMenuItem
printNo.Text=��ӡ����
printNo.Tip=��ӡ����
printNo.M=P
printNo.Action=onPrint
printNo.pic=print.gif

printList.Type=TMenuItem
printList.Text=��ӡ��ⵥ
printList.Tip=��ӡ��ⵥ
printList.M=P
printList.Action=onPrintList
printList.pic=print.gif

print.Type=TMenuItem
print.Text=�����ӡ
print.Tip=�����ӡ
print.M=P
print.Action=onPrintBarcode
print.pic=print.gif
print.Type=TMenuItem
print.Text=�����ӡ
print.Tip=�����ӡ
print.M=P
print.Action=onPrintBarcode
print.pic=print.gif


printBarcode.Type=TMenuItem
printBarcode.Text=����
printBarcode.Tip=����
printBarcode.M=P
printBarcode.Action=onAddRfid
printBarcode.pic=barcode.gif

printBarcodeBatch.Type=TMenuItem
printBarcodeBatch.Text=����(����)
printBarcodeBatch.Tip=����(����)
printBarcodeBatch.M=P
printBarcodeBatch.Action=onAddRfidBatch
printBarcodeBatch.pic=barcode.gif


printBarcodeAll.Type=TMenuItem  
printBarcodeAll.Text=�����ӡ(����)
printBarcodeAll.Tip=�����ӡ(����)
printBarcodeAll.M=A
printBarcodeAll.Action=onPrintBarcodeAll
printBarcodeAll.pic=print_red.gif

