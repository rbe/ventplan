' ###########################################
' # installfont.vbs                         #
' # installs font file in windows fonts dir #
' # (c) 2007 by Gottfried Mayer             #
' ###########################################
 
Dim oFSO, oApp, oFolderCopy, oShell
Dim strFontsPath, strScriptPath
 
' Create needed Objects
Set oFSO = CreateObject("Scripting.FileSystemObject")
Set oShell = CreateObject("WScript.Shell")
Set oApp = CreateObject("Shell.Application")
 
' Get Path of this Script (i.E. C:\Scripts\ or \\server\netlogon\)
strScriptPath = WScript.ScriptFullName
strScriptPath = left(strScriptPath,instrrev(strScriptPath,"\"))
 
' Get Path of Windows Fonts directory
strFontsPath = oShell.ExpandEnvironmentStrings("%WINDIR%") & "\Fonts"
 
' Get Folder Object of Fonts directory (i.E. C:\Scripts\FontsToInstall or \\server\netlogon\FontsToInstall)
Set oFolderCopy = oApp.Namespace(strScriptPath & "fonts")
 
' Check each Font if it already is installed
For Each oFont In oFolderCopy.Items
  If NOT oFSO.FileExists(strFontsPath & "\" & oFont.Name) Then
    ' Tell Explorer to copy the Font - this correctly installs it.
    oApp.Namespace(strFontsPath).CopyHere oFont
  End If
Next
 
' Cleanup Objects
Set oFolderCopy = Nothing
Set oApp = Nothing
Set oShell = Nothing 