 #
  # Title: 医疗卡充值操作
  #
  # Description: 医疗卡充值操作
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2011.09.28
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;checkAll;|;checkDetailAccnt;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;checkAll;|;checkDetailAccnt;|;clear;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.Action=onQuery
query.pic=query.gif

checkAll.Type=TMenuItem
checkAll.Text=对总账
checkAll.Tip=对总账
checkAll.M=E
checkAll.Action=onCheckAll
checkAll.pic=037.gif

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

checkDetailAccnt.Type=TMenuItem
checkDetailAccnt.Text=对明细账
checkDetailAccnt.Tip=对明细账
checkDetailAccnt.M=E
checkDetailAccnt.Action=onCheckDetailAccnt
checkDetailAccnt.pic=detail-1.gif