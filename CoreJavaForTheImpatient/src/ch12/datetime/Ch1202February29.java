package ch12.datetime;

import java.time.LocalDate;

// 2. What happens when you add one year to LocalDate.of(2000, 2, 29)?
// Four years? Four times one year?

// adding one year gives 2001-02-28
// adding four years gives 2004-02-29
// adding one year four times gives 2004-02-28

public class Ch1202February29 {


  public static void main(String[] args) {

    LocalDate ld = LocalDate.of(2000, 2, 29);
    System.out.println("ld: "+ld); //2000-02-29
    LocalDate ldPlus1 = ld.plusYears(1); // returns a copy of ld with 1 yr added
    System.out.println("ldPlus1: "+ldPlus1); //2001-02-28
    LocalDate ldPlus4 = ld.plusYears(4);
    System.out.println("ldPlus4: "+ldPlus4); //2004-02-29
    LocalDate ldPlus4x1 = ld;
    for(int i= 0; i < 4; i++)
      ldPlus4x1 = ldPlus4x1.plusYears(1);
    System.out.println("ldPlus4x1: "+ldPlus4x1); //2004-02-28
    System.out.println("ld: "+ld); //2000-02-29, same because LocalDate is immutable)









  }

}
