<Type=TPanel>

UI.ControlClassName=com.tbuilder.work.WorkPanelControl
UI.Item=Design|Center
UI.Layout=BorderLayout
UI.X=10
UI.Y=10
UI.Width=200
UI.Height=200
UI.MenuConfig=%ROOT%\config\tbuilder\work\UIPanelMenu.x
UI.TopMenu=Y
UI.TopToolBar=Y

Design.type=TSplitPane
Design.dividerLocation=<i>100
Design.item=TComponentSelection|Left;DesignBack|Right


TComponentSelection.type=TTree
//TComponentSelection.bkcolor=白


DesignBack.type=TSplitPane
DesignBack.dividerLocation=<i>580
DesignBack.item=UIDesign|Left;UIProperty|Right


UIDesign.type=TScrollPane
UIDesign.ControlClassName=com.tbuilder.work.UIDesugbControl
//UIDesign.bkcolor=161,192,245

UIProperty.type=TTable
UIProperty.header=name,100;value,80
UIProperty.autoResizeMode=1
//UIProperty.value=[[x,0],[y,0],[width,0],[height,0]]
UIProperty.lockColumns=0
UIProperty.ColumnHorizontalAlignmentData=0,Left;1,右
//UIProperty.HorizontalAlignmentData=0,1,左;1,1,左
UIProperty.RowHeight=20
UIProperty.focusIndexJump=N
UIProperty.Item=HorizontalAlignmentComboBox;VerticalAlignmentComboBox;FocusTypeComboBox;AutoResizeModeComboBox;SelectionModeComboBox;MoveTypeComboBox

HorizontalAlignmentComboBox.Type=TComboBox
HorizontalAlignmentComboBox.Text=ComboBox
HorizontalAlignmentComboBox.VectorData=[[id,name,py1],[0,center,c],[2,left,l],[4,right,r]]
HorizontalAlignmentComboBox.ShowName=Y
HorizontalAlignmentComboBox.SelectedID=0
HorizontalAlignmentComboBox.Editable=true
HorizontalAlignmentComboBox.TableShowList=id,name

VerticalAlignmentComboBox.Type=TComboBox
VerticalAlignmentComboBox.Text=ComboBox
VerticalAlignmentComboBox.VectorData=[[id,name,py1],[0,center,c],[1,up,u],[3,bottom,r]]
VerticalAlignmentComboBox.ShowName=Y
VerticalAlignmentComboBox.SelectedID=0
VerticalAlignmentComboBox.Editable=true
VerticalAlignmentComboBox.TableShowList=id,name

FocusTypeComboBox.Type=TComboBox
FocusTypeComboBox.Text=ComboBox
FocusTypeComboBox.VectorData=[[id,name,py1],[0,none,n],[1,up,u],[2,right,r]]
FocusTypeComboBox.ShowName=Y
FocusTypeComboBox.SelectedID=0
FocusTypeComboBox.Editable=true
FocusTypeComboBox.TableShowList=id,name

AutoResizeModeComboBox.Type=TComboBox
AutoResizeModeComboBox.Text=ComboBox
AutoResizeModeComboBox.VectorData=[[id,name,py1],[0,AUTO_RESIZE_OFF,o],[1,AUTO_RESIZE_NEXT_COLUMN,n],[2,AUTO_RESIZE_SUBSEQUENT_COLUMNS,s],[3,AUTO_RESIZE_LAST_COLUMN,r],[4,AUTO_RESIZE_ALL_COLUMNS,a]]
AutoResizeModeComboBox.ShowName=Y
AutoResizeModeComboBox.SelectedID=0
AutoResizeModeComboBox.Editable=true
AutoResizeModeComboBox.TableShowList=id,name

SelectionModeComboBox.Type=TComboBox
SelectionModeComboBox.Text=ComboBox
SelectionModeComboBox.VectorData=[[id,name,py1],[0,SINGLE_SELECTION,s],[1,SINGLE_INTERVAL_SELECTION,i],[2,MULTIPLE_INTERVAL_SELECTION,m]]
SelectionModeComboBox.ShowName=Y
SelectionModeComboBox.SelectedID=0
SelectionModeComboBox.Editable=true
SelectionModeComboBox.TableShowList=id,name

MoveTypeComboBox.Type=TComboBox
MoveTypeComboBox.Text=ComboBox
MoveTypeComboBox.VectorData=[[id,name,py1],[0,NO_MOVE,n],[1,WIDTH_MOVE,w],[2,HEIGHT_MOVE,h],[3,MOVE,m]]
MoveTypeComboBox.ShowName=Y
MoveTypeComboBox.SelectedID=0
MoveTypeComboBox.Editable=true
MoveTypeComboBox.TableShowList=id,name

TComponentList=TButton;TLabel;TTextField;TNumberTextField;TDateField;TPasswordField;TCheckBox;TRadioButton;TComboBox;TPanel;TTable;TTree;TTabbedPane;&
		TLayoutAdapter;TMovePane;TDataWindow;TTextArea;TTextFormat;TDPanel;TRootPanel;TWord;TLanguageCombo
TComboList=系统管理;门诊;住院;账务;药房;其他

TComboList.系统管理=共用类别;医嘱类别;组织类别;药品类别;费用类别;人员类别;原因类别;其他类别


TComboList.系统管理.共用类别=提示信息下拉列表;门急住别下拉列表;转诊医院下拉列表;休假类别下拉列表;银行代码下拉列表;给号循环规则下拉列表;计量单位下拉列表
TComboList.系统管理.医嘱类别=中国精神障碍分类下拉列表;过敏类别下拉列表;病生理下拉列表;疾病报告卡类别下拉列表;饮食状态下拉列表;体温表病患动态下拉列表;&
			    体温变化特殊情况下拉列表;体温未量原因下拉列表;部位代码下拉列表;愈合等级下拉列表;医嘱分类下拉列表;体温种类下拉列表;护理等级;&
			    病情状态;用户自定义测量项目;麻醉方式;手术等级;手术室列表;手术类型;手术方法;排便标注
TComboList.系统管理.组织类别=收费柜台下拉列表;卫统科室下拉列表;位置字典下拉列表;医院等级下拉列表;床位等级下拉列表;床位状态下拉列表;科室归类下拉列表;&
			    科室等级下拉列表;省下拉列表;市下拉列表;分类规则下拉列表;区域下拉列表;科室下拉列表;床位;料位;科室下拉列表(处置专用);&
			    留观病区;静点区域;静点床位
TComboList.系统管理.药品类别=剂型大分类下拉列表;药品成分下拉列表;药品分类下拉列表;管制药品等级代码下拉列表;供应厂商下拉列表;途径下拉列表;频次下拉列表;&
			    抗生素等级;生产厂商;剂型;血液来源;血品;输血原因;血品规格;检验检查分类
TComboList.系统管理.费用类别=收据费用下拉列表;医保单位下拉列表;收据状态下拉列表;收据作废状态下拉列表;支付方式下拉列表
TComboList.系统管理.人员类别=宗教下拉列表;亲属关系下拉列表;婚姻下拉列表;职业下拉列表;职别下拉列表;血型下拉列表;种族下拉列表;教育程度下拉列表;语言下拉列表;&
			    国籍下拉列表;证照类别下拉列表;性别下拉列表;人员下拉列表
TComboList.系统管理.原因类别=DC 原因下拉列表;退药原因下拉列表;药库原因;请购类别下拉列表
TComboList.系统管理.其他类别=报告状态下拉列表;星期下拉列表;时段下拉列表;MDC项目下拉列表;打印机下拉列表;报告类别下拉列表;系统类别下拉列表;&
			    疾病诊断代码下拉列表;病案审核标准;病案审核项目;病案借阅原因;事故差错等级;事故差错原因;事故差错后果;事故差错类型;设备用途下拉列表;&
			    感染对死亡影响下拉区域;病原体种类



TComboList.门诊=代诊原因下拉列表;挂号费用类别下拉列表;付款途径下拉列表;就诊状态下拉列表;门急别下拉列表;挂号收费类别下拉列表;&
		     中西医标记下拉列表;号别下拉列表;诊室下拉列表;诊区下拉列表;门急诊适用科室下拉列表;门急诊适用人员下拉列表;&
		     门急诊适用诊室下拉列表;挂号方式下拉列表;检体下拉列表;煎药方式下拉列表;特殊煎法下拉列表;检伤等级下拉列表

TComboList.住院=取消预约住院原因下拉列表;动态记录字典(系统自定义)下拉列表;转归下拉列表;住院病患来源档下拉列表;入院状态下拉列表;住院处方号下拉列表;病区下拉列表;&
		病房下拉列表
TComboList.账务=收据类别下拉列表;票据状态下拉列表;资金来源下拉列表

TComboList.药房=药房下拉列表;麻精类别下拉列表;物联网药库下拉列表

TComboList.其他=统计费用下拉列表;首页费用下拉列表;院内费用下拉列表;身份折扣下拉列表;角色下拉列表;中间对照部门下拉列表;病历类别COMBO;病历样式COMBO;&
		病历调用类别COMBO;结构化病例申请单;KPI指标;评估方案;感染部位;感染对死亡的影响;病原体种类;点评类别下拉列表;处方点评问题代码下拉区域;&
                抗菌素限制类型;感染干预措施下拉菜单

TComboList.服务平台=BPEL流程类别COMBO






TTextFormatList=系统管理;门诊;住院;账务;药房;物联网;其他


TTextFormatList.系统管理=共用类别;医嘱类别;组织类别;药品类别;费用类别;人员类别;原因类别;其他类别


TTextFormatList.系统管理.共用类别=银行代码;提示信息;取号原则;分类规则;教育程度;休假类别;计量单位
TTextFormatList.系统管理.医嘱类别=检查项目;套餐模版;愈合等级;麻醉方式下拉区域;病生理;医嘱分类;手术诊断下拉区域;手术ICD下拉菜单;疾病诊断下拉菜单;泵入方式下拉菜单
TTextFormatList.系统管理.组织类别=床位类别;系统类别;科室;病区;诊间;诊区;医保单位;留观病区下拉区域;用户病区诊区;手术室;静点床位下拉区域;&
				 对照科室;床位下拉区域;市;科室(处置专用)下拉区域;位置;病房;省;物资部门下拉区域;设备科室下拉区域;留观床位下拉区域;&
				科室属性下拉区域;领药窗口下拉区域;成本中心下拉区域;记账单位下拉区域;适用科室(泰心专用);主管分类下拉区域;洗衣转发点
TTextFormatList.系统管理.药品类别=供应厂商;料位下拉区域;剂型下拉区域;频次;用法下拉区域;检验检查分类下拉区域;生产厂商下拉区域;药物分类下拉区域;&
				 药品属性分类下拉区域
TTextFormatList.系统管理.费用类别=统计费用
TTextFormatList.系统管理.人员类别=权限;亲属关系;种族;职业;国籍;胎儿沐浴方式;胎儿粪便状态;胎儿哺育方式;胎儿脐血;职别类型;语言;宗教;人员;行业类别下拉区域;&
				 职别下拉区域;适用人员(泰心专用);洗衣人员
TTextFormatList.系统管理.原因类别=代诊原因;物资原因下拉区域
TTextFormatList.系统管理.其他类别=MDC项目;传染病类别;疾病诊断代码;床位等级;设备种类下拉区域;设备属性下拉区域;购入途径下拉区域;绩效类别下拉区域;&
				 设备分类下拉区域;外挂动作下拉区域;感染部位下拉区域;物资种类下拉区域;物资料位下拉列表;标本名称;供应类别下拉区域;&
				 统一编码厂商下拉区域;编码同步字典下拉区域;煎药室下拉区域;隔离方式下拉区域;计量方式下拉区域;服务等级下拉区域;&
				 试管代号下拉区域;入厕方式下拉区域;健检结算方式下拉区域;试验药物下拉区域;饮食分类下拉区域;饮食套餐下拉区域;菜单禁忌下拉区域;&
				 餐次设置下拉区域;膳食菜单下拉区域;出生地下拉区域;包药机台号下拉区域;包药机类型下拉区域;短信状态分类下拉区域;设备资金来源下拉区域;医令管控病历下拉区域;设备财务分类下拉区域&
                                 会诊种类;被会医生;会诊取消原因;会诊原因;住院点评期间下拉区域





TTextFormatList.门诊=门诊适用诊室;号别;门诊适用科室;门诊适用人员;给号组别下拉区域;特殊号别下拉区域
TTextFormatList.住院=病区;离院方式下拉区域;入院病情下拉区域;住院科室分类下拉区域;住院重返等级下拉区域;外转院所下拉区域
TTextFormatList.账务=收据类别;首页费用;院内费用;支付方式下拉区域(门诊用)
TTextFormatList.药房=药房;药房(报表专用)下拉区域;物联网药库下拉列表
TTextFormatList.物联网=物联网药库下拉列表;物联网药库药房病区下拉列表;物联网智能柜下拉列表;物联网麻精默认入库下拉列表;物联网药库请领部门A下拉列表;物联网药库请领部门B下拉列表;物联网药库请领部门C下拉列表
TTextFormatList.其他=结构化病例申请单下拉区域;检测项目下拉区域;监测标准下拉区域;健康检查套餐下拉区域;健康检查团体下拉区域;健康检查合同下拉区域;&
		     健检顺序队列;健检打印属性下拉区域;查核类别下拉区域;临床路径项目下拉区域;临床路径执行人员下拉区域;适用临床路径下拉区域;适用临床路径(启用)下拉区域;&
		     评估代码下拉区域;临床路径变更原因下拉区域;临床路径进度状态下拉区域;护嘱分类下拉区域;临床路径时程下拉区域;病历模版元素;病历模版数据分类;&
		     临床路径溢出原因下拉区域;发卡原因下拉区域;支付方式下拉区域(医疗卡);申请原因下拉区域;病历模版主分类;病历子模版下拉区域;邮政编码下拉区域;知识库病历下拉区域;&
                     点评期间下拉区域;处方点评问题代码下拉区域;套餐类别;皮试结果;临床路径套餐字典;临床路径不进入原因字典;临床路径变异原因;临床路径变异类别;临床试验下拉菜单;感染来源下拉菜单;&
                     感染手术部位下拉菜单;切口分类下拉菜单



editDialog.TextArea=%ROOT%\config\tbuilder\edit\TextAreaEdit.x
InputParmDialogConfig=%ROOT%\config\tbuilder\dialog\InputParmDialog.x