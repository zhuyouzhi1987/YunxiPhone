package com.yunxi.phone.utils;

import com.yunxi.phone.bean.SortModel;

import java.util.Comparator;

/**
 * 
 * @author xiaanming
 *
 */
public class PinyinComparator implements Comparator<SortModel> {

	public int compare(SortModel o1, SortModel o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			if((o1.getSortLetters().compareTo(o2.getSortLetters()))==0){
				return o1.getName().compareTo(o2.getName());
			}else{
				return o1.getSortLetters().compareTo(o2.getSortLetters());
			}
		}
	}

}
