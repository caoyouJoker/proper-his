#
  # Title: �����շ�
  #
  # Description:�����շѺͲ���Ƽ�
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author fudw
  # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
//kangy �ѿ���ԭ   UI.button=save;|;query;|;delete;|;record;|;backReceipt;|;ektCard;|;insCard;|;idCard;|;ektPrint;|;insPrint;|;operation;|;fee;|;clear;|;close;|;
UI.button=save;|;query;|;delete;|;record;|;backReceipt;|;ektCard;|;insCard;|;ektPrint;|;insPrint;|;operation;|;fee;|;clear;|;close;|;

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
//kangy �ѿ���ԭ  File.Item=save;|;query;|;delete;|;record;|;backReceipt;|;ektCard;|;insCard;|;idCard;|;ektPrint;|;insPrint;|;operation;|;fee;|;clear;|;close
File.Item=save;|;query;|;delete;|;record;|;backReceipt;|;ektCard;|;insCard;|;ektPrint;|;insPrint;|;operation;|;fee;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

backReceipt.Type=TMenuItem
backReceipt.Text=���ò�ѯ
backReceipt.Tip=���ò�ѯ
backReceipt.M=R
backReceipt.key=F7
backReceipt.Action=onBackReceipt
backReceipt.pic=detail-1.gif

backContract.Type=TMenuItem
backContract.Text=���˲�ѯ
backContract.Tip=���˲�ѯ
backContract.M=R
backContract.key=F8
backContract.Action=onBackContract
backContract.pic=011.gif

ektCard.Type=TMenuItem
ektCard.Text=��ҽ�ƿ�
ektCard.Tip=��ҽ�ƿ�
ektCard.M=
ektCard.key=F6
ektCard.Action=onEKT
ektCard.pic=042.gif

insCard.Type=TMenuItem
insCard.Text=��ҽ����
insCard.Tip=��ҽ����
insCard.M=
insCard.Action=readINSCard
insCard.pic=008.gif

//kangy  �ѿ���ԭ start
//idCard.Type=TMenuItem
//idCard.Text=�����֤
//idCard.Tip=�����֤
//idCard.M=
//idCard.Action=readIdCard
//idCard.pic=038.gif
//kangy �ѿ���ԭ  end

insPrint.Type=TMenuItem
insPrint.Text=�����ӡ
insPrint.Tip=�����ӡ
insPrint.M=
insPrint.Action=exeInsPrint
insPrint.pic=018.gif

ektPrint.Type=TMenuItem
ektPrint.Text=��Ʊ
ektPrint.Tip=��Ʊ
ektPrint.M=
ektPrint.key=F6
ektPrint.Action=onEKTPrint
ektPrint.pic=print.gif

fee.Type=TMenuItem
fee.Text=ҽ�ƿ���ֵ
fee.Tip=ҽ�ƿ���ֵ
fee.M=S
fee.key=Ctrl+S
fee.Action=onFee
fee.pic=bill.gif


clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

record.Type=TMenuItem
record.Text=�����
record.Tip=�����
record.M=R
record.key=F5
record.Action=onRecord
record.pic=012.gif

operation.Type=TMenuItem
operation.Text=�����ҼƷ�
operation.Tip=�����ҼƷ�
operation.M=P
operation.Action=onOperation
operation.pic=operation.gif
