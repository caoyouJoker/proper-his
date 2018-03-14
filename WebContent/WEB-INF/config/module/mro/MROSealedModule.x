###############################
# <p>Title:²¡Àú·â´æ </p>
#
# <p>Description:²¡Àú·â´æ</p>
#
# <p>Copyright: Copyright (c) 2009</p>
#
# <p>Company:bluecore </p>
#
# @author huangtt 20161110
# @version 1.0
#
###############################

Module.item=updateEmrFileIndex;updateMroMreTech;updateMroMreTechSealedPrint;updateMroMreTechSealedProblem


updateEmrFileIndex.Type=TSQL
updateEmrFileIndex.SQL=UPDATE  EMR_FILE_INDEX SET SEALED_STATUS=<SEALED_STATUS> &
						WHERE CASE_NO=<CASE_NO> AND FILE_SEQ=<FILE_SEQ>
updateEmrFileIndex.Debug=N


updateMroMreTech.Type=TSQL
updateMroMreTech.SQL=UPDATE  MRO_MRV_TECH SET SEALED_STATUS=<SEALED_STATUS>, &
						SEALED_DATE=SYSDATE,SEALED_USER=<SEALED_USER> &
						,OPT_DATE=SYSDATE,OPT_USER=<OPT_USER>,OPT_TERM=<OPT_TERM> &
						WHERE CASE_NO=<CASE_NO>
updateMroMreTech.Debug=N

updateMroMreTechSealedPrint.Type=TSQL
updateMroMreTechSealedPrint.SQL=UPDATE  MRO_MRV_TECH SET  &
						SEALED_PRINT_DATE=SYSDATE,SEALED_PRINT_USER=<SEALED_PRINT_USER> &
						WHERE CASE_NO=<CASE_NO>
updateMroMreTechSealedPrint.Debug=N


updateMroMreTechSealedProblem.Type=TSQL
updateMroMreTechSealedProblem.SQL=UPDATE  MRO_MRV_TECH SET  &
						SEALED_PROBLEM=<SEALED_PROBLEM> &
						WHERE CASE_NO=<CASE_NO>
updateMroMreTechSealedProblem.Debug=N



