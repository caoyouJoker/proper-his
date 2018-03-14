#
  # Title: 转外就医登记下载/开立
  #
  # Description:转外就医登记下载/开立
  #
  # Copyright: JavaHis (c) 2011
  #
  # @author pangben 2011-11-30
  # @version 2.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;admNClose;|;print;|;readCard;|;confirmQuery;|;revoke;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;admNClose;|;print;|;readCard;|;confirmQuery;|;revoke;|;clear;|;close

save.Type=TMenuItem
save.Text=转外就医登记下载/开立
save.Tip=转外就医登记下载/开立
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=Commit.gif

revoke.Type=TMenuItem
revoke.Text=撤销
revoke.Tip=撤销
revoke.M=N
revoke.Action=onRevoke
revoke.pic=046.gif

readCard.Type=TMenuItem
readCard.Text=刷卡
readCard.Tip=刷卡
readCard.M=N
readCard.Action=onReadCard
readCard.pic=008.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

admNClose.Type=TMenuItem
admNClose.Text=住院未结案
admNClose.Tip=住院未结案
admNClose.Action=onAdmNClose
admNClose.pic=048.gif

confirmQuery.Type=TMenuItem
confirmQuery.Text=转外就医登记历史记录查询
confirmQuery.Tip=转外就医登记历史记录查询
confirmQuery.Action=onConfirmNo
confirmQuery.pic=043.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

print.Type=TMenuItem
print.Text=转外就医登记打印
print.Tip=转外就医登记打印
print.M=S
print.key=
print.Action=onPrint
print.pic=print.gif