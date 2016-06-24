
package com.mapopolis.viewer.utils;

import java.util.Vector;



public class QSortAlgorithm extends SortAlgorithm

{
	public static Vector sort(Vector a)
                                          
    {
            int n = a.size();
                
			ISortable[] objs = new ISortable[n];
			
			for ( int i = 0; i < n; ++i )
				objs[i] = (ISortable) a.elementAt(i);
			
			(new QSortAlgorithm()).sort(objs);
			
			Vector vo = new Vector();
			
			for ( int i = 0; i < n; ++i )
				vo.addElement(objs[i]);
			
			return vo;
    }
	
   
   /** This is a generic version of C.A.R Hoare's Quick Sort
    * algorithm.  This will handle arrays that are already
    * sorted, and arrays with duplicate keys.<BR>
    *
    * If you think of a one dimensional array as going from
    * the lowest index on the left to the highest index on the right
    * then the parameters to this function are lowest index or
    * left and highest index or right.  The first time you call
    * this function it will be with the parameters 0, a.length - 1.
    *
    * @param a       an integer array
    * @param lo0     left boundary of array partition
    * @param hi0     right boundary of array partition
    */
   void QuickSort(ISortable a[], int lo0, int hi0)
   {
      int lo = lo0;
      int hi = hi0;
      ISortable mid;

      if ( hi0 > lo0 )
      {

         /* Arbitrarily establishing partition element as the midpoint of
          * the array.
          */
         mid = a[ ( lo0 + hi0 ) / 2 ];

         // loop through the array until indices cross
         while( lo <= hi )
         {
            /* find the first element that is greater than or equal to
             * the partition element starting from the left Index.
             */
	     while( ( lo < hi0 ) && ( a[lo].compareTo(mid) < 0 ) )
		 ++lo;

            /* find an element that is smaller than or equal to
             * the partition element starting from the right Index.
             */
	     while( ( hi > lo0 ) && ( a[hi].compareTo(mid) > 0 ) )
		 --hi;

            // if the indexes have not crossed, swap
            if( lo <= hi )
            {
               swap(a, lo, hi);
               ++lo;
               --hi;
            }
         }

         /* If the right index has not reached the left side of array
          * must now sort the left partition.
          */
         if( lo0 < hi )
            QuickSort( a, lo0, hi );

         /* If the left index has not reached the right side of array
          * must now sort the right partition.
          */
         if( lo < hi0 )
            QuickSort( a, lo, hi0 );

      }
   }

   private void swap(ISortable a[], int i, int j)
   {
      ISortable T;
      T = a[i];
      a[i] = a[j];
      a[j] = T;
   }

   public void sort(ISortable a[])
   {
      QuickSort(a, 0, a.length - 1);
   }
}


class SortAlgorithm {
    /**
     * The sort item.
     */
    private SortItem parent;

    /**
     * When true stop sorting.
     */
    protected boolean stopRequested = false;

    /**
     * Set the parent.
     */
    public void setParent(SortItem p) {
	parent = p;
    }

    
   
    
    /**
     * Stop sorting.
     */
    public void stop() {
	stopRequested = true;
    }

    /**
     * Initialize
     */
    public void init() {
	stopRequested = false;
    }

    /**
     * This method will be called to
     * sort an array of integers.
     */
    void sort(int a[]) throws Exception {
    }
}

class SortItem
    
    implements Runnable {
    /**
     * The thread that is sorting (or null).
     */
    private Thread kicker;

    /**
     * The array that is being sorted.
     */
    int arr[];

    /**
     * The high water mark.
     */
    int h1 = -1;

    /**
     * The low water mark.
     */
    int h2 = -1;

    /**
     * The name of the algorithm.
     */
    String algName;

    /**
     * The sorting algorithm (or null).
     */
    SortAlgorithm algorithm;

 

   

    /**
     * Pause a while, and draw the low&high water marks.
     * @see SortAlgorithm
     */
    
    /**
     * Initialize the applet.
     */
    

   

    

    /**
     * Run the sorting algorithm. This method is
     * called by class Thread once the sorting algorithm
     * is started.
     * @see java.lang.Thread#run
     * @see SortItem#mouseUp
     */
    public void run() {
	try {
	    if (algorithm == null) {
		algorithm = (SortAlgorithm)Class.forName(algName).newInstance();
		algorithm.setParent(this);
	    }
	    algorithm.init();
	    algorithm.sort(arr);
	} catch(Exception e) {
	}
    }

    /**
     * Stop the applet. Kill any sorting algorithm that
     * is still sorting.
     */
    public synchronized void stop() {
	if (algorithm != null){
            try {
		algorithm.stop();
            } catch (IllegalThreadStateException e) {
                // ignore this exception
            }
            kicker = null;
	}
    }

    /**
     * For a Thread to actually do the sorting. This routine makes
     * sure we do not simultaneously start several sorts if the user
     * repeatedly clicks on the sort item.  It needs to be
     * synchronoized with the stop() method because they both
     * manipulate the common kicker variable.
     */
    private synchronized void startSort() {
	if (kicker == null || !kicker.isAlive()) {
	    kicker = new Thread(this);
	    kicker.start();
	}
    }

  //1.1 event handling

  
  /**
   * The user clicked in the applet. Start the clock!
   */

 

  public String getAppletInfo() {
    return "Title: ImageMap \nAuthor: James Gosling 1.17f, 10 Apr 1995 \nA simple applet class to demonstrate a sort algorithm.  \nYou can specify a sorting algorithm using the 'alg' attribyte.  \nWhen you click on the applet, a thread is forked which animates \nthe sorting algorithm.";
  }

  public String[][] getParameterInfo() {
    String[][] info = {
      {"als", "string", "The name of the algorithm to run.  You can choose from the provided algorithms or suppply your own, as long as the classes are runnable as threads and their names end in 'Algorithm.'  BubbleSort is the default.  Example:  Use 'QSort' to run the QSortAlgorithm class."}
    };
    return info;
  }
}