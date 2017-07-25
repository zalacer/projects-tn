package ex22;

import static v.ArrayUtils.rangeInteger;

import ds.LinkedList;

public class Ex2218LinkedListShuffling {

  /* p286
  2.2.18 Shuffling a linked list. Develop and implement a divide-and-conquer algo-
  rithm that randomly shuffles a linked list in linearithmic time and logarithmic extra
  space.
  
  I implemented an algorithm as follows:
  1. Recursively split the node list in the input LinkedList into two sublists of as 
     equal lengths as possible until it has just one element and then return that node.
  2. Merge pairs of sublists from the bottom up using probabilities based on their 
     lengths which are recalculated after each node is merged. Return the merged node 
     list and its last node.
  3. When all the merging is done set the input LinkedList.first to the head of the last 
     returned merged node list and set LinkedList.last to its last element that was returned 
     with it.
     
  This algorithm is like mergesort except rather than sorting during each merge it shuffles
  based on comparison of a double random value with leftListSize/(leftListSize+rightListSize).
  Like mergesort it does ~NlnN comparisons. Step 2 creates a new Node to act as the head of
  the merged list being constructed. This new Node is not set to null and will persist until 
  garbage collection. Since this step runs lnN times, space for that number of new nodes can be 
  used. 
  
  Implementation of this algorithm is ds.LinkedList.shuffleEx2218().
  
  More efficiently extra space could be reduced to that for one Node by creating it globally 
  and reusing it in merge(). ds.LinkedList.shuffle() implements this.
  
  I also tried a version of shuffle that does only one merge on the two halves of the initial 
  list and found it does poor randomization. It's implemented in ds.LinkedList.shuffleWith1Merge.
 
  Looking at the code for java.util.Collections.shuffle() it converts a Collection to an
  array, shuffles it with Fisher–Yates and iterates it back into the Collection. I implemented
  this in ds.LinkedList.shuffleFisherYates.
    
  All four shuffle implementations use SecureRandom.getInstanceStrong() because testing showed 
  that pseudorandomization with a new Random() instance gave unacceptably biased results. 
   
  Testing shows ds.LinkedList.shuffle(), ds.LinkedList.shuffleEx2218() and ds.LinkedList.
  shuffleFisherYates produce about the same quality of shuffling, however for small LinkedLists, 
  shuffleFisherYates is about 7 times faster than the other two. Since shuffleFisherYates 
  requires ~N extra space, time to generate it and ~3N time overall in terms of array accesses, 
  as LinkedList size increases at some point it will make a difference and the shuffleFisherYates 
  impementation won't be fastest.
    
   */ 
  
  public static void main(String[] args) {
    
    LinkedList<Integer> ll = new LinkedList<Integer>(rangeInteger(0,21));
    LinkedList.shuffleFisherYates(ll);
    System.out.println(ll.toSimpleString()+"\n");
    
    /*
    testShuffle takes a String argument alg for the algorithm implementation and int 
    arguments n for the length of the list and trials for the number of trials. For 
    trials times it creates a LinkedList with Integer elements in the the range [0,n) 
    and shuffles it. Then it prints the total elapsed time, the bias matrix, stats for 
    each row of the bias matrix, overall bias and max bias. Bias is deviation from 
    uniform randomization, which if achieved means that the probability of the value 
    in any position of the initial list being shuffled to any position in the final 
    list is independent of the positions. In other words if 0 is the value of the item 
    at the head of the initial list, then after shuffling it should wind up in any 
    position of the final list with equal probability. The bias matrix contains the 
    counts of each starting element in each position in the final list over all trials
    by row where its first row contains the counts of the first element in the starting
    list being shuffled to the first and all other positions in the final list and so on.
    Ideally the value of each element in the bias matrix should be trials/n and deviation
    from that positive or negative is the bias from position i to j when the indices of
    element is bias[i,j]. If randomization is uniform the average bias/trials should 
    decrease as trials increases even though the average bias itself increases and the
    deviation from the bias should be about the same for all positions.

    valid values for alg are shuffle, shuffleEx2218, shuffleFisherYates and shuffleWith1Merge */

    LinkedList.testShuffle("shuffle", 10, 10000); // shuffle a list of 10 elements 10K times


    /*

      Shuffling a 10 element list 10000 times using shuffle()
      
      Original LinkedList = 0 1 2 3 4 5 6 7 8 9 
      
      elapsed time = 47424 ms
      
      bias matrix
      [985,991,994,986,1020,972,1031,1002,1039,980]
      [985,965,953,994,1013,991,999,1032,1043,1025]
      [1010,961,976,1010,1024,1033,1014,1004,983,985]
      [998,1045,1048,1008,978,993,979,967,979,1005]
      [977,1024,967,999,1055,1016,999,974,986,1003]
      [1014,1049,956,1039,937,984,997,1021,1013,990]
      [954,995,1013,992,1018,1012,1021,984,961,1050]
      [984,1002,991,1007,1014,1045,972,1051,993,941]
      [1044,994,1040,979,995,951,997,1022,983,995]
      [1049,974,1062,986,946,1003,991,943,1020,1026]
      
      stats per row of bias matrix
      (min max mean stddev)
      [972.0,1039.0,1000.0,22.627416997969522]
      [953.0,1043.0,1000.0,28.759539480163987]
      [961.0,1033.0,1000.0,22.82299035816492]
      [967.0,1048.0,1000.0,27.740864362084242]
      [967.0,1055.0,1000.0,26.579022638999433]
      [937.0,1049.0,1000.0,34.95711658589707]
      [954.0,1050.0,1000.0,28.982753492378876]
      [941.0,1051.0,1000.0,32.5337431668175]
      [951.0,1044.0,1000.0,28.374479926707224]
      [943.0,1062.0,1000.0,40.06661120351125]
      
      overall stats
      (min max mean stddev)
      [937.0,1062.0,1000.0,28.391562269111013]
      
      max bias = -63.0
      max bias/trials % = -0.63
      
      
      Shuffling a 10 element list 10000 times using shuffleEx2218()
      
      Original LinkedList = 0 1 2 3 4 5 6 7 8 9 
      
      elapsed time = 47269 ms
      
      bias matrix
      [999,1012,947,989,964,1052,1039,978,1016,1004]
      [1034,993,1050,939,992,963,1012,975,1031,1011]
      [1018,951,985,1037,992,992,983,1056,1000,986]
      [990,1029,989,983,974,1004,1026,1024,1008,973]
      [1010,989,992,1036,986,988,993,998,1009,999]
      [996,973,1006,1030,1075,971,990,971,962,1026]
      [933,1046,992,1009,995,973,1001,993,1038,1020]
      [1040,984,1014,975,1019,984,1046,982,963,993]
      [1007,999,1007,964,1011,1052,948,1015,1001,996]
      [973,1024,1018,1038,992,1021,962,1008,972,992]
      
      stats per row of bias matrix
      (min max mean stddev)
      [947.0,1052.0,1000.0,32.23524640996422]
      [939.0,1050.0,1000.0,34.49637662132068]
      [951.0,1056.0,1000.0,29.940682097180826]
      [973.0,1029.0,1000.0,21.31248981752771]
      [986.0,1036.0,1000.0,15.114378731672845]
      [962.0,1075.0,1000.0,35.289280714309456]
      [933.0,1046.0,1000.0,32.31442746239243]
      [963.0,1046.0,1000.0,28.189832682487964]
      [948.0,1052.0,1000.0,28.21740991342441]
      [962.0,1038.0,1000.0,25.67748689676101]
      
      overall stats
      (min max mean stddev)
      [933.0,1075.0,1000.0,27.548304683030693]
      
      max bias = 75.0
      max bias/trials % = 0.75
      
      
      Shuffling a 10 element list 10000 times using shuffleFisherYates()
      
      Original LinkedList = 0 1 2 3 4 5 6 7 8 9 
      
      elapsed time = 6575 ms
      
      bias matrix
      [1001,1011,1025,935,947,1028,973,1025,1033,1022]
      [981,975,1049,990,1043,937,1009,997,1002,1017]
      [1073,968,973,938,1006,1004,1013,1046,1016,963]
      [961,980,1027,1007,1033,1043,966,964,1014,1005]
      [1030,1014,1016,1034,1015,995,985,1003,949,959]
      [945,1028,961,1085,1016,964,1008,957,1000,1036]
      [1007,1009,1004,1011,996,945,1005,1039,980,1004]
      [991,1025,960,952,940,1051,1034,1025,1038,984]
      [1038,1002,1004,998,997,1027,1003,955,962,1014]
      [973,988,981,1050,1007,1006,1004,989,1006,996]
      
      stats per row of bias matrix
      (min max mean stddev)
      [935.0,1033.0,1000.0,35.73358208868639]
      [937.0,1049.0,1000.0,32.80921279830475]
      [938.0,1073.0,1000.0,40.672130780452385]
      [961.0,1043.0,1000.0,30.38640046687553]
      [949.0,1034.0,1000.0,28.429249413627193]
      [945.0,1085.0,1000.0,43.86342439892262]
      [945.0,1039.0,1000.0,24.19825521717538]
      [940.0,1051.0,1000.0,39.84971769034255]
      [955.0,1038.0,1000.0,25.560386016907753]
      [973.0,1050.0,1000.0,21.10292238845922]
      
      overall stats
      (min max mean stddev)
      [935.0,1085.0,1000.0,31.52968778978333]
      
      max bias = 85.0
      max bias/trials % = 0.85
      
      
      Shuffling a 10 element list 10000 times using shuffleWith1Merge()
      
      Original LinkedList = 0 1 2 3 4 5 6 7 8 9 
      
      elapsed time = 14186 ms
      
      bias matrix
      [4951,2828,1385,608,184,44,0,0,0,0]
      [0,2189,2806,2397,1590,778,240,0,0,0]
      [0,0,774,1817,2451,2363,1765,830,0,0]
      [0,0,0,222,785,1605,2330,2794,2264,0]
      [0,0,0,0,33,174,598,1347,2777,5071]
      [5049,2762,1415,552,189,33,0,0,0,0]
      [0,2221,2784,2404,1584,800,207,0,0,0]
      [0,0,836,1772,2350,2430,1807,805,0,0]
      [0,0,0,228,790,1577,2463,2790,2152,0]
      [0,0,0,0,44,196,590,1434,2807,4929]
      
      stats per row of bias matrix
      (min max mean stddev)
      [0.0,4951.0,1000.0,1661.112946858889]
      [0.0,2806.0,1000.0,1135.3878436708558]
      [0.0,2451.0,1000.0,1016.3037603656367]
      [0.0,2794.0,1000.0,1135.417984708715]
      [0.0,5071.0,1000.0,1687.5408801632693]
      [0.0,5049.0,1000.0,1682.0881202970445]
      [0.0,2784.0,1000.0,1137.9620185030585]
      [0.0,2430.0,1000.0,1009.9259598824285]
      [0.0,2790.0,1000.0,1137.4721486211822]
      [0.0,4929.0,1000.0,1653.9211992514436]
      
      overall stats
      (min max mean stddev)
      [0.0,5071.0,1000.0,1293.0538201552872]
      
      max bias = 4071.0
      max bias/trials % = 40.71


    */
    
  }
  
}

