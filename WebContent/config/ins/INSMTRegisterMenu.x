#
  # Title: 门特登记 审核查询
  #
  # Description:门特登记 \审核:门特登记开具或下载 \审核查询
  #
  # Copyright: ProperSoft (c) 2011
  #
  # @author pangben 2012-1-13
  # @version 2.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;readCard;command;|;loadDown;|;update;|;share;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;readCard;|;clear;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+Q
query.Action=onQuery
query.pic=query.gif

readCard.Type=TMenuItem
readCard.Text=读卡
readCard.Tip=读卡
readCard.M=R
readCard.Action=onReadCard
readCard.pic=042.gif

command.Type=TMenuItem
command.Text=开具
command.Tip=开具
command.M=S
command.Action=onCommandButSave
command.pic=convert.gif

loadDown.Type=TMenuItem
loadDown.Text=下载
loadDown.Tip=下载
loadDown.M=D
loadDown.Action=onLoadDown
loadDown.pic=031.gif

update.Type=TMenuItem
update.Text=变更
update.Tip=变更
update.M=U
update.Action=onUpdate
update.pic=046.gif

share.Type=TMenuItem
share.Text=共享信息
share.Tip=共享信息
share.M=D
share.Action=onShare
share.pic=detail-1.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif