@echo off
echo Compiling Java files...
javac -d bin ProducerConsumer.java
if %errorlevel% neq 0 (
    echo Compilation failed.
    pause
    exit /b
)

echo Creating JAR file...
jar cfm ProducerConsumer.jar manifest.txt -C bin .
if %errorlevel% neq 0 (
    echo JAR creation failed.
    pause
    exit /b
)

echo Running the program...
java -jar ProducerConsumer.jar %*
pause