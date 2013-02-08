package de.vksi.c4j.doclet.writer;

import java.io.IOException;
import java.util.List;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.formats.html.ConfigurationImpl;
import com.sun.tools.doclets.formats.html.PackageIndexWriter;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;

import de.vksi.c4j.doclet.util.C4JContractFilter;
import de.vksi.c4j.doclet.util.TargetContractMap;

/**
 * Generate the package index page "overview-summary.html" (without contracts) for the right-hand
 * frame. A click on the package name on this page will update the same frame
 * with the "pacakge-summary.html" file for the clicked package.
 *
 * @author Atul M Dambalkar
 * @author fmeyerer
 */
public class CustomPackageIndexWriter extends PackageIndexWriter {
	private static final String DOCLET_EXCEPTION_ENCOUNTERED = "doclet.exception_encountered";
	private static final String DOCLET_WINDOW_OVERVIEW_SUMMARY = "doclet.Window_Overview_Summary";
	private static final String OVERVIEW_SUMMARY_HTML = "overview-summary.html";
	private TargetContractMap targetContractMap;
	
	public CustomPackageIndexWriter(ConfigurationImpl configuration, String filename) throws IOException {
		super(configuration, filename);
	}
	public CustomPackageIndexWriter(ConfigurationImpl configuration, String filename, TargetContractMap targetContractMap) throws IOException {
		super(configuration, filename);
		this.targetContractMap = (targetContractMap != null) ? targetContractMap : new TargetContractMap();
	}

	/**
     * Generate the package index page for the right-hand frame.
     *
     * @param configuration the current configuration of the doclet.
     */
    public static void generate(ConfigurationImpl configuration, TargetContractMap targetContractMap) {
        CustomPackageIndexWriter packgen;
        String filename = OVERVIEW_SUMMARY_HTML;
        try {
            packgen = new CustomPackageIndexWriter(configuration, filename, targetContractMap);
            packgen.generatePackageIndexFile(DOCLET_WINDOW_OVERVIEW_SUMMARY, true);
            packgen.close();
        } catch (IOException exc) {
            configuration.standardmessage.error(
                        DOCLET_EXCEPTION_ENCOUNTERED,
                        exc.toString(), filename);
            throw new DocletAbortException();
        }
    }
    
	/**
     * {@inheritDoc}
     */
    @Override
    protected void printIndexRow(PackageDoc pkg) {
    	List<ClassDoc> filteredClasses = C4JContractFilter.removeContractClasses(pkg.allClasses(), targetContractMap);

		if (!filteredClasses.isEmpty())
			super.printIndexRow(pkg);
    }
}
