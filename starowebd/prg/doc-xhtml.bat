set JAVA_HOME=D:\programs\jdk8
set SOURCE_DIR=D:\#starohub\starowebd\src\main\java
set DOC_DIR=D:\#starohub\starowebd\doc\xhtml
set PACKAGE=jsx

%JAVA_HOME%\bin\javadoc.exe -public -d %DOC_DIR% -sourcepath %SOURCE_DIR% -subpackages %PACKAGE%

pause