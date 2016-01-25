# polite (Python Objective lite)

VERSION: 0.0 pre-alpha

Lauguage description can be found in polite.pdf

This file describes the needed steps to compile

## Steps:
    1. On console, go to this project's root folder folder and run ant
    2. type polite.bat 'yourtestfile' to run the interpreter

## Content: (beside the obvious)
    src/testSyntax.in - some unittest to written in polite to test itself
    codes/samples.py  - example codes provided in presentation + some extra
    src/debug.prop    - debug property file, need to have debug enabled before
            this file is accessed
    CS 345 Project.pdf - presentation file
    grammar.html - grammar rule for this language, this file is generated
            after each ant call

## Debugging:
    use either polite-debug.bat or run java -Dpolite.debug.props='file location'
    and execute the program.  The program will generate a template debug prop
    to be filled.  Go into this file, and set polite.debugging to true.  You may
    also add the location to generate the debugging output(recommanded, the tool
    is very wordy).  List of method will automatically be regenerated after each
    run.  You may toggle logging of each method on or off.  To disable output
    from particular class, delete all its method's property, then set the class
    itself to false.
    
    A default debug property file is provided at src/debug.prop

## Publishing:
    Run ant -Drelease=true, it should generate polite.jar where build.xml is
    located.  Simply use java -jar polite.jar 'file' to run the code afterward

## Abstract Syntax Tree:
    After running a language, its corresponding abstract syntax tree is
    generated at where the file is with extension .ilf
    