 #
  # Title: 电生理叫号
  #
  # Description: 电生理叫号
  #
  # Copyright: BlueCore (c) 
  #
  # @author wanglong 2013.11.12
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;read;|;regist;|;unRegist;|;charge;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;clear;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

read.Type=TMenuItem
read.Text=医疗卡
read.Tip=医疗卡(Ctrl+C)
read.M=C
read.key=Ctrl+C
read.Action=onReadCard
read.pic=042.gif

regist.Type=TMenuItem
regist.Text=报到
regist.Tip=报到(Ctrl+R)
regist.M=G
regist.key=Ctrl+R
regist.Action=onRegist
regist.pic=017.gif

unRegist.Type=TMenuItem
unRegist.Text=取消报到
unRegist.Tip=取消报到(Ctrl+U)
unRegist.M=U
unRegist.key=Ctrl+U
unRegist.Action=onUnRegist
unRegist.pic=Undo.gif

charge.Type=TMenuItem
charge.Text=补充计价
charge.Tip=补充计价
charge.M=
charge.key=
charge.Action=onCharge
charge.pic=bill.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新(F5)
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=Z
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

