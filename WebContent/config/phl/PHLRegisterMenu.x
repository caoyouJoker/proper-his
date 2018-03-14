 #
  # Title: 静点室报道
  #
  # Description: 静点室报道
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2009.05.07
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;clear;|;card;|;bill;|;print1;|;print2;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;query;|;clear;|;card;|;bill;|;print1;|;print2;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

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

print1.Type=TMenuItem
print1.Text=瓶签打印
print1.Tip=瓶签打印
print1.M=P
print1.Action=onBottlePrint
print1.pic=print.gif

card.Type=TMenuItem
card.Text=床头卡
card.Tip=床头卡
card.M=P
card.Action=onCard
card.pic=card.gif

bill.Type=TMenuItem
bill.Text=补充计价
bill.Tip=补充计价
bill.M=P
bill.Action=onBill
bill.pic=bill.gif

print2.Type=TMenuItem
print2.Text=床/座打印
print2.Tip=床/座打印
print2.M=P
print2.Action=onBedPrint
print2.pic=pha_print.gif

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif
