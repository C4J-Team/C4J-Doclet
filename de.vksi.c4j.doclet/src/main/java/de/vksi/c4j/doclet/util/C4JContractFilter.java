package de.vksi.c4j.doclet.util;

import java.util.ArrayList;
import java.util.List;

import com.sun.javadoc.ClassDoc;

/**
 * @author fmeyerer
 *
 */
public class C4JContractFilter {
	
	public static List<ClassDoc> removeContractClasses(ClassDoc[] classes, TargetContractMap targetContractMap) {
		List<ClassDoc> filteredClasses = new ArrayList<ClassDoc>();

		for (ClassDoc clazz : classes) {
			if (!targetContractMap.isContractClass(clazz)) {
				filteredClasses.add(clazz);
			}
		}
		return filteredClasses;
	}

}
