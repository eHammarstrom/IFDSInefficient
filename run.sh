#!/bin/bash

cpJAVA=\
target/classes\
:src/main/resources/soot-3.1.0.jar

cpSOOT=\
target/classes\
:target/lib/rt-1.8.0.jar\
:target/lib/junit-3.8.2.jar\
:target/lib/jce-1.8.0.jar

mvn clean
mvn package
for filename in ./examples/*; do
    if [[ -f $filename ]]; then
        echo "----------------------------------------------"
        echo "Running for: $filename"
        echo "----------------------"
        java -cp target/classes:target/lib/* eda045f.exercises.MyMainClass -cp $cpSOOT -f jimple -process-dir $filename
        echo "----------------------------------------------"
    fi
done

# java -ea -cp target/classes:target/lib/* eda045f.exercises.MyMainClass -cp $cpSOOT -f j -process-dir ./examples/javacc-5.0.jar
# java -ea -cp target/classes:target/lib/* eda045f.exercises.MyMainClass -cp $cpSOOT -f j -process-dir ./examples/bouncycastle-160.jar
# java -ea -cp target/classes:target/lib/* eda045f.exercises.MyMainClass -cp $cpSOOT:./examples/javacc-5.0.jar -f j org.javacc.parser.LexGen
# java -ea -cp target/classes:target/lib/* eda045f.exercises.MyMainClass -cp $cpSOOT -f j eda045f.exercises.test.ArrayIndex
# java -ea -cp target/classes:target/lib/* eda045f.exercises.MyMainClass -cp $cpSOOT -f j eda045f.exercises.test.Foo
# java -cp target/DFAnalysis1-1.0-SNAPSHOT.jar:target/lib/* eda045f.exercises.MyMainClass -cp $cpSOOT -f j -p jb preserve-source-annotations eda045f.exercises.test.Foo
