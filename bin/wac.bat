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
copy "%OOOJ%\juh.jar" "%OOO_PROGRAM%" 1>nul 2>&1

rem Classpath
set CLASSPATH=%OOO_PROGRAM%\juh.jar;%OOOJ%\jurt.jar;%OOOJ%\ridl.jar;%OOOU%\unoil.jar;lib\Filters.jar;lib\MultipleGradientPaint.jar;lib\TaskDialog-1.1.0.jar;lib\balloontip_2009-01-2.jar;lib\commons-dbcp-1.2.2.jar;lib\commons-logging-1.1.jar;lib\commons-pool-1.5.3.jar;lib\glazedlists-1.8.0_java15.jar;lib\grails-spring-1.3.2.jar;lib\griffon-glazedlists-addon-0.4.jar;lib\griffon-gsql-addon-0.4.jar;lib\griffon-miglayout-addon-0.1.jar;lib\griffon-oxbow-addon-0.2.jar;lib\griffon-rt-0.9.jar;lib\griffon-spring-addon-0.3.jar;lib\griffon-tray-builder-addon-0.4.jar;lib\groovy-all-1.7.3.jar;lib\gsplash.jar;lib\h2-1.2.136.jar;lib\hsqldb-1.8.0.10.jar;lib\jide-oss-2.6.2.jar;lib\jidebuilder-2.2.jar;lib\juh.jar;lib\jurt.jar;lib\jut.jar;lib\l2fprod-common-all.jar;lib\mail.jar;lib\miglayout-3.7-swing.jar;lib\miglayout-3.7.3.1-swing.jar;lib\odisee.jar;lib\officebean.jar;lib\org.springframework.aop-3.0.3.RELEASE.jar;lib\org.springframework.asm-3.0.3.RELEASE.jar;lib\org.springframework.aspects-3.0.3.RELEASE.jar;lib\org.springframework.beans-3.0.3.RELEASE.jar;lib\org.springframework.context-3.0.3.RELEASE.jar;lib\org.springframework.context.support-3.0.3.RELEASE.jar;lib\org.springframework.core-3.0.3.RELEASE.jar;lib\org.springframework.expression-3.0.3.RELEASE.jar;lib\ridl.jar;lib\splash-0.1.4.jar;lib\svg-salamander-1.0.jar;lib\swing-worker-1.1.jar;lib\swingx-0.9.3.jar;lib\swingxbuilder-0.1.6.jar;lib\swingxtrasbuilder-0.1.jar;lib\timingframework-1.0.jar;lib\unoil.jar;lib\wac2.jar;lib\xswingx-0.2.jar

rem No OpenOffice running
taskkill /f /im soffice* 1>nul 2>&1

rem Update
xcopy update\conf\* conf /e /c /i /f /r /y 1>wac.log 2>&1
xcopy update\sql\* sql /e /c /i /f /r /y 1>wac.log 2>&1
xcopy update\lib\* lib /e /c /i /f /r /y 1>wac.log 2>&1
rmdir /s /q update 1>wac.log 2>&1

rem Start
java -cp "%CLASSPATH%" -Djava.library.path="%OOOL%" griffon.swing.SwingApplication 1>>wac.log 2>&1
