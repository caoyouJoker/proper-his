 #
  # Title: 医疗卡制卡操作
  #
  # Description: 医疗卡制卡操作
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2011.09.28
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
// kangy  脱卡还原     start 
//UI.button=save;|;newsave;|;new;|;bankSave;|;query;|;renew;|;cardprint;|;MIcard;|;EKTcard;|;bankCard;|;idcard;|;updateEKTpwd;|;clear;|;close
UI.button=save;|;new;|;bankSave;|;query;|;renew;|;cardprint;|;MIcard;|;EKTcard;|;bankCard;|;idcard;|;updateEKTpwd;|;clear;|;close
// kangy  脱卡还原     end
Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
// kangy 脱卡还原  start
//File.Item=save;|;newsave;|;bankSave;|;query;|;renew;|;cardprint;|;MIcard;|;EKTcard;|;bankCard;|;idcard;|;updateEKTpwd;|;clear;|;close
File.Item=save;|;bankSave;|;query;|;renew;|;cardprint;|;MIcard;|;EKTcard;|;bankCard;|;idcard;|;updateEKTpwd;|;clear;|;close
// kangy 脱卡还原  end

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif
// kangy  脱卡还原    start
//save.Type=TMenuItem
//save.Text=购卡实体卡
//save.Tip=购卡实体卡(Ctrl+S)
//save.M=S
//save.key=Ctrl+S
//save.Action=onSaveST
//save.pic=save.gif

//newsave.Type=TMenuItem
//newsave.Text=购卡虚拟卡
//newsave.Tip=购卡虚拟卡
//newsave.M=
//newsave.key=
//newsave.Action=onSaveXN
//newsave.pic=save.gif

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif
// kangy  脱卡还原    end

renew.Type=TMenuItem
renew.Text=补写卡
renew.Tip=补写卡
renew.M=R
renew.key=F5
renew.Action=onRenew
renew.pic=idcard.gif

cardprint.Type=TMenuItem
cardprint.Text=卡片打印
cardprint.Tip=卡片打印
cardprint.M=D
cardprint.key=Ctrl+D
cardprint.Action=onPrint
cardprint.pic=print.gif

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

MIcard.Type=TMenuItem
MIcard.Text=医保卡
MIcard.Tip=医保卡
MIcard.M=M
MIcard.Action=onMRcard
MIcard.pic=008.gif

EKTcard.Type=TMenuItem
EKTcard.Text=医疗卡
EKTcard.Tip=医疗卡
EKTcard.M=E
EKTcard.Action=onEKTcard
EKTcard.pic=042.gif

idcard.Type=TMenuItem
idcard.Text=二代身份证
idcard.Tip=二代身份证
idcard.M=M
idcard.Action=onIdCard
idcard.pic=038.gif

updateEKTpwd.Type=TMenuItem
updateEKTpwd.Text=医疗卡修改密码
updateEKTpwd.Tip=医疗卡修改密码
updateEKTpwd.M=U
updateEKTpwd.Action=updateEKTPwd
updateEKTpwd.pic=007.gif

bankCard.Type=TMenuItem
bankCard.Text=银行卡
bankCard.Tip=银行卡
bankCard.M=M
bankCard.Action=onBankCard
bankCard.pic=bank.gif

bankSave.Type=TMenuItem
bankSave.Text=银行卡关联
bankSave.Tip=银行卡关联
bankSave.M=B
bankSave.Action=onBankSave
bankSave.pic=convert.gif
