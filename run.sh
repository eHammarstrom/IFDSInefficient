#!/bin/bash

cpSOOT=\
target/test-classes\
:examples/javacc-5.0.jar\
:examples/bcel-6.2.jar\
:examples/xalan-2.7.2.jar\
:target/lib/junit-3.8.2.jar

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
# jjdoc:jjtree:javacc:org.javacc.jjtree.Main:org.javacc.utils.JavaFileGenerator:org.javacc.jjdoc.JJDocMain:org.javacc.parser.Main
# java -ea -cp target/classes:target/lib/* eda045f.exercises.MyMainClass -cp $cpSOOT -f j -process-dir ./examples/javacc-5.0.jar
# java -cp target/classes:target/lib/* eda045f.exercises.Stub2 $cpSOOT eda045f.exercises.Test1
# java -cp target/classes:target/lib/* eda045f.exercises.Stub2 $cpSOOT org.javacc.jjtree.Main:javacc:org.javacc.parser.Main

echo "-------------------------------------------------------"
echo "Running Custom Tests"
echo "-------------------------------------------------------"
java -cp target/classes:target/lib/* eda045f.exercises.Stub2 $cpSOOT eda045f.exercises.Test1:eda045f.exercises.Test2
echo "-------------------------------------------------------"

echo "-------------------------------------------------------"
echo "Running JAVACC Tests"
echo "-------------------------------------------------------"
java -cp target/classes:target/lib/* eda045f.exercises.Stub2 $cpSOOT jjdoc:jjtree:javacc:org.javacc.jjtree.Main:org.javacc.utils.JavaFileGenerator:org.javacc.jjdoc.JJDocMain:org.javacc.parser.Main
echo "-------------------------------------------------------"

echo "-------------------------------------------------------"
echo "Running BCEL"
echo "-------------------------------------------------------"
java -cp target/classes:target/lib/* eda045f.exercises.Stub2 $cpSOOT org.apache.bcel.util.Class2HTML:org.apache.bcel.util.JavaWrapper:org.apache.bcel.util.BCELifier:org.apache.bcel.verifier.TransitiveHull:org.apache.bcel.verifier.Verifier:org.apache.bcel.verifier.NativeVerifier:org.apache.bcel.verifier.GraphicalVerifier:org.apache.bcel.verifier.VerifyDialog:org.apache.bcel.verifier.exc.AssertionViolatedException
echo "-------------------------------------------------------"

echo "-------------------------------------------------------"
echo "Running XALAN Tests"
echo "-------------------------------------------------------"
java -cp target/classes:target/lib/* eda045f.exercises.Stub2 $cpSOOT java_cup.Main:org.apache.xml.dtm.ref.IncrementalSAXSource_Xerces:org.apache.xml.dtm.ref.DTMSafeStringPool:org.apache.xml.dtm.ref.DTMStringPool:org.apache.xml.serializer.Version:org.apache.xml.resolver.apps.resolver:org.apache.xml.resolver.apps.xparse:org.apache.xml.resolver.apps.xread:org.apache.xml.resolver.Version:org.apache.xml.resolver.tests.BasicResolverTests:org.apache.xmlcommons.Version:org.apache.xerces.impl.xpath.regex.REUtil:org.apache.xerces.impl.xpath.XPath:org.apache.xerces.impl.Version:org.apache.xerces.impl.Constants:org.apache.xalan.xslt.EnvironmentCheck:org.apache.xalan.xslt.Process:org.apache.xalan.xsltc.util.JavaCupRedirect:org.apache.xalan.xsltc.cmdline.Compile:org.apache.xalan.xsltc.cmdline.Transform:org.apache.xalan.xsltc.ProcessorVersion:org.apache.xalan.lib.sql.ObjectArray:org.apache.xalan.processor.XSLProcessorVersion:org.apache.xalan.Version
echo "-------------------------------------------------------"

# :javacc:org.javacc.jjtree.Main:org.javacc.utils.JavaFileGenerator:org.javacc.jjdoc.JJDocMain:org.javacc.parser.Main
# java -cp target/classes:target/lib/* eda045f.exercises.Stub2 $cpSOOT eda045f.exercises.Test2
# java -cp target/classes:target/lib/* eda045f.exercises.Stub2 $cpSOOT eda045f.exercises.Test1:eda045f.exercises.Test2
# java -cp target/classes:target/lib/* eda045f.exercises.Stub2 $cpSOOT org.javacc.parser.Main
# java -ea -cp target/classes:target/lib/* eda045f.exercises.MyMainClass -cp $cpSOOT -f j -process-dir ./examples/bouncycastle-160.jar
# java -ea -cp target/classes:target/lib/* eda045f.exercises.MyMainClass -cp $cpSOOT:./examples/javacc-5.0.jar -f j org.javacc.parser.LexGen
# java -ea -cp target/classes:target/lib/* eda045f.exercises.MyMainClass -cp $cpSOOT -f j eda045f.exercises.test.ArrayIndex
# java -ea -cp target/classes:target/lib/* eda045f.exercises.MyMainClass -cp $cpSOOT -f j eda045f.exercises.test.Foo
# java -cp target/DFAnalysis1-1.0-SNAPSHOT.jar:target/lib/* eda045f.exercises.MyMainClass -cp $cpSOOT -f j -p jb preserve-source-annotations eda045f.exercises.test.Foo
