 #
  # Title: 肠内营养确认菜单
  #
  # Description:
  #
  # Copyright: JavaHis (c) 2015
  #
  # @author wangb 2015.2.26
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;printReady;|;printHandOver;|;send;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;clear;|;printReady;|;printHandOver;|;send;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

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

printReady.Type=TMenuItem
printReady.Text=打印备料清单
printReady.Tip=打印备料清单(Ctrl+P)
printReady.M=P
printReady.key=Ctrl+P
printReady.Action=onPrintReady
printReady.pic=print.gif

printHandOver.Type=TMenuItem
printHandOver.Text=打印交接单
printHandOver.Tip=打印交接单(Ctrl+K)
printHandOver.M=K
printHandOver.key=Ctrl+K
printHandOver.Action=onPrintHandOver
printHandOver.pic=print_red.gif

send.Type=TMenuItem
send.Text=打印条码标签
send.Tip=打印条码标签(Ctrl+T)
send.M=
send.key=Ctrl+T
send.Action=onPrintENBarCode
send.pic=barcode.gif