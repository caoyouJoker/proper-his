 #
  # Title: 健检结算
  #
  # Description:健检结算
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;delete;print;|;printBill;|;export;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;delete;print;|;printBill;|;export;|;clear;|;close

save.Type=TMenuItem
save.Text=结算
save.Tip=结算
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=取消结算
delete.Tip=取消结算
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

print.Type=TMenuItem
print.Text=打印账单明细
print.Tip=打印账单明细
print.M=S
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.GIF

printBill.Type=TMenuItem
printBill.Text=打印缴费通知单
printBill.Tip=打印缴费通知单
printBill.M=B
printBill.key=Ctrl+B
printBill.Action=onPrintBill
printBill.pic=pha_print.GIF

export.Type=TMenuItem
export.Text=汇出
export.Tip=汇出
export.M=E
export.key=F4
export.Action=onExport
export.pic=exportexcel.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif


