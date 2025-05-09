#!/bin/bash

##
## Update the *.s-model and *.ref file contents
##
#
#for source_file in ./tests/tema3/*.cl; do
#	echo "Running with `basename $source_file`"
#	java -jar Tema3.jar $source_file > ./tests/tema3/`basename $source_file .cl`.s-model
#	if [ "$source_file" = "./tests/tema3/32-big.cl" ]; then 
#		echo 5 | spim -exception_file trap.handler.nogc -file tests/tema3/`basename $source_file .cl`.s-model > tests/tema3/`basename $source_file .cl`.ref
#	else
#		spim -exception_file trap.handler.nogc -file tests/tema3/`basename $source_file .cl`.s-model > tests/tema3/`basename $source_file .cl`.ref
#	fi
#done

#/usr/local/lib/antlr4.jar

#CLASSPATH=.:./antlr-4.13.0-complete.jar
CLASSPATH=./out/production/Tema2/

#for source_file in ./tests/tema3/01*.cl; do

passed=0
for source_file in ./tests/tema3/*.cl; do
	echo -e "\n`basename $source_file`"

	#java -cp $CLASSPATH cool.compiler.Compiler $source_file > ./tests/tema3/`basename $source_file .cl`.s
    java -cp "antlr-4.13.0-complete.jar:./out/production/Tema2/" cool.compiler.Compiler $source_file > ./tests/tema3/`basename $source_file .cl`.s

	if [ "$source_file" = "./tests/tema3/32-big.cl" ]; then
		echo 5 | spim -exception_file trap.handler.nogc -file tests/tema3/`basename $source_file .cl`.s > tests/tema3/`basename $source_file .cl`.out
	else
		spim -exception_file trap.handler.nogc -file tests/tema3/`basename $source_file .cl`.s > tests/tema3/`basename $source_file .cl`.out
	fi

	diff tests/tema3/`basename $source_file .cl`.ref tests/tema3/`basename $source_file .cl`.out

	if [ $? = 0 ]; then
		echo -e "Test passed!\n"
		passed=$(($passed + 1))
	else	
		echo -e "Test failed!\n"
	fi
done

echo -e "\nTotal: $(( $(($passed * 100)) / 32))"
