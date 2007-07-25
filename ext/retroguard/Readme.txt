
     ** RETROGUARD FOR JAVA(tm) OBFUSCATOR **

-- RetroGuard v2.3.1, released 13th March 2007 --

Thank you for downloading this release of the RetroGuard(tm) for 
Java Obfuscator. If you have questions about using RetroGuard, or 
wish to report a bug to be fixed for the next release, please 
contact web@retrologic.com


To obtain a license agreement for commercial use of this software, 
please visit:

        http://www.retrologic.com/retroguard-main.html

or contact Retrologic Systems at web@retrologic.com

RetroGuard is licensed in several versions, depending on your needs:

 RetroGuard Pro  - subscription-based commercial license for commercial projects;

 RetroGuard Lite - non-commercial license for academic / not-for-profit projects;

 RetroGuard Library - GNU GPL v2 library for integration in open-source projects.

In summary, if your project is commercial then RetroGuard is commercial;
if your project is not-for-profit then RetroGuard is provided at no charge. 


Included in this release are the following files and directories:

Readme.txt      : This file.

License.txt     : For commercial projects, RetroGuard Pro is distributed under
                  an annual subscription-based commercial license.
-OR-
LicenseLite.txt : For academic and not-for-profit projects, RetroGuard Lite 
                  is distributed under a non-commercial license.
-OR-
GPL.txt         : The RetroGuard Library for open-source tools developers
                  is distributed under the GNU General Public License.

retroguard.jar  : A JAR (Java ARchive) file containing the classes that
                  make up the RetroGuard Bytecode Obfuscator. 

src-dist.tar.gz : RetroGuard source (GPL release of RetroGuard Library only).


The latest documentation for RetroGuard can be found online at:

        http://www.retrologic.com/retroguard-docs.html

The change history of the software can be found at:

        http://www.retrologic.com/retroguard-changes.html

Answers to common questions about obfuscation and RetroGuard can be found at:

        http://www.retrologic.com/retroguard-faq.html

If you have any questions please contact web@retrologic.com


-------------------------------------------
-- RetroGuard Documentation: Quick Start --
-------------------------------------------

Follow the steps below to get started right away with RetroGuard.

-- Step 1: Jar Your Code --

Let's assume you have a Java application 'com.myco.MyApp' and a number of 
support classes in package 'com.myco':

        javac com/myco/*.java 
        jar cf in.jar com/myco/*.class

So, you would usually run this application as:

        java -cp in.jar com.myco.MyApp

-- Step 2: Run RetroGuard --

        java -jar retroguard.jar

-- Step 3: Test Your Code --

        java -cp out.jar com.myco.MyApp

Run your software as usual, but with the obfuscated out.jar in your classpath 
instead of the original in.jar. Test that everything works as it should. Any 
change in behavior shows that a custom RetroGuard script will be needed for 
your project, usually due to use of reflection in your software.

-- Step 4: Review 'retroguard.log' --

Review the RetroGuard log file to ensure no warnings or errors occured during obfuscation. Now continue reading at:

        http://www.retrologic.com/retroguard-docs.html

RetroGuard's default settings will get you started, but creating a custom 
RetroGuard script file will mean fewer unobfuscated Java identifiers and 
smaller obfuscated code size. 
