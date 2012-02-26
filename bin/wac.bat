@echo off
rem
rem Copyright (C) 2005      Informationssysteme Ralf Bensmann.
rem Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
rem Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
rem 
rem Alle Rechte vorbehalten.
rem All rights reserved.
rem

rem Java 6
set JAVA_HOME=Windows\jre6
set PATH=%JAVA_HOME%\bin;%PATH%
set CLASSPATH=lib\*
rem Update
xcopy update\resources\* resources /e /c /i /f /r /y 1>%TEMP%\wac.log 2>&1
xcopy update\sql\* sql /e /c /i /f /r /y 1>>%TEMP%\wac.log 2>&1
xcopy update\lib\* lib /e /c /i /f /r /y 1>>%TEMP%\wac.log 2>&1
rmdir /s /q update 1>>%TEMP%\wac.log 2>&1
rem Start
start javaw -cp "%CLASSPATH%" griffon.swing.SwingApplication 1>>%TEMP%\wac.log 2>&1
stop
