package com.jiangguo.transactios.util;

import java.util.Comparator;

import com.jiangguo.bean.MyUser;


public class PinyinComparator implements Comparator<MyUser> {

	public int compare(MyUser o1, MyUser o2) {
		if (o1.getSortLetters().equals("@") || o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
