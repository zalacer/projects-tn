package utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class RandomUtils {
    
    public final static char[] ch = new char[]{'-', '+', '=', '?', '^', '*', '%',
      '$', '#', '@', '!', '&', '{', '}', '(', ')', '<', '>', '|', '/', '\\',
      '\'', '"', '.', ':', ';', '_'};
    
    public static String randomString(int k) {
        if (k<1) throw new IllegalArgumentException("k must be greater than 0");
        SecureRandom random = new SecureRandom();
        String r = new java.math.BigInteger(k*7, random).toString(32);
//        System.out.println("r="+r);
        char[] chars = new char[r.length()];
        for(int i = 0; i < r.length(); i++) {
           if (Character.isLetter(r.charAt(i)) && random.nextInt(10) > 4) {
               chars[i] = Character.toUpperCase(r.charAt(i));
           } else chars[i] = r.charAt(i);
           if (Character.isLetter(r.charAt(i)) && random.nextInt(10) < 3) {
               chars[i] = ch[random.nextInt(ch.length)];
           }
           if (Character.isDigit(r.charAt(i)) && random.nextInt(10) > 5) {
               chars[i] = ch[random.nextInt(ch.length)];               
           }
        }
        String s = new String(chars);
        if (s.length() > k) {
            return s.substring(s.length()-k);
        } else if (s.length() < k) {
          return randomString(k);
        }
        return "hello";
    }
    
    public static String randomStringStrong(int k) {
      if (k<1) throw new IllegalArgumentException("k must be greater than 0");
      Random random = null;
      try {
        random = java.security.SecureRandom.getInstanceStrong();
      } catch (NoSuchAlgorithmException e) {}
      if (random == null) random = new SecureRandom();
      String r = new java.math.BigInteger(k*7, random).toString(32);
//      System.out.println("r="+r);
      char[] chars = new char[r.length()];
      for(int i = 0; i < r.length(); i++) {
         if (Character.isLetter(r.charAt(i)) && random.nextInt(10) > 4) {
             chars[i] = Character.toUpperCase(r.charAt(i));
         } else chars[i] = r.charAt(i);
         if (Character.isLetter(r.charAt(i)) && random.nextInt(10) < 3) {
             chars[i] = ch[random.nextInt(ch.length)];
         }
         if (Character.isDigit(r.charAt(i)) && random.nextInt(10) > 5) {
             chars[i] = ch[random.nextInt(ch.length)];               
         }
      }
      String s = new String(chars);
      if (s.length() > k) {
          return s.substring(s.length()-k);
      } else if (s.length() < k) {
        return randomString(k);
      }
      return "hello";
  }
    
    public static String umodRandomString(int k) {
        if (k<1) throw new IllegalArgumentException("k must be greater than 0");
        SecureRandom random = new java.security.SecureRandom();
        String r = new java.math.BigInteger(k*7, random).toString(32);
        char[] chars = new char[r.length()];
        for(int i = 0; i < r.length(); i++) {
           if (Character.isLetter(r.charAt(i)) && random.nextInt(10) > 4) {
               chars[i] = Character.toUpperCase(r.charAt(i));
           } else chars[i] = r.charAt(i);
        }
        String s = new String(chars);
        if (s.length() > k) {
            return s.substring(s.length()-k);
        } else {
            return s;
        }
    }
    
    public static void main(String[] args) {
        
        for (int i = 0; i < 15; i++) {
           System.out.println(randomString(10));
//            System.out.println(umodRandomString(27));   
        }
    }
}

// c4lGoVU+b8igC5op*CQNUg2?UPk
// BrtOf+g1@o%Arbn=9USdF0n01$6
// j4e^%t*gaj6lL2Dd-tshqTjhq37
// K1DJ=Ker1^bsq256$DSm4q+hJQ-
// lK+nSG9r0ecIBfhsO?-dBKoSNuN
// > 4
// qSArPLSpaglT9amq-5#cUkhNGKj
// extra chars
// i/qf5(7rM2Ke4mlE*lOONNu6HMP
// SmG/@|CatSGb5GiFojg#E&FCmT7
// C9)vbnOvLoIt)npsHnnP+-E3*m8
// BpbBJkUm))h$NT8+=Q}eAMiGe=t
// Ie3I$>B8j/6rMe81vCLn+#hs8Lu
// additional logic sub letters for syms
// Q6$(DHvA!{%<2{pr$96/@7BLEa/
// 4gU{3Sl+|<+6<T%}O4i#(|7&U=^
// 0{2a@560tiD>i7Si$Jcq=^mG9-c
// ^hOB9^(#Gj?Qo)DCkgI}V=0#2#o
//
//    4V71=re>F(Vp^2j2?>TjA4P*20p
//    /(uU@$4+{)@>1$2A^09kR)A1E!G
//    F>gtkI3LimVPb/e+#m|^?Q236}#
//    FN12Q+j3i5H3|aBLIkT{?>)Jv8/
//    l%9}1$=Jli!oV898/kio|1U27Q%
//    P2<{/-m2nb|Prm$3j{U?ccT3=(a
//    2A@FJd77#!+=r7*euP<)egrn)32
//    DC4/e7nN0G$4V4t#qO26o^S!q$3
//    s=1FGp3b=H9s$!4cBHt@ECf^{7}
//    /)3IAa?3CeK11r2B@=3{9}BGt{!
//    6{<Jp7@jkp@40f<id$!e+DjLgdJ
//    {Q!7{vA--#J{12mr=CBo!N3%6<R
//    ^6Mid$&bjq?crsJ1!K3S@g|@{d2
//    u!)T5$OM{k<E>NL/?q<g8$i)iD^
//    %m8R7E8?(/Jcv*#>)pcb^>U2+5^
//    0|l5j5eh^mnjVUNOFcN1|U0s$7k
//    /*tB)-P^G>6e#3/rl0bSaM!C83V
//    *O2|j91Ncqf{^(u+49OG(@PSmlu
//    @Dd@lUb2=>$48=)#+9lqA5r*H+j
// umod
//    n7p95tK93aTPfd0GGvLkJcAdQJT
//    lv1vDE5FgR3749ut75Qn2R9saMA
//    6sH045Ci3gjVsDD8JkAg7eulAb7
//    9r470QgTR7q9aReI8kLT1QABr6B
//    Fqo6ctv3MF908chja4f4R3mea02
//    OQQ09U2Js80DDmaldpT6ua8go8n
//    ro0uemrj3V1he1B446oQp0Lc15J
//    hP4BLJJ0TO5eAKdstVR7iqo2UUk
//    NavKlH2vGv48C1hTHqs9HlA7luD
//    aMtO3IAFlOe9T146q14LcDMtjQG
//    HOh4bp3LD2FvE4gB2g5P2BgJDoq
//    3kqqN3aiueGEhPIb86ChHkO9r7J
//    jOOf7dk2hlpuMKlv6ilEH1iBINq
//    9oArCft179OnK0H7UDl3e81faFF
//    32JcrTlrc5Dr3Dc37u0u377gkoa
//    nRV34b20abTi24Hbc3fVQQ5sn9g
//    orfR6reU5jqi5p3i6A53nTCSlH1
//    0faAgD0sb7o79J6O40524k65le2
//    S81uD5hP0ptpV4qd7HGN6gfg6r6
