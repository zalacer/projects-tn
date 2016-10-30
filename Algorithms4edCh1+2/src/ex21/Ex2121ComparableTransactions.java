package ex21;

/* p266
  2.1.21 Comparable transactions. Using our code for Date (page 247) as a model, ex-
  pand your implementation of Transaction (Exercise 1.2.13) so that it implements
  Comparable, such that transactions are kept in order by amount.
  
  Solution :
  
  public class Transaction implements Comparable<Transaction> {
    ...
    private final double amount;
    ...
    public int compareTo(Transaction that) {
      if (this.amount > that.amount) return +1;
      if (this.amount < that.amount) return -1;
      return 0;
    }
    ...
  }
  
  See ds.Transaction and ds.Date.

 */

public class Ex2121ComparableTransactions {

}
