 #
  # Title: 健检团体报到
  #
  # Description:健检团体报到
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;delete;|;openOrder;copyOrder;closeOrder;|;reportSheet;barCode;exaApply;medApply;|;batchAdd;batchDelete;|;singleOpt;|;idcard;|;printWristBands;|;excel;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;delete;|;openOrder;copyOrder;closeOrder;|;reportSheet;barCode;exaApply;|;batchAdd;batchDelete;|;singleOpt;|;pdf;|;excel;|;clear;close

save.Type=TMenuItem
save.Text=报到
save.Tip=报到
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=取消报到
delete.Tip=取消报到
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=Undo.gif

reportSheet.Type=TMenuItem
reportSheet.Text=导览单
reportSheet.Tip=打印导览单
reportSheet.M=C
reportSheet.Action=onReportPrint
reportSheet.pic=print.gif


barCode.Type=TMenuItem
barCode.Text=条码
barCode.Tip=打印条码
barCode.M=C
barCode.Action=onBarCode
barCode.pic=barcode.gif

medApply.Type=TMenuItem
medApply.Text=申请单
medApply.Tip=打印申请单
medApply.M=C
medApply.Action=onOpenExa
medApply.pic=RIS-1.gif

exaApply.Type=TMenuItem
exaApply.Text=批量打印申请单
exaApply.Tip=打印申请单
exaApply.M=C
exaApply.Action=onPrintExa
exaApply.pic=RIS.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

batchAdd.Type=TMenuItem
batchAdd.Text=批量新增
batchAdd.Tip=批量新增
batchAdd.M=
batchAdd.Action=batchAdd
batchAdd.pic=sta-1.gif

batchDelete.Type=TMenuItem
batchDelete.Text=批量删除
batchDelete.Tip=批量删除
batchDelete.M=
batchDelete.Action=batchDelete
batchDelete.pic=delete.gif

singleOpt.Type=TMenuItem
singleOpt.Text=单人操作
singleOpt.Tip=单人操作
singleOpt.M=
singleOpt.Action=onSingleOpt
singleOpt.pic=sta.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

openOrder.Type=TMenuItem
openOrder.Text=展开体检项目
openOrder.Tip=展开体检项目
openOrder.M=
openOrder.key=
openOrder.Action=onOpenOrder
openOrder.pic=046.gif

copyOrder.Type=TMenuItem
copyOrder.Text=复制体检项目
copyOrder.Tip=复制体检项目
copyOrder.M=
copyOrder.key=
copyOrder.Action=onCopyOrder
copyOrder.pic=exportword.gif

closeOrder.Type=TMenuItem
closeOrder.Text=取消展开
closeOrder.Tip=取消展开
closeOrder.Action=onCloseOrder
closeOrder.pic=032.gif

excel.Type=TMenuItem
excel.Text=汇出Excel
excel.Tip=汇出Excel
excel.M=S
excel.key=Ctrl+S
excel.Action=onExcel
excel.pic=export.gif

idcard.Type=TMenuItem
idcard.Text=二代身份证
idcard.Tip=二代身份证
idcard.M=M
idcard.Action=onIdCard
idcard.pic=038.gif

printWristBands.Type=TMenuItem
printWristBands.Text=打印腕带
printWristBands.Tip=打印腕带
printWristBands.Action=printWristBands
printWristBands.pic=print-1.gif

pdf.Type=TMenuItem
pdf.Text=病历整合
pdf.zhText=病历整合
pdf.enText=病历整合
pdf.Tip=病历整合
pdf.zhTip=病历整合
pdf.enTip=病历整合
pdf.M=X
pdf.Action=onSubmitPDF
pdf.pic=005.gif