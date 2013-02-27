package de.vksi.c4j.doclet.util;

import com.sun.javadoc.ClassDoc;

import de.vksi.c4j.doclet.analyzer.TypeHierarchy;

public class ContractChecker {

	public static boolean isContract(ClassDoc clazz) {
		if (clazz != null) {
			if(new C4JContractAnnotation(clazz).exists())
				return true;
			if(new C4JContractReferenceAnnotation(clazz).exists())
				return false;
			
			if (clazz.superclass() != null && !TypeHierarchy.isObject(clazz.superclass())) {
				C4JContractReferenceAnnotation c4jContractReferenceAnnotation = new C4JContractReferenceAnnotation(clazz.superclass());
				if(c4jContractReferenceAnnotation.exists() && clazz.name().equals(c4jContractReferenceAnnotation.getContractClassName()))
					return true;
				
				C4JContractAnnotation c4jContractAnnotation = new C4JContractAnnotation(clazz.superclass());
				if(c4jContractAnnotation.exists())
					return true;
			}

			ClassDoc[] superInterfaces = clazz.interfaces();
			if (superInterfaces.length > 0) {
				C4JContractReferenceAnnotation c4jContractReferenceAnnotation = new C4JContractReferenceAnnotation(superInterfaces[0]);
				if(c4jContractReferenceAnnotation.exists() && clazz.name().equals(c4jContractReferenceAnnotation.getContractClassName()))
					return true;
				
				C4JContractAnnotation c4jContractAnnotation = new C4JContractAnnotation(superInterfaces[0]);
				if(c4jContractAnnotation.exists())
					return true;
			}
		}
		return false;
	}
}
