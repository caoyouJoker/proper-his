#
  # Title: 新农合住院登记上传
  #
  # Description:新农合住院登记上传
  #
  # Copyright: JavaHis (c) 2011
  #
  # @author pangben 2011-11-30
  # @version 2.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;admNClose;|;readCard;|;confirmQuery;|;eveinspat;|;InpRegisterSeek;|;cancelInpRegister;|;print;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;admNClose;|;readCard;|;confirmQuery;|;eveinspat;|;InpRegisterSeek;|;cancelInpRegister;|;print;|;clear;|;close

save.Type=TMenuItem
save.Text=住院登记上传
save.Tip=住院登记上传
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
readCard.Text=转诊申请下载
readCard.Tip=转诊申请下载
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
confirmQuery.Text=转诊申请查询
confirmQuery.Tip=转诊申请查询
confirmQuery.Action=onConfirmNo
confirmQuery.pic=043.gif

eveinspat.Type=TMenuItem
eveinspat.Text=住院登记本地查询
eveinspat.Tip=住院登记本地查询
eveinspat.Action=onEveInsPat
eveinspat.pic=035.gif

delayapp.Type=TMenuItem
delayapp.Text=延迟申报
delayapp.Tip=延迟申报
delayapp.Action=onDelayApp
delayapp.pic=011.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

cancelInpRegister.Type=TMenuItem
cancelInpRegister.Text=取消住院登记
cancelInpRegister.Tip=取消住院登记
cancelInpRegister.Action=onCancelInpRegister
cancelInpRegister.pic=011.gif

InpRegisterSeek.Type=TMenuItem
InpRegisterSeek.Text=住院登记中心查询
InpRegisterSeek.Tip=住院登记中心查询
InpRegisterSeek.Action=onInpRegisterSeek
InpRegisterSeek.pic=011.gif

print.Type=TMenuItem
print.Text=结算单
print.Tip=结算单
print.Action=onPrint
print.pic=print.gif