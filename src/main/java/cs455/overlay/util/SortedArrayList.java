/**
 * The following class has been adapted from :
 *  https://stackoverflow.com/questions/4031572/sorted-array-list-in-java
 *
 */
package cs455.overlay.util;

import java.util.*;

public class SortedArrayList<T> extends ArrayList<T> {

  @SuppressWarnings("unchecked")
  public void insert(T value) {
    add(value);
    Comparable<T> cmp = (Comparable<T>) value;
    for (int i = size()-1; i > 0 && cmp.compareTo(get(i-1)) < 0; i--)
      Collections.swap(this, i, i-1);
  }

}
