Module.item=getDept

//查询物资科室数据GYSUsed
getDept.Type=TSQL
getDept.SQL=SELECT A.ORG_CODE,B.DEPT_CHN_DESC,B.DEPT_ABS_DESC,B.PY1 &
              FROM INV_ORG A,SYS_DEPT B &
              WHERE A.ORG_CODE=B.DEPT_CODE AND B.ACTIVE_FLG <> 'N'
getDept.Debug=N





