#
  # Title: ��ũ��סԺ�Ǽ��ϴ�
  #
  # Description:��ũ��סԺ�Ǽ��ϴ�
  #
  # Copyright: JavaHis (c) 2011
  #
  # @author pangben 2011-11-30
  # @version 2.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;admNClose;|;readCard;|;confirmQuery;|;eveinspat;|;InpRegisterSeek;|;cancelInpRegister;|;print;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;admNClose;|;readCard;|;confirmQuery;|;eveinspat;|;InpRegisterSeek;|;cancelInpRegister;|;print;|;clear;|;close

save.Type=TMenuItem
save.Text=סԺ�Ǽ��ϴ�
save.Tip=סԺ�Ǽ��ϴ�
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=Commit.gif

resvNClose.Type=TMenuItem
resvNClose.Text=ԤԼδ�᰸
resvNClose.Tip=ԤԼδ�᰸
resvNClose.M=N
resvNClose.Action=onResvNClose
resvNClose.pic=046.gif

readCard.Type=TMenuItem
readCard.Text=ת����������
readCard.Tip=ת����������
readCard.M=N
readCard.Action=onReadCard
readCard.pic=008.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

admNClose.Type=TMenuItem
admNClose.Text=סԺδ�᰸
admNClose.Tip=סԺδ�᰸
admNClose.Action=onAdmNClose
admNClose.pic=048.gif

confirmQuery.Type=TMenuItem
confirmQuery.Text=ת�������ѯ
confirmQuery.Tip=ת�������ѯ
confirmQuery.Action=onConfirmNo
confirmQuery.pic=043.gif

eveinspat.Type=TMenuItem
eveinspat.Text=סԺ�ǼǱ��ز�ѯ
eveinspat.Tip=סԺ�ǼǱ��ز�ѯ
eveinspat.Action=onEveInsPat
eveinspat.pic=035.gif

delayapp.Type=TMenuItem
delayapp.Text=�ӳ��걨
delayapp.Tip=�ӳ��걨
delayapp.Action=onDelayApp
delayapp.pic=011.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

cancelInpRegister.Type=TMenuItem
cancelInpRegister.Text=ȡ��סԺ�Ǽ�
cancelInpRegister.Tip=ȡ��סԺ�Ǽ�
cancelInpRegister.Action=onCancelInpRegister
cancelInpRegister.pic=011.gif

InpRegisterSeek.Type=TMenuItem
InpRegisterSeek.Text=סԺ�Ǽ����Ĳ�ѯ
InpRegisterSeek.Tip=סԺ�Ǽ����Ĳ�ѯ
InpRegisterSeek.Action=onInpRegisterSeek
InpRegisterSeek.pic=011.gif

print.Type=TMenuItem
print.Text=���㵥
print.Tip=���㵥
print.Action=onPrint
print.pic=print.gif