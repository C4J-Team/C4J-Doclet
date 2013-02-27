package de.vksi.c4j.doclet.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;

import de.vksi.c4j.doclet.analyzer.TypeHierarchy;

/**
 * @author fmeyerer
 * 
 */
public class TargetContractMap {
	private Map<ClassDoc, List<ClassDoc>> map;
	private RootDoc root;

	public TargetContractMap() {
		map = new HashMap<ClassDoc, List<ClassDoc>>();
	}

	public TargetContractMap(RootDoc root) {
		this();
		this.root = root;
	}

	public List<ClassDoc> getContractFor(ClassDoc target) {
		return map.containsKey(target) ? map.get(target) : new ArrayList<ClassDoc>();
	}

	public boolean containsTarget(ClassDoc target) {
		return map.containsKey(target);
	}

	public boolean isContractClass(ClassDoc contract) {
		for (List<ClassDoc> valueList : map.values()) {
			if (valueList.contains(contract))
				return true;
		}
		return false;
	}

	public void generateMapping() {
		ClassDoc[] classes = root.classes();

		for (ClassDoc clazz : classes) {
			C4JContractReferenceAnnotation contractRefAnnotation = new C4JContractReferenceAnnotation(clazz);
			if (contractRefAnnotation.exists()) {
				mapTargetWithInternalContract(clazz, contractRefAnnotation);
				continue;
			}

			C4JContractAnnotation contractAnnotation = new C4JContractAnnotation(clazz);
			if(contractAnnotation.exists()){
				mapTargetWithExternalContract(clazz, contractAnnotation);
			}
		}
	}

	private void mapTargetWithInternalContract(ClassDoc clazz,
			C4JContractReferenceAnnotation contractRefAnnotation) {
		ClassDoc contract = root.classNamed(contractRefAnnotation.getContractClassName());
		addContractFor(clazz, contract);
	}

	private void mapTargetWithExternalContract(ClassDoc clazz, C4JContractAnnotation contractAnnotation) {
		if (contractAnnotation.hasValue()) {
			ClassDoc target = root.classNamed(contractAnnotation.getTargetClassName());
			addContractFor(target, clazz);
		}
		
		if (clazz.superclass() != null && !ContractChecker.isContract(clazz.superclass())
				&& !TypeHierarchy.isObject(clazz.superclass())) {
			ClassDoc superclass = clazz.superclass();
			addContractFor(superclass, clazz);
		}

		if (clazz.interfaces().length > 0) {
			for (ClassDoc superinterface : clazz.interfaces()) {
				addContractFor(superinterface, clazz);
			}
		}
	}

	public void addContractFor(ClassDoc target, ClassDoc contract) {
		if (contract == null || target == null)
			return;

		List<ClassDoc> listOfContracts = new ArrayList<ClassDoc>();
		listOfContracts.add(contract);
		addContractsFor(target, listOfContracts);
	}

	public void addContractsFor(ClassDoc target, List<ClassDoc> contracts) {
		if (contracts == null || target == null)
			return;

		if (map.containsKey(target))
			map.get(target).addAll(contracts);
		else
			map.put(target, contracts);
	}
}
