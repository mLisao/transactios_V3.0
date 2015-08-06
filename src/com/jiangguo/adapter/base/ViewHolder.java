package com.jiangguo.adapter.base;

import android.util.SparseArray;
import android.view.View;

/**
 * Viewholder�ļ� ͨ��ViewHodler
 * 
 * @ClassName: ViewHolder
 * @Description: TODO
 * @author smile
 * @date 2014-5-28 ����9:56:29
 */
public class ViewHolder {

	@SuppressWarnings("unchecked")
	public static <T extends View> T get(View view, int id) {
		SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();// ȡ��viewholder
		if (viewHolder == null) {
			viewHolder = new SparseArray<View>();
			view.setTag(viewHolder);
		}
		View childView = viewHolder.get(id);
		if (childView == null) {
			childView = view.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (T) childView;
	}
}
