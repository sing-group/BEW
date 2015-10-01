package es.uvigo.ei.sing.bew.util;

import java.util.Comparator;

public class StringComparatorSecond implements Comparator<String> {
	@Override
	public int compare(String str1, String str2) {
		String array1[] = str1.split("\t");
		String array2[] = str2.split("\t");
		
		return array1[1].compareTo(array2[1]);
	}
}
