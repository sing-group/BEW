@echo off

set CURRENT_PATH=%~dp0
set R_HOME=%CURRENT_PATH%\R\App\R-Portable
set JRI_LIB_PATH=%CURRENT_PATH%\R\App\R-Portable\library\rJava\jri\i386
set R_SHARE_DIR=%R_HOME%\share
set R_INCLUDE_DIR=%R_HOME%\include
set R_DOC_DIR=%R_HOME%\doc
set R_LIBS=%R_HOME%\library

rem Include R DLLs in PATH.
set PATH=%R_HOME%\bin\i386;%PATH%
set PATH=%JRI_LIB_PATH%;%PATH%

echo Running BEW
IF EXIST jre7_32\bin\java.exe (
	echo Running with custom JRE...
	jre7_32\bin\java.exe -Djava.library.path="%JRI_LIB_PATH%" -Dswing.defaultlaf=javax.swing.plaf.nimbus.NimbusLookAndFeel -Duser.language=en -jar ./lib/aibench.jar ./plugins_bin
	) ELSE (
	echo ATTENTION: JRE is no in the BEW folder. Trying to execute with system JRE...
	java.exe -Djava.library.path="%JRI_LIB_PATH%" -Dswing.defaultlaf=javax.swing.plaf.nimbus.NimbusLookAndFeel -Duser.language=en -jar ./lib/aibench.jar ./plugins_bin
)