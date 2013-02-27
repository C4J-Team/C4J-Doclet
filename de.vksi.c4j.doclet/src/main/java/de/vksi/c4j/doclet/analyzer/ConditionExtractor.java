package de.vksi.c4j.doclet.analyzer;

import static de.vksi.c4j.doclet.util.C4JDocletConstants.PATH_DELIMITER;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.tools.doclets.internal.toolkit.Configuration;

import de.vksi.c4j.doclet.util.C4JConditions;
import de.vksi.c4j.doclet.util.TargetContractMap;

/**
 * Extracts conditions (pre-, post, invariants) for a given element
 * 
 * @author fmeyerer
 * 
 */
public class ConditionExtractor {
	private C4JConditions conditions;

	private ClassDoc clazz;
	private TargetContractMap targetContractMap;
	private Configuration configuration;

	public ConditionExtractor(ClassDoc clazz, TargetContractMap targetContractMap, Configuration configuration) {
		this.clazz = clazz;
		this.targetContractMap = targetContractMap;
		this.configuration = configuration;
		conditions = new C4JConditions();
	}

	public void extractConditionsOf(ProgramElementDoc element) {
		TypeHierarchy typeHierarchy = new TypeHierarchy();
		List<ClassDoc> allSupertypes = typeHierarchy.getAllSupertypesOf(clazz);
		Collections.reverse(allSupertypes);

		searchTypeHierarchyForContractConditions(element, allSupertypes);
	}

	private void searchTypeHierarchyForContractConditions(ProgramElementDoc element,
			List<ClassDoc> allSupertypes) {
		for (ClassDoc classDoc : allSupertypes) {
			searchTypeForContractConditions(element, classDoc);
		}
	}

	private void searchTypeForContractConditions(ProgramElementDoc element, ClassDoc classDoc) {
		if (targetContractMap.containsTarget(classDoc)) {
			List<ClassDoc> contracts = targetContractMap.getContractFor(classDoc);
			for (ClassDoc contract : contracts) {
				File contractSourceFile = getContractSourceFile(contract);
				C4JConditions conditionsToMerge = parseContract(element, contractSourceFile);

				if (preConditionsAlreadyDefined(classDoc, conditionsToMerge)) {
					addWarningToPreconditions(contract);
					conditionsToMerge.setPreConditions(new ArrayList<String>());
				}

				conditions.mergeWith(conditionsToMerge);
			}
		}
	}

	private boolean preConditionsAlreadyDefined(ClassDoc classDoc, C4JConditions conditionsToMerge) {
		return hasSupertype(classDoc) && conditions.hasPreConditions()
				&& !TypeHierarchy.isObject(classDoc.superclass())
				&& !conditionsToMerge.getConditions(C4JConditions.PRE_CONDITIONS).isEmpty();
	}

	private File getContractSourceFile(ClassDoc contract) {
		File contractSourceFile = getSourceFileOf(contract);
		return contractSourceFile;
	}

	private File getSourceFileOf(ClassDoc contract) {
		if (contract != null) {
			String sourcepath = configuration.sourcepath;
			String[] paths = sourcepath.split(PATH_DELIMITER);

			File sourceFile = null;
			for (String path : paths) {
				String fullFilePath = path + File.separator
						+ contract.qualifiedName().replace(".", File.separator) + ".java";
				sourceFile = new File(fullFilePath);
				if (sourceFile != null && sourceFile.exists()) {
					return sourceFile;
				}
			}
		}
		return null;
	}

	private C4JConditions parseContract(ProgramElementDoc element, File contractSourceFile) {
		if (contractSourceFile != null) {
			try {
				CompilationUnit cu = JavaParser.parse(contractSourceFile);
				if (element instanceof ClassDoc)
					return extractInvariants(cu);
				else if (element instanceof ConstructorDoc)
					return extractContructorConditions(element, cu);
				else if (element instanceof MethodDoc)
					return extractMethodConditions(element, cu);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new C4JConditions();
	}

	private C4JConditions extractInvariants(CompilationUnit cu) {
		InvariantVisitor iVisitor = new InvariantVisitor();
		iVisitor.visit(cu);
		C4JConditions conditionsToMerge = iVisitor.getConditions();
		return conditionsToMerge;
	}

	private C4JConditions extractContructorConditions(ProgramElementDoc element, CompilationUnit cu) {
		ConstructorVisitor cVisitor = new ConstructorVisitor();
		cVisitor.visit(cu, (ConstructorDoc) element);
		C4JConditions conditionsToMerge = cVisitor.getConditions();
		return conditionsToMerge;
	}

	private C4JConditions extractMethodConditions(ProgramElementDoc element, CompilationUnit cu) {
		MethodVisitor mVisitor = new MethodVisitor((MethodDoc) element);
		mVisitor.visit(cu, null);
		C4JConditions conditionsToMerge = mVisitor.getConditions();
		return conditionsToMerge;
	}

	private boolean hasSupertype(ClassDoc classDoc) {
		return classDoc.superclassType() != null || classDoc.interfaces().length > 0;
	}

	public List<String> getPreConditions() {
		return conditions.getConditions(C4JConditions.PRE_CONDITIONS);
	}

	public void setPreConditions(List<String> preConditions) {
		conditions.setPreConditions(preConditions);
	}

	public List<String> getPostConditions() {
		return conditions.getConditions(C4JConditions.POST_CONDITIONS);
	}

	public void setPostConditions(List<String> postConditions) {
		conditions.setPostConditions(postConditions);
	}

	public List<String> getInvariantConditions() {
		return conditions.getConditions(C4JConditions.INVARIANT_CONDITIONS);
	}

	public void setInvariantConditions(List<String> invariants) {
		conditions.setInvariantConditions(invariants);
	}

	private void addWarningToPreconditions(ClassDoc classDoc) {
		String relatedContractName = classDoc.typeName();
		String warning = MessageFormat.format("<br>WARNING: Found strengthening pre-condition "
				+ "in Contract ''{0}'' which is already defined from its super Contract "
				+ "- ignoring the pre-condition", relatedContractName);
		conditions.addWaringToConditions(C4JConditions.PRE_CONDITIONS, warning);
	}
}
