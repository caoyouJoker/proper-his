 #
  # Title: 交叉配血
  #
  # Description: 交叉配血
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2009.09.22
 # @version 1.0
<Type=TMenuBar>  
UI.Item=File;Window
UI.button=save;|;delete;|;clear;|;stock;|;lis;|;ris;|;tnb;|;pay;|;dispense;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;delete;|;clear;|;stock;|;pay;|;dispense;|;close;|;lis;|;ris;|;tnb

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除(Delete)
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

stock.Type=TMenuItem
stock.Text=库存查询
stock.Tip=库存查询
stock.M=P
stock.Action=onStock
stock.pic=query.gif

pay.Type=TMenuItem
pay.Text=血液计价
pay.Tip=血液计价
pay.M=P
pay.Action=onPay
pay.pic=bill-1.gif

dispense.Type=TMenuItem
dispense.Text=血液出库
dispense.Tip=血液出库
dispense.M=P
dispense.Action=onDispense
dispense.pic=export.gif

lis.Type=TMenuItem
lis.Text=检验报告
lis.zhText=检验报告
lis.enText=检验报告
lis.Tip=检验报告
lis.zhTip=检验报告
lis.enTip=检验报告
lis.M=S
lis.Action=onLis
lis.pic=LIS.gif

ris.Type=TMenuItem
ris.Text=检查报告
ris.zhText=检查报告
ris.enText=检查报告
ris.Tip=检查报告
ris.zhTip=检查报告
ris.enTip=检查报告
ris.M=S
ris.Action=onRis
ris.pic=RIS.gif

tnb.Type=TMenuItem
tnb.Text=糖尿病
tnb.zhText=糖尿病
tnb.enText=糖尿病
tnb.Tip=糖尿病
tnb.zhTip=糖尿病
tnb.enTip=糖尿病
tnb.M=S
tnb.Action=onTnb
tnb.pic=modify.gif