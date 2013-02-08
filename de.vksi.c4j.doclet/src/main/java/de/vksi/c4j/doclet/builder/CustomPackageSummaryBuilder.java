package de.vksi.c4j.doclet.builder;

import com.sun.tools.doclets.internal.toolkit.builders.AbstractBuilder;
import com.sun.tools.doclets.internal.toolkit.builders.LayoutParser;
import com.sun.tools.doclets.internal.toolkit.util.*;
import com.sun.tools.doclets.internal.toolkit.*;
import com.sun.javadoc.*;

import de.vksi.c4j.doclet.util.C4JContractFilter;
import de.vksi.c4j.doclet.util.TargetContractMap;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;

/**
 * Builds the summary for a given package.
 * 
 * This code is not part of an API. It is implementation that is subject to
 * change. Do not use it as an API
 * 
 * @author Jamie Ho, modified by fmeyerer
 * @since 1.5
 */
public class CustomPackageSummaryBuilder extends AbstractBuilder {

	private static final String DOCLET_ERROR_SUMMARY = "doclet.Error_Summary";
	private static final String DOCLET_EXCEPTION_SUMMARY = "doclet.Exception_Summary";
	private static final String DOCLET_ENUM_SUMMARY = "doclet.Enum_Summary";
	private static final String DOCLET_ANNOTATION_TYPES_SUMMARY = "doclet.Annotation_Types_Summary";
	private static final String DOCLET_INTERFACE_SUMMARY = "doclet.Interface_Summary";
	private static final String DOCLET_CLASS_SUMMARY = "doclet.Class_Summary";

	/**
	 * The root element of the package summary XML is {@value} .
	 */
	public static final String ROOT = "PackageDoc";

	/**
	 * The package being documented.
	 */
	private PackageDoc packageDoc;

	/**
	 * The doclet specific writer that will output the result.
	 */
	private PackageSummaryWriter packageWriter;
	
	private TargetContractMap targetContractMap;

	private CustomPackageSummaryBuilder(Configuration configuration) {
		super(configuration);
	}

	private CustomPackageSummaryBuilder(Configuration configuration, TargetContractMap targetContractMap) {
		this(configuration);
		this.targetContractMap = (targetContractMap != null) ? targetContractMap : new TargetContractMap();
	}

	/**
	 * Construct a new CustomPackageSummaryBuilder.
	 * 
	 * @param configuration
	 *            the current configuration of the doclet.
	 * @param pkg
	 *            the package being documented.
	 * @param packageWriter
	 *            the doclet specific writer that will output the result.
	 * 
	 * @return an instance of a CustomPackageSummaryBuilder.
	 */
	public static CustomPackageSummaryBuilder getInstance(Configuration configuration, PackageDoc pkg,
			PackageSummaryWriter packageWriter, TargetContractMap targetContractMap) {
		CustomPackageSummaryBuilder builder = new CustomPackageSummaryBuilder(configuration, targetContractMap);
		builder.packageDoc = pkg;
		builder.packageWriter = packageWriter;
		return builder;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("rawtypes")
	public void invokeMethod(String methodName, Class[] paramClasses, Object[] params) throws Exception {
		if (DEBUG) {
			configuration.root.printError("DEBUG: " + this.getClass().getName() + "." + methodName);
		}
		Method method = this.getClass().getMethod(methodName, paramClasses);
		method.invoke(this, params);
	}

	/**
	 * Build the package summary.
	 */
	public void build() throws IOException {
		if (packageWriter == null) {
			// Doclet does not support this output.
			return;
		}
		build(LayoutParser.getInstance(configuration).parseXML(ROOT));
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return ROOT;
	}

	/**
	 * Build the package documentation.
	 */
	@SuppressWarnings("rawtypes")
	public void buildPackageDoc(List elements) throws Exception {
		build(elements);
		packageWriter.close();
		Util.copyDocFiles(configuration, Util.getPackageSourcePath(configuration, packageDoc),
				DirectoryManager.getDirectoryPath(packageDoc) + File.separator
						+ DocletConstants.DOC_FILES_DIR_NAME, true);
	}

	/**
	 * Build the header of the summary.
	 */
	public void buildPackageHeader() {
		packageWriter.writePackageHeader(Util.getPackageName(packageDoc));
	}

	/**
	 * Build the description of the summary.
	 */
	public void buildPackageDescription() {
		if (configuration.nocomment) {
			return;
		}
		packageWriter.writePackageDescription();
	}

	/**
	 * Build the tags of the summary.
	 */
	public void buildPackageTags() {
		if (configuration.nocomment) {
			return;
		}
		packageWriter.writePackageTags();
	}

	/**
	 * Build the package summary.
	 */
	@SuppressWarnings("rawtypes")
	public void buildSummary(List elements) {
		build(elements);
	}

	/**
	 * Build the overall header.
	 */
	public void buildSummaryHeader() {
		packageWriter.writeSummaryHeader();
	}

	/**
	 * Build the overall footer.
	 */
	public void buildSummaryFooter() {
		packageWriter.writeSummaryFooter();
	}

	/**
	 * Build the summary for the classes in this package.
	 */
	public void buildClassSummary() {
		ClassDoc[] classes = packageDoc.isIncluded() ? packageDoc.ordinaryClasses()
				: configuration.classDocCatalog.ordinaryClasses(Util.getPackageName(packageDoc));
		if (classes.length > 0) {
			
			List<ClassDoc> filteredClasses = C4JContractFilter.removeContractClasses(classes, targetContractMap);

			if(!filteredClasses.isEmpty())
				packageWriter.writeClassesSummary(filteredClasses.toArray(new ClassDoc[filteredClasses.size()]), configuration.getText(DOCLET_CLASS_SUMMARY));
		}
	}
	

	/**
	 * Build the summary for the interfaces in this package.
	 */
	public void buildInterfaceSummary() {
		ClassDoc[] interfaces = packageDoc.isIncluded() ? packageDoc.interfaces()
				: configuration.classDocCatalog.interfaces(Util.getPackageName(packageDoc));
		if (interfaces.length > 0) {
			packageWriter.writeClassesSummary(interfaces, configuration.getText(DOCLET_INTERFACE_SUMMARY));
		}
	}

	/**
	 * Build the summary for the enums in this package.
	 */
	public void buildAnnotationTypeSummary() {
		ClassDoc[] annotationTypes = packageDoc.isIncluded() ? packageDoc.annotationTypes()
				: configuration.classDocCatalog.annotationTypes(Util.getPackageName(packageDoc));
		if (annotationTypes.length > 0) {
			packageWriter.writeClassesSummary(annotationTypes,
					configuration.getText(DOCLET_ANNOTATION_TYPES_SUMMARY));
		}
	}

	/**
	 * Build the summary for the enums in this package.
	 */
	public void buildEnumSummary() {
		ClassDoc[] enums = packageDoc.isIncluded() ? packageDoc.enums() : configuration.classDocCatalog
				.enums(Util.getPackageName(packageDoc));
		if (enums.length > 0) {
			packageWriter.writeClassesSummary(enums, configuration.getText(DOCLET_ENUM_SUMMARY));
		}
	}

	/**
	 * Build the summary for the exceptions in this package.
	 */
	public void buildExceptionSummary() {
		ClassDoc[] exceptions = packageDoc.isIncluded() ? packageDoc.exceptions()
				: configuration.classDocCatalog.exceptions(Util.getPackageName(packageDoc));
		if (exceptions.length > 0) {
			packageWriter.writeClassesSummary(exceptions, configuration.getText(DOCLET_EXCEPTION_SUMMARY));
		}
	}

	/**
	 * Build the summary for the errors in this package.
	 */
	public void buildErrorSummary() {
		ClassDoc[] errors = packageDoc.isIncluded() ? packageDoc.errors() : configuration.classDocCatalog
				.errors(Util.getPackageName(packageDoc));
		if (errors.length > 0) {
			packageWriter.writeClassesSummary(errors, configuration.getText(DOCLET_ERROR_SUMMARY));
		}
	}

	/**
	 * Build the footer of the summary.
	 */
	public void buildPackageFooter() {
		packageWriter.writePackageFooter();
	}
}
