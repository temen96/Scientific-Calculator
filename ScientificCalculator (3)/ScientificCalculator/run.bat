@echo off
setlocal
cd /d %~dp0
if not exist out mkdir out
javac -encoding UTF-8 -d out src\ExpressionEvaluator.java src\Theme.java src\UnitConverterPanel.java src\ScientificCalculator.java
if errorlevel 1 (
  echo Compilation failed.
  exit /b 1
)
java -cp out ScientificCalculator

