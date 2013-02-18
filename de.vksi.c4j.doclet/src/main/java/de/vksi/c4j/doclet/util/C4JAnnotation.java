package de.vksi.c4j.doclet.util;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationDesc.ElementValuePair;
import com.sun.javadoc.ClassDoc;

public abstract class C4JAnnotation {
	private static final int FIRST_MEMBER_VALUE_PAIR = 0;
	private ClassDoc clazz;

	public C4JAnnotation(ClassDoc clazz) {
		this.clazz = clazz;
	}

	public boolean exists() {
		return getAnnotation() != null;
	}

	public AnnotationDesc getAnnotation() {
		if (clazz == null)
			return null;

		AnnotationDesc[] annotations = clazz.annotations();
		for (AnnotationDesc annotation : annotations) {
			if (c4jAnnotationType().equals(annotation.annotationType().toString())) {
				return annotation;
			}
		}

		return null;
	}

	public abstract String c4jAnnotationType();

	public boolean hasValue() {
		if (exists()) {
			if (getAnnotation().elementValues().length > 0) {
				return true;
			}
		}
		return false;
	}
	

	public String getAnnotationValue() {
		if (hasValue()) {
			ElementValuePair memberValuePair = getAnnotation().elementValues()[FIRST_MEMBER_VALUE_PAIR];
			return memberValuePair.value().toString();
		}
		return "";
	}

	// protected ClassDoc findContractClass(String contractClass) throws
	// JavaModelException {
	// ClassDoc contractType = findTypeByImports(contractClass);
	// // if contract-compilation unit is located in the same package, an
	// // import statement will not be required
	// return contractType != null ? contractType :
	// findTypeInSamePackage(contractClass);
	// }
	//
	// private ClassDoc findTypeByImports(String contract) throws
	// JavaModelException {
	// IImportDeclaration[] imports = type.getCompilationUnit().getImports();
	// for (IImportDeclaration currentImport : imports) {
	// if (currentImport.getElementName().endsWith("." + contract)
	// || currentImport.getElementName().equals(contract)) {
	// return type.getJavaProject().findType(currentImport.getElementName());
	// }
	// }
	// return null;
	// }
	//
	// private IType findTypeInSamePackage(String contract) throws
	// JavaModelException {
	// IPackageFragment[] packageFragments =
	// type.getJavaProject().getPackageFragments();
	// for (IPackageFragment packageFragment : packageFragments) {
	// if
	// (type.getPackageFragment().getElementName().equals(packageFragment.getElementName()))
	// {
	// ICompilationUnit compilationUnitInSamePackage =
	// packageFragment.getCompilationUnit(contract + ".java");
	// if (compilationUnitInSamePackage.exists()) {
	// return compilationUnitInSamePackage.getTypes()[0];
	// }
	// }
	// }
	// return null;
	// }

}