C4J-Doclet
==========
##Download the C4J-Doclet
* [Download](https://github.com/C4J-Team/C4J-Doclet/raw/master/download/c4j-doclet-6.0.0.jar)

**Note**: Required JDK Version 1.6

##Generate JavaDoc within Eclipse
* Go to Project -> Generate Javadoc. A Javadoc generation wizard is shown.
* Set up the path to the javadoc.exe (e.g. [path to your jdk]\bin\javadox.exe)
* Select the project(s) for which Javadoc will be generated
* Select the option "Use custom doclet"
  * Fill in the C4J-Doclet name: de.vksi.c4j.doclet.C4JDoclet
  * Set up the path to the c4j-doclet-6.0.0.jar
* Click on "Next"
  * Set up the destination directory where javadoc saves the generated HTML files
    * Enter -d [your destination] into the text field "Extra Javadoc options"
* Click on "Finish" to generate the Javadoc generation

For more detailed information about the Javadoc-Tool and the use of doclets, please refere to:
* http://www.oracle.com/technetwork/java/javase/documentation/index-jsp-135444.html
[![githalytics.com alpha](https://cruel-carlota.pagodabox.com/a0e6c1f393dd8802d4da46835189ff46 "githalytics.com")](http://githalytics.com/C4J-Team/C4J-Doclet)
