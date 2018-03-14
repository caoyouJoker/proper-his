#
# TBuilder Config File 
#
# Title:
#
# Company:JavaHis
#
# Author:ehui 2009.04.23
#
# version 1.0
#

<Type=TFrame>
UI.Title=
UI.MenuConfig=
UI.Width=400
UI.Height=300
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.sys.SysAllergyPopUp
UI.item=EDIT;TABLE;ALLERGY_TYPE
UI.layout=null
UI.X=0
UI.AutoX=N
UI.Y=0
UI.AutoY=N
UI.AutoHeight=N
UI.AutoWidth=N
UI.FocusList=EDIT
ALLERGY_TYPE.Type=TComboBox
ALLERGY_TYPE.X=338
ALLERGY_TYPE.Y=133
ALLERGY_TYPE.Width=81
ALLERGY_TYPE.Height=23
ALLERGY_TYPE.Text=TButton
ALLERGY_TYPE.showID=N
ALLERGY_TYPE.Editable=Y
ALLERGY_TYPE.ShowText=N
ALLERGY_TYPE.ShowName=Y
ALLERGY_TYPE.ExpandWidth=40
ALLERGY_TYPE.StringData=[[id,name],[PHA_INGREDIENT,过敏成分],[SYS_ALLERGYTYPE,其他过敏源]]
ALLERGY_TYPE.TableShowList=name
TABLE.Type=TTable
TABLE.X=2
TABLE.Y=24
TABLE.Width=310
TABLE.Height=268
TABLE.SpacingRow=1
TABLE.RowHeight=20
TABLE.AutoWidth=Y
TABLE.AutoHeight=Y
TABLE.Header=类别,80,ALLERGY_TYPE;过敏名称,200
TABLE.DoubleClickedAction=onDoubleClicked
TABLE.LockColumns=0,1
TABLE.Item=ALLERGY_TYPE
TABLE.ParmMap=GROUP_ID;CHN_DESC
TABLE.enHeader=Allergy Type;Allergens
TABLE.LanguageMap=CHN_DESC|ENG_DESC
EDIT.Type=TTextField
EDIT.X=2
EDIT.Y=3
EDIT.Width=384
EDIT.Height=20
EDIT.Text=
EDIT.FocusLostAction=grabFocus