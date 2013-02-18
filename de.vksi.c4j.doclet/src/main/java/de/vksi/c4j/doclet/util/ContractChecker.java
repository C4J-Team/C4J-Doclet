package de.vksi.c4j.doclet.util;

import static de.vksi.c4j.doclet.util.C4JDocletConstants.ANNOTATION_CONTRACT;
import static de.vksi.c4j.doclet.util.C4JDocletConstants.ANNOTATION_CONTRACT_REFERENCE;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;

public class ContractChecker {

	public static boolean isContract(ClassDoc clazz) {
		if (clazz != null) {
			for (AnnotationDesc annotation : clazz.annotations()) {
				if (isContractReferenceAnnotation(annotation))
					return false;
				else if (isContractAnnotation(annotation))
					return true;
				else if (clazz.superclassType() != null)
					return isContract(clazz.superclass());
				else if (clazz.interfaces().length > 0)
					return isContract(clazz.interfaces()[0]);
				else
					return false;
			}
		}
		return false;
	}

	private static boolean isContractReferenceAnnotation(AnnotationDesc annotation) {
		return annotation.annotationType().toString().equals(ANNOTATION_CONTRACT_REFERENCE);
	}

	private static boolean isContractAnnotation(AnnotationDesc annotation) {
		return annotation.annotationType().toString().equals(ANNOTATION_CONTRACT);
	}
}
