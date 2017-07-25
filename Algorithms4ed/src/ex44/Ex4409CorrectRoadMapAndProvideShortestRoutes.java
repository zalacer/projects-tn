package ex44;

/* p685
  4.4.9  The table below, from an old published road map, purports to 
  give the length of the shortest routes connecting the cities. It 
  contains an error. Correct the table. Also, add a table that shows 
  how to achieve the shortest routes.
  
              Providence  Westerly  New London  Norwich
  Providence  -           53        54          48
  Westerly    53          -         18          101
  New London  54          18        -           12
  Norwich     48          101       12          -
  
  Actual approximate SP distances (from Google Maps on 20170507)
  (read table below only from the left, i.e. SP distance Providence->Westerly 
    = 43.2 but SP distance Westerly->Providence = 44.4)
    
              Providence  Westerly  New London  Norwich
  Providence  -           43.2      56.8        49.8
  Westerly    44.4        -         17          18.6     
  New London  56.5        15.8      -           13.7
  Norwich     49.7        19.2      15          -
  
  Actual approximate SP routes (from Google Maps on 20170507)
  (read table below only from the left)
    
              Providence       Westerly     New London       Norwich
  Providence  -                NE Regional  NE Regional      I-95S & CT-165W
  Westerly    NE Regional      -            NE Regional      CT-2E   
  New London  I-95N            I-95N        -                I-395N
  Norwich     CT-165E & 1-95N  CT-2E        I-395S & CT-32s  -
  
  Based on data from Google Maps, the glaring error in the provided table is 
  the distance of 101 between Norwich and Westerly and that should be 18.9
  on average.
 
 */  

public class Ex4409CorrectRoadMapAndProvideShortestRoutes {

  
	public static void main(String[] args) {
	
	}

}


