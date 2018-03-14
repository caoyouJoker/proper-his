 #
  # Title: 健检缴费
  #
  # Description:健检缴费
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=delete;print;prePay;|;receiptDetail;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=delete;print;prePay;|;receiptDetail;|;clear;|;close

delete.Type=TMenuItem
delete.Text=退费
delete.Tip=退费
delete.M=C
delete.Action=onDelete
delete.pic=030.gif

print.Type=TMenuItem
print.Text=补印
print.Tip=补印
print.M=C
print.Action=onRePrint
print.pic=print_red.gif

prePay.Type=TMenuItem
prePay.Text=预交金
prePay.Tip=预交金
prePay.M=N
prePay.key=Delete
prePay.Action=onPrePay
prePay.pic=fee.GIF

receiptDetail.Type=TMenuItem
receiptDetail.Text=费用查询
receiptDetail.Tip=费用查询
receiptDetail.M=R
receiptDetail.key=F7
receiptDetail.Action=onReceiptDetail
receiptDetail.pic=detail-1.gif

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

fee.Type=TMenuItem
fee.Text=收费
fee.Tip=收费
fee.M=S
fee.key=Ctrl+S
fee.Action=onFee
fee.pic=emr-1.gif

charge.Type=TMenuItem
charge.Text=打票
charge.Tip=打票
charge.M=S
charge.key=Ctrl+S
charge.Action=onCharge
charge.pic=emr-2.gif