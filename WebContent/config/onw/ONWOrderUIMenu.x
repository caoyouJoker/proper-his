#############################################
# <p>Title:急诊抢救口头医嘱套餐Menu </p>
#
# <p>Description:急诊抢救口头医嘱套餐Menu </p>
#
# <p>Copyright: Copyright (c) 2017</p>
#
# <p>Company: Javahis</p>
#
# @author wangqing 2017.08.31
# @version 5.0
#############################################
<Type=TMenuBar>
UI.button=sign;|;cancelSign;close

sign.Type=TMenuItem
sign.Text=医生签字
sign.zhText=医生签字
sign.enText=sign
sign.Tip=医生签字
sign.zhTip=医生签字
sign.enTip=sign
sign.M=
sign.key=
sign.Action=onSign
sign.pic=039.gif

cancelSign.Type=TMenuItem
cancelSign.Text=取消签字
cancelSign.zhText=取消签字
cancelSign.enText=cancelSign
cancelSign.Tip=取消签字
cancelSign.zhTip=取消签字
cancelSign.enTip=cancelSign
cancelSign.M=
cancelSign.key=
cancelSign.Action=onCancelSign
cancelSign.pic=039.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
