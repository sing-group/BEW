package es.uvigo.ei.sing.bew.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import es.uvigo.ei.aibench.core.Core;
import es.uvigo.ei.aibench.core.clipboard.ClipboardItem;
import es.uvigo.ei.aibench.core.clipboard.ClipboardListener;

/**
 * Class to de/activate the operations in AIBench according to the necessary
 * objects to complete them.
 * 
 * @author Gael Pérez Rodríguez
 * 
 */
class ClipboardBasedOperationActivator implements ClipboardListener {

	private Map<String, HashSet<Class>> opReq = new HashMap<String, HashSet<Class>>();

	/**
	 * Add a requirement to an operation.
	 * 
	 * @param uid
	 *            Operation uid.
	 * @param className
	 *            Required class to activate it.
	 */
	public void addRequirement(String uid, Class className) {
		HashSet<Class> reqs = opReq.get(uid);
		if (reqs == null) {
			reqs = new HashSet<Class>();
			reqs.add(className);
			opReq.put(uid, reqs);
			processClipboard();
		}
	}

	/**
	 * Check opearations and classes in the system.
	 */
	private void processClipboard() {
		for (String uid : opReq.keySet()) {
			boolean reqSatisfied = true;
			for (Class c : opReq.get(uid)) {
				if (Core.getInstance().getClipboard().getItemsByClass(c).size() == 0) {
					reqSatisfied = false;
					break;
				}
			}
			if (reqSatisfied) {
				Core.getInstance().enableOperation(uid);
			} else {
				Core.getInstance().disableOperation(uid);
			}
		}

	}

	/**
	 * 
	 */
	public void elementAdded(ClipboardItem arg0) {
		processClipboard();
	}

	/**
	 * 
	 */
	public void elementRemoved(ClipboardItem arg0) {
		processClipboard();
	}
}