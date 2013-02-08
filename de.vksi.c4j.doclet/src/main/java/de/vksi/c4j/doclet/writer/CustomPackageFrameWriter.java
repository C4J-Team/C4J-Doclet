package de.vksi.c4j.doclet.writer;

import java.io.IOException;
import java.util.List;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.formats.html.ConfigurationImpl;
import com.sun.tools.doclets.formats.html.PackageFrameWriter;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import com.sun.tools.doclets.internal.toolkit.util.Util;

import de.vksi.c4j.doclet.util.C4JContractFilter;
import de.vksi.c4j.doclet.util.TargetContractMap;

/**
 * Class to generate file for each package contents in the left-hand bottom
 * frame. This will list all the Class Kinds in the package, except contracts. 
 * A click on any class-kind will update the right-hand frame with the clicked 
 * class-kind page.
 *
 * @author Atul M Dambalkar
 * @author fmeyerer
 */
public class CustomPackageFrameWriter extends PackageFrameWriter {

	private static final String DOCLET_EXCEPTION_ENCOUNTERED = "doclet.exception_encountered";

	private TargetContractMap targetContractMap;

	public CustomPackageFrameWriter(ConfigurationImpl configuration, PackageDoc packageDoc)
			throws IOException {
		this(configuration, packageDoc, null);
	}

	public CustomPackageFrameWriter(ConfigurationImpl configuration, PackageDoc packageDoc,
			TargetContractMap targetContractMap) throws IOException {
		super(configuration, packageDoc);

		this.targetContractMap = (targetContractMap != null) ? targetContractMap : new TargetContractMap();
	}
	

	/**
	 * Generate a package summary page for the left-hand bottom frame. Construct
	 * the CustomPackageFrameWriter object and then uses it generate the file.
	 * 
	 * @param configuration
	 *            the current configuration of the doclet.
	 * @param packageDoc
	 *            The package for which "pacakge-frame.html" is to be generated.
	 */
	public static void generate(ConfigurationImpl configuration, PackageDoc packageDoc,
			TargetContractMap targetContractMap) {

		CustomPackageFrameWriter packgen;
		try {
			packgen = new CustomPackageFrameWriter(configuration, packageDoc, targetContractMap);
			String pkgName = Util.getPackageName(packageDoc);
			packgen.printHtmlHeader(pkgName, configuration.metakeywords.getMetaKeywords(packageDoc), false);
			packgen.printPackageHeader(pkgName);
			packgen.generateClassListing();
			packgen.printBodyHtmlEnd();
			packgen.close();
		} catch (IOException exc) {
			configuration.standardmessage.error(DOCLET_EXCEPTION_ENCOUNTERED, exc.toString(),
					OUTPUT_FILE_NAME);
			throw new DocletAbortException();
		}
	}

	@Override
	protected void generateClassKindListing(ClassDoc[] classes, String label) {
		List<ClassDoc> filteredClasses = C4JContractFilter.removeContractClasses(classes, targetContractMap);

		if(!filteredClasses.isEmpty())
			super.generateClassKindListing(filteredClasses.toArray(new ClassDoc[filteredClasses.size()]), label);
	}
}
