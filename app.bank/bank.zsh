#!/bin/zsh

echo "*=========================="
echo "* Run the bank application."
echo "*=========================="

echo "*================================="
echo "* Set up the required directories."
echo "*================================="

ROOT_DIR=/Users/nevil/Projects/Banking.app
JAR_NAME=BankingApp.jar
DATA_DIR=/Users/nevil/OneDrive/Projects/data

echo "*============================================="
echo "* Invoke the program - may take a few moments."
echo "*============================================="

java -jar ${ROOT_DIR}/${JAR_NAME} --name=bank.application --dir=${DATA_DIR}