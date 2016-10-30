package ex22;

import static sort.Merges.compare;
import static sort.Merges.cutoff;

public class Ex2223EffectivenessOfMergeSortImprovements {

  /* p287
  2.2.23 Improvements. Run empirical studies to evaluate the effectiveness of each of the
  three improvements to mergesort that are described in the text (see Exercise 2.2.11).
  Also, compare the performance of the merge implementation given in the text with the
  merge described in Exercise 2.2.10. In particular, empirically determine the best value
  of the parameter that decides when to switch to insertion sort for small subarrays.
  
  I found 32 is the best cutoff length for topDown, meaning that for arrays of length
  under 32 processing should be done with insertion sort. For bottomUp 8 works best and
  for natural no cutoff is recommended.
  
  In sort.merge I wrote 17 variations of merge sorts. 7 for top-down, 5 for bottom up
  and 5 for natural. They are named topDownSuffix, bottupUpSuffix and naturalSuffix where
  Suffix is combinations of Co for cutoff, Fm for faster merge, Sm for skip merge and Ac for
  avoid copy in merge.  Ac applies only to top-down and the rest apply to all. Also in 
  sort.merge are two test methods called compare and cutoff. compare compares the run times
  of a pair of sort methods for random double arrays of lengths from 10^1-10^6 by increasing 
  powers of ten. cutoff similarly compares the run times a pair of sort methods for random
  double arrays of lengths from [1,200] with their cut parameters set to the array length.
  cutoff is designed to be run with one sort method not implementing a cutoff while the other
  one does.  Superficially the signature of all the sort methods includes a int cut parameter
  but only the ones with names including the Co suffix actually implement a cutoff. The 
  reason for uniformity of signature was to enable intefacing them easily with the cutoff 
  test method.
  
  Overall, testing showed that for my implementations topDownAcCoSm runs the fastest and its
  performance is closely matched with topDownCoFmSM. In others words the advantage of Ac
  (avoiding the copy in merge by switching a and aux in recursive code) is about the same
  as that of Fm (the faster merge as in exercise 2.2.10 on p285). This conclusion applies
  only to random double arrays, which are likely to have few if any duplicates.  
  
  Testing is setup to be done in main and some results are included below.
 
   */ 
 
  public static void main(String[] args) {
    
    /*
      the syntax of compare is: 
        compare(String alg1, int cutoff1, String alg2, cutoff2, int trials)
      valid alg strings are:
         1. bottomUp
         2. bottomUpCo
         3. bottomUpCoFmSm
         4. bottomUpFm
         5. bottomUpSm
         6. natural
         7. naturalCo
         8. naturalCoFmSm
         9. naturalFm
        10. naturalSm
        11. topDown
        12. topDownAc
        13. topDownAcCoSM
        14. topDownCo
        15. topDownCoFmSm
        16. topDownFm
        17. topDownSm 
        
     the syntax of cutoff is:
       compare(String alg1, int cutoff1, String alg2, cutoff2, int trials)
     cutoff accepts the same alg names listed above.
    */
    
    compare("topDownCoFmSm", 31,"topDownAcCoSm", 31, 100);
    
    cutoff("topDown", -1,"topDownCo", 31, 1000);
    
    /*    Some test results:

    for random Double arrays topDownCoFmSm cutoff 31 topDownAcCoSm cutoff 31
    array                      average times
    length        topDownCoFmSm     topDownAcCoSm
        10                0.020             0.040
       100                0.020             0.090
        1K                0.670             0.650
       10K                0.770             0.820
      100K               10.030             9.860
        1M              143.160           142.880

    for random Double arrays topDownCoFmSm cutoff 31 bottomUpCoFmSm cutoff 8
    array                      average times
    length        topDownCoFmSm    bottomUpCoFmSm
        10                0.020             0.040
       100                0.110             0.040
        1K                0.490             1.360
       10K                1.550             1.470
      100K               13.420            17.950
        1M              201.910           261.040

    for random Double arrays topDownCoFmSm cutoff 31 natural cutoff -1
    array                  average times
    length        topDownCoFmSm           natural
        10                0.040             0.050
       100                0.130             0.050
        1K                0.490             1.300
       10K                1.170             2.040
      100K               12.220            17.950
        1M              166.780           265.630
 
  for random Double arrays topDownCoFmSm cutoff 31 topDownAcCoSm cutoff 31
    array                  average times
    length        topDownCoFmSm     topDownAcCoSm
        10                0.010             0.030
       100                0.040             0.120
        1K                0.570             0.580
       10K                1.060             1.250
      100K               10.820            10.840
        1M              164.890           164.160
        
    for random Double arrays topDownCoFmSm cutoff 31 topDownAcCoSm cutoff 31
    array                  average times
    length        topDownCoFmSm     topDownAcCoSm
        10                0.010             0.010
       100                0.030             0.120
        1K                0.270             0.330
       10K                1.040             1.000
      100K               11.130            11.030
        1M              163.150           162.540

    for random Double arrays alg1 cutoff 31 alg2 cutoff 31
    array                  average times
    length        topDownCoFmSm     topDownAcCoSm
        10                0.020             0.030
       100                0.040             0.130
        1K                0.740             0.630
       10K                0.930             1.050
      100K               10.180            10.140
        1M              144.400           144.960

    for random Double arrays alg1 cutoff -1 alg2 cutoff 31
    array                  average times
    length              natural     topDownAcCoSm
        10                0.080             0.010
       100                0.030             0.140
        1K                1.600             0.470
       10K                1.520             1.080
      100K               16.080            13.620
        1M              254.390           214.330

    for random Double arrays alg1 cutoff -1 alg2 cutoff 31
    array                  average times
    length              natural         naturalCo
        10                0.050             0.040
       100                0.030             0.130
        1K                1.110             0.680
       10K                1.210             1.640
      100K               15.910            16.770
        1M              256.990           262.260

    for random Double arrays alg1 cutoff -1 alg2 cutoff 31
    array                  average times
    length             bottomUp        bottomUpCo
        10                0.040             0.020
       100                0.050             0.060
        1K                1.840             1.030
       10K                2.080             0.990
      100K               34.690            20.130
        1M              535.830           377.450

    for random Double arrays alg1 cutoff -1 alg2 cutoff 63
    array                  average times
    length              topDown         topDownCo
        10                0.060             0.060
       100                0.050             0.130
        1K                1.800             0.570
       10K                1.860             2.420
      100K               20.430            16.850
        1M              270.610           243.130

    for random Double arrays alg1 cutoff -1 alg2 cutoff 31
    array                  average times
    length              topDown         topDownCo
        10                0.080             0.020
       100                0.100             0.060
        1K                3.430             0.390
       10K                5.980             3.180
      100K               20.290            13.970
        1M              272.900           216.520


    for random Double arrays alg1 cutoff -1 alg2 cutoff 32
    array                  average times
    length              topDown         topDownCo
        10                0.090             0.030
       100                0.050             0.120
        1K                1.540             0.820
       10K                2.060             5.560
      100K               19.050            19.250
        1M              280.550           388.940

    for random Double arrays alg1 cutoff -1 alg2 cutoff 8
    array                  average times
    length              topDown         topDownCo
        10                0.060             0.120
       100                0.120             0.050
        1K                1.420             1.920
       10K                2.080             5.630
      100K               20.720            15.920
        1M              288.440           240.970
 
    for random Double arrays alg1 cutoff -1 alg2 cutoff 75
    array                  average times
    length              topDown         topDownCo
        10                0.080             0.030
       100                0.100             0.100
        1K                3.560             0.460
       10K                3.990             4.660
      100K               19.320            18.840
        1M              278.970           303.240

    for random Double arrays alg1 cutoff -1 alg2 cutoff 100
    array                  average times
    length              topDown         topDownCo
        10                0.080             0.020
       100                0.060             0.060
        1K                2.470             1.080
       10K                2.770             5.450
      100K               27.270            22.140
        1M              474.860           309.080

    for random Double arrays alg1 cutoff -1 alg2 cutoff 50
    array                  average times
    length              topDown         topDownCo
        10                0.080             0.040
       100                0.100             0.080
        1K                3.510             0.790
       10K                3.900             1.840
      100K               20.810            22.200
        1M              277.370           380.020

    for random Double arrays alg1 cutoff -1 alg2 cutoff 130
    array                  average times
    length              topDown         topDownCo
        10                0.070             0.050
       100                0.030             0.070
        1K                2.040             0.740
       10K                2.190             3.870
      100K               28.540            23.130
        1M              486.730           433.000


    for random Double arrays
    length                     average times
    cutoff              topDown         topDownCo
         1                0.003             0.001
         2                0.010             0.005
         3                0.000             0.001
         4                0.001             0.001
         5                0.005             0.001
         6                0.015             0.003
         7                0.011             0.003
         8                0.014             0.003
         9                0.016             0.008
        10                0.024             0.006
        11                0.019             0.010
        12                0.022             0.003
        13                0.014             0.003
        14                0.009             0.000
        15                0.005             0.003
        16                0.006             0.002
        17                0.007             0.002
        18                0.009             0.001
        19                0.003             0.003
        20                0.004             0.003
        21                0.004             0.004
        22                0.004             0.003
        23                0.005             0.002
        24                0.004             0.003
        25                0.005             0.000
        26                0.003             0.000
        27                0.002             0.003
        28                0.002             0.001
        29                0.003             0.004
        30                0.004             0.003
        31                0.007             0.002
        32                0.004             0.003
        33                0.003             0.005
        34                0.001             0.004
        35                0.005             0.007
        36                0.004             0.001
        37                0.004             0.003
        38                0.004             0.003
        39                0.008             0.004
        40                0.007             0.008
        41                0.004             0.008
        42                0.003             0.003
        43                0.008             0.005
        44                0.003             0.006
        45                0.004             0.005
        46                0.002             0.006
        47                0.006             0.006
        48                0.003             0.006
        49                0.003             0.006
        50                0.007             0.008
        51                0.004             0.008
        52                0.005             0.004
        53                0.005             0.006
        54                0.010             0.010
        55                0.002             0.010
        56                0.003             0.009
        57                0.002             0.008
        58                0.004             0.007
        59                0.004             0.007
        60                0.008             0.004
        61                0.005             0.006
        62                0.007             0.005
        63                0.007             0.005
        64                0.007             0.007
        65                0.006             0.005
        66                0.003             0.011
        67                0.003             0.010
        68                0.004             0.010
        69                0.004             0.010
        70                0.003             0.013
        71                0.003             0.010
        72                0.002             0.012
        73                0.002             0.012
        74                0.006             0.007
        75                0.006             0.011
        76                0.006             0.012
        77                0.002             0.015
        78                0.006             0.009
        79                0.008             0.009
        80                0.003             0.014
        81                0.003             0.017
        82                0.007             0.013
        83                0.006             0.012
        84                0.005             0.012
        85                0.008             0.011
        86                0.006             0.013
        87                0.005             0.015
        88                0.002             0.019
        89                0.008             0.012
        90                0.013             0.011
        91                0.009             0.013
        92                0.011             0.015
        93                0.010             0.017
        94                0.007             0.017
        95                0.009             0.017
        96                0.008             0.015
        97                0.007             0.014
        98                0.006             0.017
        99                0.004             0.022
       100                0.007             0.020
       101                0.006             0.022
       102                0.008             0.020
       103                0.011             0.016
       104                0.011             0.016
       105                0.009             0.021
       106                0.008             0.023
       107                0.011             0.015
       108                0.010             0.021
       109                0.008             0.022
       110                0.009             0.021
       111                0.009             0.022
       112                0.007             0.024
       113                0.007             0.022
       114                0.005             0.025
       115                0.015             0.017
       116                0.011             0.024
       117                0.010             0.026
       118                0.012             0.024
       119                0.010             0.022
       120                0.011             0.029
       121                0.012             0.027
       122                0.011             0.028
       123                0.009             0.029
       124                0.013             0.027
       125                0.013             0.026
       126                0.011             0.026
       127                0.010             0.031
       128                0.012             0.029
       129                0.043             0.042
       130                0.013             0.032
       131                0.008             0.039
       132                0.018             0.040
       133                0.011             0.040
       134                0.012             0.039
       135                0.010             0.039
       136                0.015             0.029
       137                0.012             0.034
       138                0.012             0.037
       139                0.013             0.034
       140                0.017             0.032
       141                0.008             0.043
       142                0.018             0.031
       143                0.021             0.029
       144                0.016             0.039
       145                0.013             0.040
       146                0.020             0.030
       147                0.017             0.039
       148                0.023             0.032
       149                0.009             0.041
       150                0.015             0.034
       151                0.017             0.036
       152                0.017             0.042
       153                0.016             0.053
       154                0.023             0.053
       155                0.023             0.058
       156                0.016             0.054
       157                0.019             0.061
       158                0.023             0.053
       159                0.026             0.051
       160                0.028             0.050
       161                0.028             0.058
       162                0.024             0.041
       163                0.024             0.047
       164                0.022             0.046
       165                0.018             0.050
       166                0.012             0.061
       167                0.018             0.053
       168                0.020             0.056
       169                0.018             0.051
       170                0.019             0.057
       171                0.021             0.052
       172                0.017             0.057
       173                0.020             0.056
       174                0.018             0.055
       175                0.019             0.058
       176                0.019             0.059
       177                0.023             0.053
       178                0.017             0.061
       179                0.026             0.054
       180                0.017             0.061
       181                0.022             0.056
       182                0.022             0.062
       183                0.017             0.062
       184                0.015             0.067
       185                0.021             0.060
       186                0.021             0.065
       187                0.026             0.060
       188                0.019             0.070
       189                0.023             0.068
       190                0.023             0.068
       191                0.026             0.062
       192                0.021             0.069
       193                0.027             0.067
       194                0.021             0.075
       195                0.023             0.067
       196                0.017             0.083
       197                0.015             0.082
       198                0.017             0.079
       199                0.018             0.078
       200                0.022             0.077

    
    */
    
  }
  
}

