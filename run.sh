#!/bin/bash

cpSOOT=\
target/test-classes\
:examples/javacc-5.0.jar
# :target/lib/rt-1.8.0.jar\
# :target/lib/junit-3.8.2.jar\
# :target/lib/jce-1.8.0.jar\
# :target/lib
echo "SOOT CP: $cpSOOT"

# mvn clean
mvn compile
# for filename in ./examples/*; do
#     if [[ -f $filename ]]; then
#         echo "----------------------------------------------"
#         echo "Running for: $filename"
#         echo "----------------------"
#         java -cp target/classes:target/lib/* eda045f.exercises.MyMainClass -cp $cpSOOT -f jimple -process-dir $filename
#         echo "----------------------------------------------"
#     fi
# done

# java -ea -cp target/classes:target/lib/* eda045f.exercises.MyMainClass -cp $cpSOOT -f j -process-dir ./examples/javacc-5.0.jar
java -cp target/classes:target/lib/* eda045f.exercises.Stub2 $cpSOOT eda045f.exercises.Test1 
# java -cp target/classes:target/lib/* eda045f.exercises.Stub2 $cpSOOT org.javacc.parser.Main
# java -ea -cp target/classes:target/lib/* eda045f.exercises.MyMainClass -cp $cpSOOT -f j -process-dir ./examples/bouncycastle-160.jar
# java -ea -cp target/classes:target/lib/* eda045f.exercises.MyMainClass -cp $cpSOOT:./examples/javacc-5.0.jar -f j org.javacc.parser.LexGen
# java -ea -cp target/classes:target/lib/* eda045f.exercises.MyMainClass -cp $cpSOOT -f j eda045f.exercises.test.ArrayIndex
# java -ea -cp target/classes:target/lib/* eda045f.exercises.MyMainClass -cp $cpSOOT -f j eda045f.exercises.test.Foo
# java -cp target/DFAnalysis1-1.0-SNAPSHOT.jar:target/lib/* eda045f.exercises.MyMainClass -cp $cpSOOT -f j -p jb preserve-source-annotations eda045f.exercises.test.Foo
