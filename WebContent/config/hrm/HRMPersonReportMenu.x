 #
  # Title:健检个人报到
  #
  # Description:健检个人报到
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;delete;backReceipt;barCode;readCard;|;picture;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;delete;barCode;readCard;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSaveCom
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

disCharge.Type=TMenuItem
disCharge.Text=退费
disCharge.Tip=退费
disCharge.M=N
disCharge.key=Delete
disCharge.Action=onDisCharge
disCharge.pic=030.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

barCode.Type=TMenuItem
barCode.Text=打印条码
barCode.Tip=打印条码
barCode.M=C
barCode.Action=onBarCode
barCode.pic=barcode.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

backReceipt.Type=TMenuItem
backReceipt.Text=费用查询
backReceipt.Tip=费用查询
backReceipt.M=R
backReceipt.key=F7
backReceipt.Action=onBackReceipt
backReceipt.pic=detail-1.gif

creatCard.Type=TMenuItem
creatCard.Text=制卡
creatCard.Tip=制卡
creatCard.M=R
creatCard.key=F11
creatCard.Action=onCreatCard
creatCard.pic=card.gif

readCard.Type=TMenuItem
readCard.Text=读卡
readCard.Tip=读卡
readCard.M=R
readCard.key=F12
readCard.Action=onReadCard
readCard.pic=042.gif

reportSheet.Type=TMenuItem
reportSheet.Text=导览单
reportSheet.Tip=导览单
reportSheet.M=C
reportSheet.Action=onReportPrint
reportSheet.pic=print.gif

picture.Type=TMenuItem
picture.Text=二代身份证
picture.Tip=二代身份证
picture.M=
picture.key=
picture.Action=onIdCardNo
picture.pic=idcard.gif
