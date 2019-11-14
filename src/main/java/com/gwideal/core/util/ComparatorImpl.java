package com.gwideal.core.util;

import com.gwideal.core.entity.Menu;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by li_hongyu on 2015/03/16.
 */
public class ComparatorImpl implements Comparator<Menu>,Serializable {
  @Override
  public int compare(Menu o1, Menu o2) {
    String age1 = o1.getSeq();
    String age2 = o2.getSeq();
    if (age1.compareTo(age2) > 0) {
      return 1;
    } else if (age1.compareTo(age2) < 0) {
      return -1;
    } else {
      return 0;
    }
  }
}
