 #
  # Title: 备血申请单查询
  #
  # Description: 备血申请单查询
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2009.04.22
 # @version 1.0
<Type=TMenuBar>  
UI.Item=File;Window
UI.button=query;|;clear;|;apply;|;check;|;cross;|;out;|;feeDetail;|;qxResult;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;clear;|;apply;|;check;|;cross;|;out;|;feeDetail;|;qxResult;|;close

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

apply.Type=TMenuItem
apply.Text=备血申请单
apply.Tip=备血申请单
apply.M=P
apply.Action=onApply
apply.pic=046.gif

check.Type=TMenuItem
check.Text=检验记录
check.Tip=检验记录
check.M=P
check.Action=onCheck
check.pic=search-2.gif

cross.Type=TMenuItem
cross.Text=交叉配血
cross.Tip=交叉配血
cross.M=P
cross.Action=onCross
cross.pic=tempsave.gif

out.Type=TMenuItem
out.Text=血液出库
out.Tip=血液出库
out.M=P
out.Action=onOut
out.pic=export.gif

feeDetail.Type=TMenuItem
feeDetail.Text=血费明细
feeDetail.Tip=血费明细
feeDetail.M=P
feeDetail.Action=onFeeDetail
feeDetail.pic=convert.gif

qxResult.Type=TMenuItem
qxResult.Text=取血单
qxResult.Tip=取血单
qxResult.M=Q
qxResult.Action=onQXResult
qxResult.pic=051.gif