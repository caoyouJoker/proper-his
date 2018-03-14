#
   # Title: 评估基本档
   #
   # Description:评估基本档
   #
   # Copyright: JavaHis (c) 2015
   #
   # @author huzc 2015.10.22

Module.item=insertINWAssessmentRead

//添加一行新数据到SYS_EVALUTION_DICT
insertINWAssessmentRead.Type=TSQL
insertINWAssessmentRead.SQL=INSERT INTO SYS_EVALUTION_DICT ( EVALUTION_CODE, EVALUTION_DESC, &  
                            SHORT_DESC, PY, EVALUTION_CLASS, LOGIC1, SCORE1, & 
                            SCORE_DESC, OPT_USER, OPT_DATE, OPT_TERM ) & 
                            VALUES (<EVALUTION_CODE>, <EVALUTION_DESC>, &  
                            <SHORT_DESC>, <PY>, <EVALUTION_CLASS>, <LOGIC1>, <SCORE1>, & 
                            <SCORE_DESC>, <OPT_USER>, <OPT_DATE>, <OPT_TERM> )
insertINWAssessmentRead.Debug=Y

