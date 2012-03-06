@echo off
rem
rem VentPlan
rem
rem Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann.
rem Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
rem 
rem Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
rem All rights reserved. Use is subject to license terms.
rem

rem Java 6
rem set JAVA_HOME=..\Windows\jre6
rem set PATH=%JAVA_HOME%\bin;%PATH%
rem Update
xcopy ..\update\resources\* resources /e /c /i /f /r /y 1>%TEMP%\VentPlan.log 2>&1
xcopy ..\update\sql\* ..\sql /e /c /i /f /r /y 1>>%TEMP%\VentPlan.log 2>&1
xcopy ..\update\lib\* ..\lib /e /c /i /f /r /y 1>>%TEMP%\VentPlan.log 2>&1
rmdir /s /q ..\update 1>>%TEMP%\VentPlan.log 2>&1
rem Start
set CLASSPATH=..\lib\*
start javaw -cp "%CLASSPATH%" griffon.swing.SwingApplication 1>>%TEMP%\VentPlan.log 2>&1
stop