package de.vksi.c4j.doclet;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SourcePosition;
import com.sun.tools.doclets.formats.html.ClassUseWriter;
import com.sun.tools.doclets.formats.html.DeprecatedListWriter;
import com.sun.tools.doclets.formats.html.FrameOutputWriter;
import com.sun.tools.doclets.formats.html.HelpWriter;
import com.sun.tools.doclets.formats.html.HtmlDoclet;
import com.sun.tools.doclets.formats.html.PackageWriterImpl;
import com.sun.tools.doclets.formats.html.SingleIndexWriter;
import com.sun.tools.doclets.formats.html.SplitIndexWriter;
import com.sun.tools.doclets.formats.html.StylesheetWriter;
import com.sun.tools.doclets.internal.toolkit.builders.AbstractBuilder;
import com.sun.tools.doclets.internal.toolkit.builders.BuilderFactory;
import com.sun.tools.doclets.internal.toolkit.util.ClassTree;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import com.sun.tools.doclets.internal.toolkit.util.DocletConstants;
import com.sun.tools.doclets.internal.toolkit.util.IndexBuilder;
import com.sun.tools.doclets.internal.toolkit.util.PackageListWriter;
import com.sun.tools.doclets.internal.toolkit.util.SourceToHTMLConverter;
import com.sun.tools.doclets.internal.toolkit.util.Util;

import de.vksi.c4j.doclet.builder.CustomClassBuilder;
import de.vksi.c4j.doclet.builder.CustomPackageSummaryBuilder;
import de.vksi.c4j.doclet.util.TargetContractMap;
import de.vksi.c4j.doclet.writer.CustomAllClassesFrameWriter;
import de.vksi.c4j.doclet.writer.CustomClassWriterImpl;
import de.vksi.c4j.doclet.writer.CustomPackageFrameWriter;
import de.vksi.c4j.doclet.writer.CustomPackageIndexFrameWriter;
import de.vksi.c4j.doclet.writer.CustomPackageIndexWriter;
import de.vksi.c4j.doclet.writer.CustomPackageTreeWriter;
import de.vksi.c4j.doclet.writer.CustomTreeWriter;

/**
 * @author fmeyerer
 *
 */
public class CustomHtmlDoclet extends HtmlDoclet {
	private TargetContractMap targetContractMap;

	public static boolean start(RootDoc root) {
		CustomHtmlDoclet doclet = new CustomHtmlDoclet();

		doclet.configuration.root = root;

		try {
			doclet.startGeneration(root);
		} catch (Exception exc) {
			exc.printStackTrace();
			return false;
		}
		return true;
	}

	private void startGeneration(RootDoc root) throws Exception {
		if (root.classes().length == 0) {
			
			
			configuration.message.error("doclet.No_Public_Classes_To_Document");
			return;
		}

		configuration.setOptions();
		configuration.getDocletSpecificMsg().notice("doclet.build_version",
				configuration.getDocletSpecificBuildDate());

		ClassTree classtree = new ClassTree(configuration, configuration.nodeprecated);
		
		targetContractMap = new TargetContractMap(root);
		targetContractMap.generateMapping();

		generateClassFiles(root, classtree);
		if (configuration.sourcepath != null && configuration.sourcepath.length() > 0) {
			StringTokenizer pathTokens = new StringTokenizer(configuration.sourcepath,
					String.valueOf(File.pathSeparatorChar));
			boolean first = true;
			while (pathTokens.hasMoreTokens()) {
				Util.copyDocFiles(configuration, pathTokens.nextToken() + File.separator,
						DocletConstants.DOC_FILES_DIR_NAME, first);
				first = false;
			}
		}

		PackageListWriter.generate(configuration);
		generatePackageFiles(classtree);

		generateOtherFiles(root, classtree);
		configuration.tagletManager.printReport();
	}

	@Override
	protected void generateClassFiles(ClassDoc[] classes, ClassTree classtree) {
		Arrays.sort(classes);
		for (int i = 0; i < classes.length; i++) {
			if (!(configuration.isGeneratedDoc(classes[i]) && classes[i].isIncluded())) {
				continue;
			}
			ClassDoc prev = (i == 0) ? null : classes[i - 1];
			ClassDoc curr = classes[i];
			ClassDoc next = (i + 1 == classes.length) ? null : classes[i + 1];
			try {
				if (curr.isAnnotationType()) {
					AbstractBuilder annotationTypeBuilder = configuration.getBuilderFactory()
							.getAnnotationTypeBuilder((AnnotationTypeDoc) curr, prev, next);
					annotationTypeBuilder.build();
				} else {
					AbstractBuilder classBuilder = CustomClassBuilder.getInstance(configuration, curr,
							new CustomClassWriterImpl(curr, prev, next, classtree), targetContractMap);
					classBuilder.build();

				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new DocletAbortException();
			}
		}
	}

	@Override
	protected void generatePackageFiles(ClassTree classtree) throws Exception {
		PackageDoc[] packages = configuration.packages;
		if (packages.length > 1) {
			CustomPackageIndexFrameWriter.generate(configuration, targetContractMap);
		}
		PackageDoc prev = null, next;
		for (int i = 0; i < packages.length; i++) {
			CustomPackageFrameWriter.generate(configuration, packages[i], targetContractMap);
			next = (i + 1 < packages.length && packages[i + 1].name().length() > 0) ? packages[i + 1] : null;
			// If the next package is unnamed package, skip 2 ahead if possible
			next = (i + 2 < packages.length && next == null) ? packages[i + 2] : next;
			
			PackageWriterImpl packageWriter = new PackageWriterImpl(configuration, packages[i], prev, next);
			AbstractBuilder packageSummaryBuilder = CustomPackageSummaryBuilder.getInstance(configuration,
					packages[i], packageWriter, targetContractMap);
			packageSummaryBuilder.build();
			
			if (configuration.createtree) {
				CustomPackageTreeWriter
						.generate(configuration, packages[i], prev, next, targetContractMap);
			}
			prev = packages[i];
		}
	}

	@Override
	protected void generateOtherFiles(RootDoc root, ClassTree classtree) throws Exception {
		BuilderFactory builderFactory = configuration.getBuilderFactory();
		AbstractBuilder constantsSummaryBuilder = builderFactory.getConstantsSummaryBuider();
		constantsSummaryBuilder.build();
		AbstractBuilder serializedFormBuilder = builderFactory.getSerializedFormBuilder();
		serializedFormBuilder.build();

		if (configuration.linksource) {
			if (configuration.destDirName.length() > 0) {
				SourceToHTMLConverter.convertRoot(configuration, root, configuration.destDirName
						+ File.separator + DocletConstants.SOURCE_OUTPUT_DIR_NAME);
			} else {
				SourceToHTMLConverter
						.convertRoot(configuration, root, DocletConstants.SOURCE_OUTPUT_DIR_NAME);
			}
		}

		if (configuration.topFile.length() == 0) {
			configuration.standardmessage.error("doclet.No_Non_Deprecated_Classes_To_Document");
			return;
		}
		boolean nodeprecated = configuration.nodeprecated;
		String configdestdir = configuration.destDirName;
		String confighelpfile = configuration.helpfile;
		String configstylefile = configuration.stylesheetfile;
		performCopy(configdestdir, confighelpfile);
		performCopy(configdestdir, configstylefile);
		Util.copyResourceFile(configuration, "inherit.gif", false);
		// do early to reduce memory footprint
		if (configuration.classuse) {
			ClassUseWriter.generate(configuration, classtree);
		}
		IndexBuilder indexbuilder = new IndexBuilder(configuration, nodeprecated);

		if (configuration.createtree) {
			CustomTreeWriter.generate(configuration, classtree, targetContractMap);
		}
		if (configuration.createindex) {
			if (configuration.splitindex) {
				SplitIndexWriter.generate(configuration, indexbuilder);
			} else {
				SingleIndexWriter.generate(configuration, indexbuilder);
			}
		}

		if (!(configuration.nodeprecatedlist || nodeprecated)) {
			DeprecatedListWriter.generate(configuration);
		}

		CustomAllClassesFrameWriter.generate(configuration, new IndexBuilder(configuration, nodeprecated,
				true), targetContractMap);

		FrameOutputWriter.generate(configuration);

		if (configuration.createoverview) {
			CustomPackageIndexWriter.generate(configuration, targetContractMap);
		}
		if (configuration.helpfile.length() == 0 && !configuration.nohelp) {
			HelpWriter.generate(configuration);
		}
		if (configuration.stylesheetfile.length() == 0) {
			StylesheetWriter.generate(configuration);
		}
	}

	private void performCopy(String configdestdir, String filename) {
		try {
			String destdir = (configdestdir.length() > 0) ? configdestdir + File.separatorChar : "";
			if (filename.length() > 0) {
				File helpstylefile = new File(filename);
				String parent = helpstylefile.getParent();
				String helpstylefilename = (parent == null) ? filename : filename
						.substring(parent.length() + 1);
				File desthelpfile = new File(destdir + helpstylefilename);
				if (!desthelpfile.getCanonicalPath().equals(helpstylefile.getCanonicalPath())) {
					configuration.message.notice((SourcePosition) null, "doclet.Copying_File_0_To_File_1",
							helpstylefile.toString(), desthelpfile.toString());
					Util.copyFile(desthelpfile, helpstylefile);
				}
			}
		} catch (IOException exc) {
			configuration.message.error((SourcePosition) null, "doclet.perform_copy_exception_encountered",
					exc.toString());
			throw new DocletAbortException();
		}
	}

}
