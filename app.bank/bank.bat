@echo off

rem this file is stored in the Property.app directory
rem and executes the property application.

echo *==========================
echo * Run the bank application.
echo *==========================

SETLOCAL

echo *=================================
echo * Set up the required directories.
echo *=================================

set ROOT_DIR=%cd%

set PARENT=..\
set PROJECTS_DIR=%ROOT_DIR%\%PARENT%
set FAMILY_DIR=%PROJECTS_DIR%%PARENT%

set DATA_DIR=%FAMILY_DIR%data

echo *=============================================
echo * Invoke the program - may take a few moments.
echo *=============================================

java.exe -jar %ROOT_DIR%\BankingApp.jar --name=bank.application --dir=%DATA_DIR%

ENDLOCAL