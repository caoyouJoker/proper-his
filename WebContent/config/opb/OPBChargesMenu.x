#
  # Title: 门诊收费
  #
  # Description:门诊收费和补充计价
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author fudw
  # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
//kangy 脱卡还原   UI.button=save;|;query;|;delete;|;record;|;backReceipt;|;ektCard;|;insCard;|;idCard;|;ektPrint;|;insPrint;|;operation;|;fee;|;clear;|;close;|;
UI.button=save;|;query;|;delete;|;record;|;backReceipt;|;ektCard;|;insCard;|;ektPrint;|;insPrint;|;operation;|;fee;|;clear;|;close;|;

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
//kangy 脱卡还原  File.Item=save;|;query;|;delete;|;record;|;backReceipt;|;ektCard;|;insCard;|;idCard;|;ektPrint;|;insPrint;|;operation;|;fee;|;clear;|;close
File.Item=save;|;query;|;delete;|;record;|;backReceipt;|;ektCard;|;insCard;|;ektPrint;|;insPrint;|;operation;|;fee;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

backReceipt.Type=TMenuItem
backReceipt.Text=费用查询
backReceipt.Tip=费用查询
backReceipt.M=R
backReceipt.key=F7
backReceipt.Action=onBackReceipt
backReceipt.pic=detail-1.gif

backContract.Type=TMenuItem
backContract.Text=记账查询
backContract.Tip=记账查询
backContract.M=R
backContract.key=F8
backContract.Action=onBackContract
backContract.pic=011.gif

ektCard.Type=TMenuItem
ektCard.Text=读医疗卡
ektCard.Tip=读医疗卡
ektCard.M=
ektCard.key=F6
ektCard.Action=onEKT
ektCard.pic=042.gif

insCard.Type=TMenuItem
insCard.Text=读医保卡
insCard.Tip=读医保卡
insCard.M=
insCard.Action=readINSCard
insCard.pic=008.gif

//kangy  脱卡还原 start
//idCard.Type=TMenuItem
//idCard.Text=读身份证
//idCard.Tip=读身份证
//idCard.M=
//idCard.Action=readIdCard
//idCard.pic=038.gif
//kangy 脱卡还原  end

insPrint.Type=TMenuItem
insPrint.Text=结算打印
insPrint.Tip=结算打印
insPrint.M=
insPrint.Action=exeInsPrint
insPrint.pic=018.gif

ektPrint.Type=TMenuItem
ektPrint.Text=打票
ektPrint.Tip=打票
ektPrint.M=
ektPrint.key=F6
ektPrint.Action=onEKTPrint
ektPrint.pic=print.gif

fee.Type=TMenuItem
fee.Text=医疗卡充值
fee.Tip=医疗卡充值
fee.M=S
fee.key=Ctrl+S
fee.Action=onFee
fee.pic=bill.gif


clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

record.Type=TMenuItem
record.Text=就诊号
record.Tip=就诊号
record.M=R
record.key=F5
record.Action=onRecord
record.pic=012.gif

operation.Type=TMenuItem
operation.Text=手术室计费
operation.Tip=手术室计费
operation.M=P
operation.Action=onOperation
operation.pic=operation.gif
