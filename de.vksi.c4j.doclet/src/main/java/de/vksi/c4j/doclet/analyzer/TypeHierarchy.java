package de.vksi.c4j.doclet.analyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sun.javadoc.ClassDoc;

/**
 * @author fmeyerer
 *
 */
public class TypeHierarchy {

	public static boolean isObject(ClassDoc clazz) {
		if (clazz == null)
			return false;

		return Object.class.getName().equals(clazz.qualifiedTypeName());
	}
	
	public List<ClassDoc> getAllSupertypesOf(ClassDoc type) {
		List<ClassDoc> supersWithGivenType = new ArrayList<ClassDoc>();
		supersWithGivenType.add(type);
		List<ClassDoc> allSupertypes0 = getAllSupertypes0(type, null);
		if(allSupertypes0 != null)
			supersWithGivenType.addAll(allSupertypes0);
		return supersWithGivenType;
	}
	
	private List<ClassDoc> getAllSupertypes0(ClassDoc type, List<ClassDoc> supers) {
		List<ClassDoc> superinterfaces = getAllSuperInterfacesOf(type);
		if (superinterfaces == null) // type is not part of the hierarchy
			return supers;
		if (superinterfaces.size() != 0) {
			if (supers == null)
				supers = new ArrayList<ClassDoc>();
			addAllCheckingDuplicates(supers, superinterfaces);
			for (ClassDoc currInterface : superinterfaces) {
				supers = getAllSuperInterfaces0(currInterface, supers);
			}
		}
		ClassDoc superclass = getSuperclassOf(type);
		if (superclass != null) {
			if (supers == null)
				supers = new ArrayList<ClassDoc>();
			supers.add(superclass);
			supers = getAllSupertypes0(superclass, supers);
		}
		return supers;
	}
	
	private ClassDoc getSuperclassOf(ClassDoc type) {
		return type.superclass();
	}

	private List<ClassDoc> getAllSuperInterfacesOf(ClassDoc type) {
		return Arrays.asList(type.interfaces());
	}

	private void addAllCheckingDuplicates(List<ClassDoc> list, List<ClassDoc> collection) {
		for (ClassDoc classDoc : collection) {
			if (!list.contains(classDoc)) {
				list.add(classDoc);
			}
		}
	}
	
	private List<ClassDoc> getAllSuperInterfaces0(ClassDoc type, List<ClassDoc> supers) {
		List<ClassDoc> superinterfaces = getAllSuperInterfacesOf(type);
		if (superinterfaces == null) // type is not part of the hierarchy
			return supers;
		if (superinterfaces.size() != 0) {
			if (supers == null)
				supers = new ArrayList<ClassDoc>();
			addAllCheckingDuplicates(supers, superinterfaces);
			for (ClassDoc currInterface : superinterfaces) {
				supers = getAllSuperInterfaces0(currInterface, supers);
			}
		}
		ClassDoc superclass = getSuperclassOf(type);
		if (superclass != null) {
			supers = getAllSuperInterfaces0(superclass, supers);
		}
		return supers;
	}
}
