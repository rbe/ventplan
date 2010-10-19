@echo off
rem
rem /Users/rbe/project/wac2/wac.bat
rem 
rem Copyright (C) 2010 Informationssysteme Ralf Bensmann.
rem Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
rem All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
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
copy "%OOOJ%\juh.jar" "%OOO_PROGRAM%" > nul

rem Start
set CLASSPATH=%OOO_PROGRAM%\juh.jar;%OOOJ%\jurt.jar;%OOOJ%\ridl.jar;%OOOU%\unoil.jar
java -cp "%CLASSPATH%" -Djava.library.path="%OOOL%" -jar lib\wac2.jar >wac2.log
