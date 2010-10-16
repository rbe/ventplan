rem
rem /Users/rbe/project/wac2/wac.bat
rem 
rem Copyright (C) 2010 Informationssysteme Ralf Bensmann.
rem Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
rem All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
rem 
rem Created by: rbe
rem

rem Progam Files, Program Files (x86)??
set OOO_HOME=C:\Program Files (x86)\OpenOffice.org 3
set OOO_PROGRAM=%OOO_HOME%\program

rem Java Classpath
set OOOJ=%OOO_HOME%\URE\java
set OOOU=%OOO_HOME%\Basis\program\classes
set OOOL=%OOO_HOME%\Basis\program;%OOO_HOME%\program\components;%OOO_HOME%\URE\bin

rem Set UNO path
set UNO_PATH=%OOO_PROGRAM%

rem Copy juh.jar into program folder
rem http://user.services.openoffice.org/en/forum/viewtopic.php?f=44&t=10825
copy "%OOOJ%\juh.jar" "%OOO_PROGRAM%"

rem Start
set CLASSPATH=lib;%OOO_PROGRAM%\juh.jar;%OOOJ%\jurt.jar;%OOOJ%\ridl.jar;%OOOU%\unoil.jar
cd lib
java -cp "%CLASSPATH%" -Djava.library.path="%OOOL%" -jar wac2.jar
