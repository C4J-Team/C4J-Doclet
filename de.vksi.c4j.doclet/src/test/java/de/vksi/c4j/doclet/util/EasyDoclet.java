package de.vksi.c4j.doclet.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Options;
import com.sun.tools.javadoc.JavadocTool;
import com.sun.tools.javadoc.ModifierFilter;

public class EasyDoclet {

	private static final String SOURCEPATH_OPT = "-sourcepath";

	final private Logger log = Logger.getLogger(EasyDoclet.class.getName());

	final private File[] sourceDirectory;
	final private String[] packageNames;
	final private File[] fileNames;
	final private RootDoc rootDoc;

	public EasyDoclet(File sourceDirectory, String... packageNames) {
		this(new File[] { sourceDirectory }, packageNames, new File[0]);
	}

	public EasyDoclet(File[] sourceDirectory, String... packageNames) {
		this(sourceDirectory, packageNames, new File[0]);
	}

	public EasyDoclet(File[] sourceDirectory, File... fileNames) {
		this(sourceDirectory, new String[0], fileNames);
	}

	protected EasyDoclet(File[] sourceDirectory, String[] packageNames, File[] fileNames) {
		this.sourceDirectory = sourceDirectory;
		this.packageNames = packageNames;
		this.fileNames = fileNames;

		Context context = new Context();
		Options compOpts = Options.instance(context);
		compOpts.put(SOURCEPATH_OPT, concatenateSourcePath());

		new PublicMessager(context, getApplicationName(), new PrintWriter(new LogWriter(Level.SEVERE), true),
				new PrintWriter(new LogWriter(Level.WARNING), true), new PrintWriter(
						new LogWriter(Level.FINE), true));

		ListBuffer<String> javaFiles = addingFilesToDocumentationPath(fileNames);
		ListBuffer<String[]> options = optionsToList(compOpts);
		ListBuffer<String> subPackages = addingPackagesToDocumentationPath(packageNames);

		JavadocTool javadocTool = JavadocTool.make0(context);

		try {
			rootDoc = javadocTool.getRootDocImpl("", null, new ModifierFilter(ModifierFilter.ALL_ACCESS),
					javaFiles.toList(), options.toList(), false, subPackages.toList(),
					new ListBuffer<String>().toList(), false, false, false);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		if (log.isLoggable(Level.FINEST)) {
			for (ClassDoc classDoc : getRootDoc().classes()) {
				log.finest("Parsed Javadoc class source: " + classDoc.position() + " with inline tags: "
						+ classDoc.inlineTags().length);
			}
		}
	}

	private ListBuffer<String> addingPackagesToDocumentationPath(String[] packageNames) {
		ListBuffer<String> subPackages = new ListBuffer<String>();
		for (String packageName : packageNames) {
			log.fine("Adding sub-packages to documentation path: " + packageName);
			subPackages.append(packageName);
		}
		return subPackages;
	}

	private ListBuffer<String> addingFilesToDocumentationPath(File[] fileNames) {
		ListBuffer<String> javaNames = new ListBuffer<String>();
		for (File fileName : fileNames) {
			log.fine("Adding file to documentation path: " + fileName.getAbsolutePath());
			javaNames.append(fileName.getPath());
		}
		return javaNames;
	}

	private ListBuffer<String[]> optionsToList(Options compOpts) {
		String[] opt = { SOURCEPATH_OPT, compOpts.get(SOURCEPATH_OPT) };
		ListBuffer<String[]> options = new ListBuffer<String[]>();
		options.append(opt);
		return options;
	}

	private String concatenateSourcePath() {
		String concatenatePath = "";
		for (File file : getSourceDirectory()) {
			if (file.exists()) {
				log.fine("Using source path: " + file.getAbsolutePath());
				concatenatePath += file.getAbsolutePath() + ";";
			} else {
				log.info("Ignoring non-existant source path, check your source directory argument");
			}
		}

		return concatenatePath.substring(0, concatenatePath.length() - 1);
	}

	public File[] getSourceDirectory() {
		return sourceDirectory;
	}

	public String[] getPackageNames() {
		return packageNames;
	}

	public File[] getFileNames() {
		return fileNames;
	}

	public RootDoc getRootDoc() {
		return rootDoc;
	}

	protected class LogWriter extends Writer {

		Level level;

		public LogWriter(Level level) {
			this.level = level;
		}

		public void write(char[] chars, int offset, int length) throws IOException {
			String s = new String(Arrays.copyOf(chars, length));
			if (!s.equals("\n"))
				log.log(level, s);
		}

		public void flush() throws IOException {
		}

		public void close() throws IOException {
		}
	}

	protected String getApplicationName() {
		return getClass().getSimpleName() + " Application";
	}

}
