package de.vksi.c4j.doclet;


import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.standard.Standard;

/**
 * @author fmeyerer
 *
 */
public class C4JDoclet extends Standard {

    public static int optionLength(String option) {
    	return CustomHtmlDoclet.optionLength(option);
    }

    public static boolean start(RootDoc root) {
    	return CustomHtmlDoclet.start(root);
    }

    public static boolean validOptions(String[][] options,
                                   DocErrorReporter reporter) {
        return CustomHtmlDoclet.validOptions(options, reporter);
    }

    public static LanguageVersion languageVersion() {
        return CustomHtmlDoclet.languageVersion();
    }

}