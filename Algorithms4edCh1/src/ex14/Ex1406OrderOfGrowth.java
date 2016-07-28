package ex14;

//  p208 (order of growth p179, classifications p186)
//  1.4.6  Give the order of growth (as a function of N ) of the running times of each of the
//  following code fragments:
//    
//    a. int sum = 0;                     N(1+1/2+1/4...) = 2N -> N(linear) OOG(order of growth)
//       for (int n = N; n > 0; n /= 2)   //https://en.wikipedia.org/wiki/1/2_%2B_1/4_%2B_1/8_%2B_1/16_%2B_%E2%8B%AF
//         for(int i = 0; i < n; i++)
//           sum++;
//    
//    b. int sum = 0;
//       for (int i = 1 i < N; i *= 2)    linear since for each i the 2nd loop operates i times
//         for (int j = 0; j < i; j++)    while increasing by a power of 2 but it runs only   
//           sum++;                       only about lgN times and these effects balance each
//                                        other resulting in linear growth
//    c. int sum = 0;
//       for (int i = 1 i < N; i *= 2)    linearithmic since like b the outer loop runs about
//         for (int j = 0; j < N; j++)    lgN times but for each it itererates N times
//           sum++;

public class Ex1406OrderOfGrowth {

  public static void main(String[] args) {

  }

}
