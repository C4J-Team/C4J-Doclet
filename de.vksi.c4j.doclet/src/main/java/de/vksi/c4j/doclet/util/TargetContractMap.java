package de.vksi.c4j.doclet.util;

import static de.vksi.c4j.doclet.util.C4JDocletConstants.ANNOTATION_CONTRACT;
import static de.vksi.c4j.doclet.util.C4JDocletConstants.ANNOTATION_CONTRACT_REFERENCE;

import java.util.HashMap;
import java.util.Map;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;

/**
 * @author fmeyerer
 *
 */
public class TargetContractMap {
	private static final String FILE_EXTENSION = ".class";
	private static final String VALUE_DELIMITER = "=";
	private Map<ClassDoc, ClassDoc> map;

	public TargetContractMap() {
		map = new HashMap<ClassDoc, ClassDoc>();
	}

	public TargetContractMap(RootDoc root) {
		this();
		generateMapping(root);
	}

	public void put(ClassDoc target, ClassDoc contract) {
		map.put(target, contract);
	}

	public ClassDoc getContractFor(ClassDoc target) {
		return map.get(target);
	}

	public boolean containsTarget(ClassDoc target) {
		return map.containsKey(target);
	}

	public boolean isContractClass(ClassDoc contract) {
		return map.containsValue(contract);
	}

	//TODO: write unit tests to verify mapping
	private void generateMapping(RootDoc root) {
		ClassDoc[] classes = root.classes();
		
		//TODO: refactoring -> create classes for c4j annotaion checking
		for (ClassDoc clazz : classes) {
			AnnotationDesc[] annotations = clazz.annotations();
			for (AnnotationDesc annotation : annotations) {
				if (isContractReferenceAnnotation(annotation)) {
					String annotationValue = getAnnotationValue(annotation);
					ClassDoc contract = root.classNamed(annotationValue);
					put(clazz, contract);
				} else if (isContractAnnotation(annotation)) {
					if(hasValue(annotation)){
						String annotationValue = getAnnotationValue(annotation);
						ClassDoc target = root.classNamed(annotationValue);
						put(target, clazz);
						continue;
					}
					
					if(clazz.superclass() != null && !isObject(clazz)){
						ClassDoc superclass = clazz.superclass();
						put(superclass, clazz);
					}
					
					//A contract has exactly one target. For that reason
					//(if more than one interfaces will be implemented)
					//the first interface is considered as target 
					if(clazz.interfaces().length > 0){
						ClassDoc superinterface = clazz.interfaces()[0];
						put(superinterface, clazz);
						continue;
					}
				}
			}
		}
	}

	private boolean isObject(ClassDoc clazz) {
		return Object.class.getName().equals(clazz.superclass().qualifiedTypeName());
	}

	private boolean isContractAnnotation(AnnotationDesc annotation) {
		return annotation.annotationType().toString().equals(ANNOTATION_CONTRACT);
	}

	private boolean isContractReferenceAnnotation(AnnotationDesc annotation) {
		return annotation.annotationType().toString().equals(ANNOTATION_CONTRACT_REFERENCE);
	}

	private String getAnnotationValue(AnnotationDesc annotation) {
		if (hasValue(annotation)) {
			String annotationValue = annotation.elementValues()[0].toString();
			annotationValue = annotationValue.substring(annotationValue.indexOf(VALUE_DELIMITER) + 1,
					annotationValue.indexOf(FILE_EXTENSION));
			return annotationValue.trim();
		}
		return "";
	}

	private boolean hasValue(AnnotationDesc annotation) {
		return annotation.elementValues().length > 0;
	}
}
