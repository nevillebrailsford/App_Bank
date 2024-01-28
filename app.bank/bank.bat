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

set ROOT_DIR=C:\Users\nevil\Projects\Banking.app
set JAR_NAME=BankingApp.jar
set DATA_DIR=C:\Users\nevil\OneDrive\Projects\data

echo *=============================================
echo * Invoke the program - may take a few moments.
echo *=============================================

java.exe -jar %ROOT_DIR%\%JAR_NAME% --name=bank.application --dir=%DATA_DIR%

ENDLOCAL