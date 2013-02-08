package de.vksi.c4j.doclet.writer;

import java.io.IOException;
import java.util.List;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.formats.html.ConfigurationImpl;
import com.sun.tools.doclets.formats.html.PackageTreeWriter;
import com.sun.tools.doclets.internal.toolkit.util.DirectoryManager;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;

import de.vksi.c4j.doclet.util.C4JContractFilter;
import de.vksi.c4j.doclet.util.TargetContractMap;

/**
 * Class to generate Tree page for a package without contracts. The name of the file generated is
 * "package-tree.html" and it is generated in the respective package directory.
 *
 * @author Atul M Dambalkar
 * @author fmeyerer
 */
public class CustomPackageTreeWriter extends PackageTreeWriter {

	private static final String DOCLET_EXCEPTION_ENCOUNTERED = "doclet.exception_encountered";
	private static final String PACKAGE_TREE_HTML = "package-tree.html";
	private TargetContractMap targetContractMap;

	public CustomPackageTreeWriter(ConfigurationImpl configuration, String path, String filename,
			PackageDoc packagedoc, PackageDoc prev, PackageDoc next) throws IOException {
		this(configuration, path, filename, packagedoc, prev, next, null);
	}

	public CustomPackageTreeWriter(ConfigurationImpl configuration, String path, String filename,
			PackageDoc packagedoc, PackageDoc prev, PackageDoc next, TargetContractMap targetContractMap)
			throws IOException {
		super(configuration, path, filename, packagedoc, prev, next);
		this.targetContractMap = (targetContractMap != null) ? targetContractMap : new TargetContractMap();
	}

	/**
	 * Construct a CustomPackageTreeWriter object and then use it to generate
	 * the package tree page.
	 * 
	 * @param pkg
	 *            Package for which tree file is to be generated.
	 * @param prev
	 *            Previous package in the alpha-ordered list.
	 * @param next
	 *            Next package in the alpha-ordered list.
	 * @param noDeprecated
	 *            If true, do not generate any information for deprecated classe
	 *            or interfaces.
	 * @throws DocletAbortException
	 */
	public static void generate(ConfigurationImpl configuration, PackageDoc pkg, PackageDoc prev,
			PackageDoc next, TargetContractMap targetContractMap) {
		CustomPackageTreeWriter packgen;
		String path = DirectoryManager.getDirectoryPath(pkg);
		String filename = PACKAGE_TREE_HTML;
		try {
			packgen = new CustomPackageTreeWriter(configuration, path, filename, pkg, prev, next,
					targetContractMap);
			packgen.generatePackageTreeFile();
			packgen.close();
		} catch (IOException exc) {
			configuration.standardmessage.error(DOCLET_EXCEPTION_ENCOUNTERED, exc.toString(), filename);
			throw new DocletAbortException();
		}
	}

	
	/**
	 * Remove Contracts from Class Hierarchy and call super implementation.
	 * 
	 *  {@inheritDoc}
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void generateLevelInfo(ClassDoc parent, List list, boolean isEnum) {
		ClassDoc[] classes = (ClassDoc[]) list.toArray(new ClassDoc[list.size()]);
		List<ClassDoc> filteredClasses = C4JContractFilter.removeContractClasses(classes, targetContractMap);

		super.generateLevelInfo(parent, filteredClasses, isEnum);
	}

}
