package de.vksi.c4j.doclet.writer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.formats.html.ConfigurationImpl;
import com.sun.tools.doclets.formats.html.TreeWriter;
import com.sun.tools.doclets.internal.toolkit.util.ClassTree;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;

import de.vksi.c4j.doclet.util.C4JContractFilter;
import de.vksi.c4j.doclet.util.TargetContractMap;

/**
 * Generate Class Hierarchy page for all the Classes, except contracts, in this run.  Use
 * ClassTree for building the Tree. The name of
 * the generated file is "overview-tree.html" and it is generated in the
 * current or the destination directory.
 *
 * @author Atul M Dambalkar
 * @author fmeyerer
 */
public class CustomTreeWriter extends TreeWriter {
	private static final String DOCLET_PACKAGE_HIERARCHIES = "doclet.Package_Hierarchies";

	private static final String DOCLET_EXCEPTION_ENCOUNTERED = "doclet.exception_encountered";

	private static final String OVERVIEW_TREE_HTML = "overview-tree.html";

	private static final String PACKAGE_TREE_HTML = "package-tree.html";

	/**
	 * True if there are no packages specified on the command line, False
	 * otherwise.
	 */
	private boolean classesonly;

	private TargetContractMap targetContractMap;

	public CustomTreeWriter(ConfigurationImpl configuration, String filename, ClassTree classtree)
			throws IOException {
		this(configuration, filename, classtree, null);
	}

	public CustomTreeWriter(ConfigurationImpl configuration, String filename, ClassTree classtree,
			TargetContractMap targetContractMap) throws IOException {
		super(configuration, filename, classtree);
		classesonly = configuration.packages.length == 0;
		this.targetContractMap = (targetContractMap != null) ? targetContractMap : new TargetContractMap();
	}

	/**
	 * Create a TreeWriter object and use it to generate the
	 * "overview-tree.html" file.
	 * 
	 * @param classtree
	 *            the class tree being documented.
	 * @throws DocletAbortException
	 */
	public static void generate(ConfigurationImpl configuration, ClassTree classtree,
			TargetContractMap targetContractMap) {
		CustomTreeWriter treegen;
		String filename = OVERVIEW_TREE_HTML;
		try {
			treegen = new CustomTreeWriter(configuration, filename, classtree, targetContractMap);
			treegen.generateTreeFile();
			treegen.close();
		} catch (IOException exc) {
			configuration.standardmessage.error(DOCLET_EXCEPTION_ENCOUNTERED, exc.toString(), filename);
			throw new DocletAbortException();
		}
	}

	/**
	 * Remove Contracts from Class Hierarchy and call super implementation.
	 * 
	 * {@inheritDoc}
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void generateLevelInfo(ClassDoc parent, List list, boolean isEnum) {
		ClassDoc[] classes = (ClassDoc[]) list.toArray(new ClassDoc[list.size()]);
		List<ClassDoc> filteredClasses = C4JContractFilter.removeContractClasses(classes, targetContractMap);

		super.generateLevelInfo(parent, filteredClasses, isEnum);
	}

	@Override
	protected void printPackageTreeLinks() {
		PackageDoc[] packages = getPackagesWithoutContracts();

		// Do nothing if only unnamed package is used
		if (packages.length == 1 && packages[0].name().length() == 0) {
			return;
		}
		if (!classesonly) {
			dl();
			dt();
			boldText(DOCLET_PACKAGE_HIERARCHIES);
			dd();
			for (int i = 0; i < packages.length; i++) {
				if (packages[i].name().length() == 0) {
					continue;
				}
				String filename = pathString(packages[i], PACKAGE_TREE_HTML);
				printHyperLink(filename, "", packages[i].name());
				if (i < packages.length - 1) {
					print(", ");
				}
			}
			dlEnd();
			hr();
		}
	}

	private PackageDoc[] getPackagesWithoutContracts() {
		PackageDoc[] packages = configuration.packages;
		List<PackageDoc> packagesWithoutContracts = new ArrayList<PackageDoc>();

		for (PackageDoc pkg : packages) {
			List<ClassDoc> filteredClasses = C4JContractFilter
					.removeContractClasses(pkg.allClasses(), targetContractMap);
			if (!filteredClasses.isEmpty())
				packagesWithoutContracts.add(pkg);
		}

		return packagesWithoutContracts.toArray(new PackageDoc[packagesWithoutContracts.size()]);
	}

}
