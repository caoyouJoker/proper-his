#############################################
# <p>Title:门诊护士站检验检查报告进度Menu </p>
#
# <p>Description:门诊护士站检验检查报告进度Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2010.02.02
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File
UI.button=close

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=close

close.Type=TMenuItem
close.Text=关闭
close.Tip=关闭
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif