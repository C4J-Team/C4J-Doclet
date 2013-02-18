package de.vksi.c4j.doclet.util;

import static de.vksi.c4j.doclet.util.C4JDocletConstants.ANNOTATION_CONTRACT_REFERENCE;

import com.sun.javadoc.ClassDoc;

public class C4JContractReferenceAnnotation extends C4JAnnotation {
	private static final String FILE_EXTENSION = ".class";
	private static final String VALUE_DELIMITER = "=";
	
	public C4JContractReferenceAnnotation(ClassDoc clazz) {
		super(clazz);
	}

	public String getContractClassName() {
		if (exists() && hasValue()){
				String annotationValue = getAnnotationValue().toString();
				annotationValue = annotationValue.substring(annotationValue.indexOf(VALUE_DELIMITER) + 1,
						annotationValue.indexOf(FILE_EXTENSION));
				return annotationValue.trim();
		}
		return "";
	}

	@Override
	public String c4jAnnotationType() {
		return ANNOTATION_CONTRACT_REFERENCE;
	}
}
