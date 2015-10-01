package es.uvigo.ei.sing.bew.util;

import java.util.Comparator;

public class StringComparatorFirst implements Comparator<String> {
	@Override
	public int compare(String str1, String str2) {
		String array1[] = str1.split("\t");
		String array2[] = str2.split("\t");

		return array1[0].compareTo(array2[0]);
	}
}
