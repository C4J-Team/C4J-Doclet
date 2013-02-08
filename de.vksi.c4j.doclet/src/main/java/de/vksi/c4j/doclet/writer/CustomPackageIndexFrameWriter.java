package de.vksi.c4j.doclet.writer;

import java.io.IOException;
import java.util.List;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.formats.html.ConfigurationImpl;
import com.sun.tools.doclets.formats.html.PackageIndexFrameWriter;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;

import de.vksi.c4j.doclet.util.C4JContractFilter;
import de.vksi.c4j.doclet.util.TargetContractMap;

/**
 * Generate the package index for the left-hand frame (without contracts) in the generated output.
 * A click on the package name in this frame will update the page in the bottom
 * left hand frame with the listing of contents of the clicked package.
 *
 * @author Atul M Dambalkar
 * @author fmeyerer
 */
public class CustomPackageIndexFrameWriter extends PackageIndexFrameWriter {

	private static final String OVERVIEW_FRAME_HTML = "overview-frame.html";
	private static final String DOCLET_WINDOW_OVERVIEW = "doclet.Window_Overview";
	private static final String DOCLET_EXCEPTION_ENCOUNTERED = "doclet.exception_encountered";
	private TargetContractMap targetContractMap;

	public CustomPackageIndexFrameWriter(ConfigurationImpl configuration, String filename) throws IOException {
		this(configuration, filename, null);
	}

	public CustomPackageIndexFrameWriter(ConfigurationImpl configuration, String filename,
			TargetContractMap targetContractMap) throws IOException {
		super(configuration, filename);
		this.targetContractMap = (targetContractMap != null) ? targetContractMap : new TargetContractMap();
	}

	/**
	 * Generate the package index page for the right-hand frame.
	 * 
	 * @param configuration
	 *            the current configuration of the doclet.
	 */
	public static void generate(ConfigurationImpl configuration, TargetContractMap targetContractMap) {
		CustomPackageIndexFrameWriter packgen;
		String filename = OVERVIEW_FRAME_HTML;
		try {
			packgen = new CustomPackageIndexFrameWriter(configuration, filename, targetContractMap);
			packgen.generatePackageIndexFile(DOCLET_WINDOW_OVERVIEW, false);
			packgen.close();
		} catch (IOException exc) {
			configuration.standardmessage.error(DOCLET_EXCEPTION_ENCOUNTERED, exc.toString(), filename);
			throw new DocletAbortException();
		}
	}

	@Override
	protected void printIndexContents(PackageDoc[] packages, String text) {
		super.printIndexContents(packages, text);
	}

	@Override
	protected void printIndexRow(PackageDoc pkg) {
		List<ClassDoc> filteredClasses = C4JContractFilter.removeContractClasses(pkg.allClasses(), targetContractMap);

		if (!filteredClasses.isEmpty())
			super.printIndexRow(pkg);
	}
}
