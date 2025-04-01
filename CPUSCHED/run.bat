@echo off
echo Compiling Java files...
javac -d bin src/src/*.java
if %errorlevel% neq 0 (
    echo Compilation failed.
    pause
    exit /b
)

echo Creating JAR file...
echo Main-Class: src.Main > manifest.txt
jar cfm CPUSCHED.jar manifest.txt -C bin .
if %errorlevel% neq 0 (
    echo JAR creation failed.
    pause
    exit /b
)

echo Running the program...
java -jar CPUSCHED.jar
pause