 #
  # Title: �������屨��
  #
  # Description:�������屨��
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;delete;|;openOrder;copyOrder;closeOrder;|;reportSheet;barCode;exaApply;medApply;|;batchAdd;batchDelete;|;singleOpt;|;idcard;|;printWristBands;|;excel;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;delete;|;openOrder;copyOrder;closeOrder;|;reportSheet;barCode;exaApply;|;batchAdd;batchDelete;|;singleOpt;|;pdf;|;excel;|;clear;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=ȡ������
delete.Tip=ȡ������
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=Undo.gif

reportSheet.Type=TMenuItem
reportSheet.Text=������
reportSheet.Tip=��ӡ������
reportSheet.M=C
reportSheet.Action=onReportPrint
reportSheet.pic=print.gif


barCode.Type=TMenuItem
barCode.Text=����
barCode.Tip=��ӡ����
barCode.M=C
barCode.Action=onBarCode
barCode.pic=barcode.gif

medApply.Type=TMenuItem
medApply.Text=���뵥
medApply.Tip=��ӡ���뵥
medApply.M=C
medApply.Action=onOpenExa
medApply.pic=RIS-1.gif

exaApply.Type=TMenuItem
exaApply.Text=������ӡ���뵥
exaApply.Tip=��ӡ���뵥
exaApply.M=C
exaApply.Action=onPrintExa
exaApply.pic=RIS.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

batchAdd.Type=TMenuItem
batchAdd.Text=��������
batchAdd.Tip=��������
batchAdd.M=
batchAdd.Action=batchAdd
batchAdd.pic=sta-1.gif

batchDelete.Type=TMenuItem
batchDelete.Text=����ɾ��
batchDelete.Tip=����ɾ��
batchDelete.M=
batchDelete.Action=batchDelete
batchDelete.pic=delete.gif

singleOpt.Type=TMenuItem
singleOpt.Text=���˲���
singleOpt.Tip=���˲���
singleOpt.M=
singleOpt.Action=onSingleOpt
singleOpt.pic=sta.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

openOrder.Type=TMenuItem
openOrder.Text=չ�������Ŀ
openOrder.Tip=չ�������Ŀ
openOrder.M=
openOrder.key=
openOrder.Action=onOpenOrder
openOrder.pic=046.gif

copyOrder.Type=TMenuItem
copyOrder.Text=���������Ŀ
copyOrder.Tip=���������Ŀ
copyOrder.M=
copyOrder.key=
copyOrder.Action=onCopyOrder
copyOrder.pic=exportword.gif

closeOrder.Type=TMenuItem
closeOrder.Text=ȡ��չ��
closeOrder.Tip=ȡ��չ��
closeOrder.Action=onCloseOrder
closeOrder.pic=032.gif

excel.Type=TMenuItem
excel.Text=���Excel
excel.Tip=���Excel
excel.M=S
excel.key=Ctrl+S
excel.Action=onExcel
excel.pic=export.gif

idcard.Type=TMenuItem
idcard.Text=�������֤
idcard.Tip=�������֤
idcard.M=M
idcard.Action=onIdCard
idcard.pic=038.gif

printWristBands.Type=TMenuItem
printWristBands.Text=��ӡ���
printWristBands.Tip=��ӡ���
printWristBands.Action=printWristBands
printWristBands.pic=print-1.gif

pdf.Type=TMenuItem
pdf.Text=��������
pdf.zhText=��������
pdf.enText=��������
pdf.Tip=��������
pdf.zhTip=��������
pdf.enTip=��������
pdf.M=X
pdf.Action=onSubmitPDF
pdf.pic=005.gif