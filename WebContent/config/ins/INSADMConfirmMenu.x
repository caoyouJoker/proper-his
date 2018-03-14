#
  # Title: 资格确认书下载/开立
  #
  # Description:资格确认书下载/开立
  #
  # Copyright: JavaHis (c) 2011
  #
  # @author pangben 2011-11-30
  # @version 2.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;resvNClose;|;admNClose;|;readCard;|;readCardYD;|;confirmQuery;|;eveinspat;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;resvNClose;|;admNClose;|;readCard;|;readCardYD;|;confirmQuery;|;eveinspat;|;clear;|;close

save.Type=TMenuItem
save.Text=资格确认书下载/开立
save.Tip=资格确认书下载/开立
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=Commit.gif

resvNClose.Type=TMenuItem
resvNClose.Text=预约未结案
resvNClose.Tip=预约未结案
resvNClose.M=N
resvNClose.Action=onResvNClose
resvNClose.pic=046.gif

readCard.Type=TMenuItem
readCard.Text=刷卡
readCard.Tip=刷卡
readCard.M=N
readCard.Action=onReadCard
readCard.pic=008.gif

readCardYD.Type=TMenuItem
readCardYD.Text=异地刷卡
readCardYD.Tip=异地刷卡
readCardYD.M=N
readCardYD.Action=onReadCardYD
readCardYD.pic=008.gif


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
confirmQuery.Text=资格确认书历史记录查询
confirmQuery.Tip=资格确认书历史记录查询
confirmQuery.Action=onConfirmNo
confirmQuery.pic=043.gif

eveinspat.Type=TMenuItem
eveinspat.Text=跨年度医保患者
eveinspat.Tip=跨年度医保患者
eveinspat.Action=onEveInsPat
eveinspat.pic=035.gif


clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif