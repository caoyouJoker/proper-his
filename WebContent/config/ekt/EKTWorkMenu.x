 #
  # Title: ҽ�ƿ��ƿ�����
  #
  # Description: ҽ�ƿ��ƿ�����
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2011.09.28
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
// kangy  �ѿ���ԭ     start 
//UI.button=save;|;newsave;|;new;|;bankSave;|;query;|;renew;|;cardprint;|;MIcard;|;EKTcard;|;bankCard;|;idcard;|;updateEKTpwd;|;clear;|;close
UI.button=save;|;new;|;bankSave;|;query;|;renew;|;cardprint;|;MIcard;|;EKTcard;|;bankCard;|;idcard;|;updateEKTpwd;|;clear;|;close
// kangy  �ѿ���ԭ     end
Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
// kangy �ѿ���ԭ  start
//File.Item=save;|;newsave;|;bankSave;|;query;|;renew;|;cardprint;|;MIcard;|;EKTcard;|;bankCard;|;idcard;|;updateEKTpwd;|;clear;|;close
File.Item=save;|;bankSave;|;query;|;renew;|;cardprint;|;MIcard;|;EKTcard;|;bankCard;|;idcard;|;updateEKTpwd;|;clear;|;close
// kangy �ѿ���ԭ  end

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif
// kangy  �ѿ���ԭ    start
//save.Type=TMenuItem
//save.Text=����ʵ�忨
//save.Tip=����ʵ�忨(Ctrl+S)
//save.M=S
//save.key=Ctrl+S
//save.Action=onSaveST
//save.pic=save.gif

//newsave.Type=TMenuItem
//newsave.Text=�������⿨
//newsave.Tip=�������⿨
//newsave.M=
//newsave.key=
//newsave.Action=onSaveXN
//newsave.pic=save.gif

save.Type=TMenuItem
save.Text=����
save.Tip=����(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif
// kangy  �ѿ���ԭ    end

renew.Type=TMenuItem
renew.Text=��д��
renew.Tip=��д��
renew.M=R
renew.key=F5
renew.Action=onRenew
renew.pic=idcard.gif

cardprint.Type=TMenuItem
cardprint.Text=��Ƭ��ӡ
cardprint.Tip=��Ƭ��ӡ
cardprint.M=D
cardprint.key=Ctrl+D
cardprint.Action=onPrint
cardprint.pic=print.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

MIcard.Type=TMenuItem
MIcard.Text=ҽ����
MIcard.Tip=ҽ����
MIcard.M=M
MIcard.Action=onMRcard
MIcard.pic=008.gif

EKTcard.Type=TMenuItem
EKTcard.Text=ҽ�ƿ�
EKTcard.Tip=ҽ�ƿ�
EKTcard.M=E
EKTcard.Action=onEKTcard
EKTcard.pic=042.gif

idcard.Type=TMenuItem
idcard.Text=�������֤
idcard.Tip=�������֤
idcard.M=M
idcard.Action=onIdCard
idcard.pic=038.gif

updateEKTpwd.Type=TMenuItem
updateEKTpwd.Text=ҽ�ƿ��޸�����
updateEKTpwd.Tip=ҽ�ƿ��޸�����
updateEKTpwd.M=U
updateEKTpwd.Action=updateEKTPwd
updateEKTpwd.pic=007.gif

bankCard.Type=TMenuItem
bankCard.Text=���п�
bankCard.Tip=���п�
bankCard.M=M
bankCard.Action=onBankCard
bankCard.pic=bank.gif

bankSave.Type=TMenuItem
bankSave.Text=���п�����
bankSave.Tip=���п�����
bankSave.M=B
bankSave.Action=onBankSave
bankSave.pic=convert.gif
