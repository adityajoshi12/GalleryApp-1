package com.davidballa.galleryapp;

import java.util.Comparator;

public class DateComparator implements Comparator<SortableImage> {

	@Override
	public int compare(SortableImage lhs, SortableImage rhs) {
		return rhs.getLastModDate().compareTo(lhs.getLastModDate());
	}

}
