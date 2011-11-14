@echo off
rem
rem /Users/rbe/project/wac2/wac.bat
rem 
rem Copyright (C) 2010 Informationssysteme Ralf Bensmann.
rem Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
rem Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
rem 
rem Created by: rbe
rem

rem Java 6
set JAVA_HOME=Windows\jre6
set PATH=%JAVA_HOME%\bin;%PATH%

rem Use OpenOffice Portable -- batteries included.
set OOO_HOME=Windows\OpenOfficePortable\App\openoffice
set OOO_PROGRAM=%OOO_HOME%\program

rem Java Classpath
set OOOJ=%OOO_HOME%\URE\java
set OOOU=%OOO_HOME%\Basis\program\classes
set OOOL=%OOO_HOME%\Basis\program;%OOO_HOME%\program\components;%OOO_HOME%\URE\bin

rem Set UNO path
set UNO_PATH=%OOO_PROGRAM%

rem Copy juh.jar into program folder
rem http://user.services.openoffice.org/en/forum/viewtopic.php?f=44&t=10825
copy "%OOOJ%\juh.jar" "%OOO_PROGRAM%" 1>nul 2>&1

rem Classpath
set CLASSPATH=%OOO_PROGRAM%\juh.jar;%OOOJ%\jurt.jar;%OOOJ%\ridl.jar;%OOOU%\unoil.jar;lib\*

rem No OpenOffice running
rem taskkill /f /im soffice* 1>nul 2>&1

rem Update
xcopy update\conf\* conf /e /c /i /f /r /y 1>wac.log 2>&1
xcopy update\lib\* lib /e /c /i /f /r /y 1>wac.log 2>&1
rmdir /s /q update 1>wac.log 2>&1

rem Start
java -cp "%CLASSPATH%" -Djava.library.path="%OOOL%" griffon.swing.SwingApplication 1>>wac.log 2>&1
