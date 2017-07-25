package ex25;

import static java.lang.Math.*;
import static v.ArrayUtils.*;

import java.security.SecureRandom;
import java.util.Objects;

import pq.MinPQ;
import pq.MaxPQ;

/* p356
  2.5.22 Stock market trading. Investors place buy and sell orders 
  for a particular stock on an electronic exchange, specifying a 
  maximum buy or minimum sell price that they are willing to pay, 
  and how many shares they wish to trade at that price. Develop a
  program that uses priority queues to match up buyers and sellers 
  and test it through  simulation. Maintain two priority queues, 
  one for buyers and one for sellers, executing trades whenever a 
  new order can be matched with an existing order or orders.
 
 */

@SuppressWarnings("unused")
public class Ex2522UsingPQsToSimulateStockMarketTrading {
  
  public static class Order implements Comparable<Order> {
    // this is compacted to reduce space
    private String type;    // buy or sell
    private int id;         // unique order id
    private String stock;   // symbol for the stock
    private double price;   // buy/sell price
    private double bump;    // fraction of price to bump it after n passes
    private int count = 0;  // number of times min() or max() has returned this
    private int n;          // when passes == n adjust price by bump
    private int quant;      // number of shares
    private int acct;       // account id
    public Order(String t, int i, String s, double p, double b, int N, int q, int a) {
      type = t; id = i; stock = s; price = p; bump = b; n = N; quant = q; acct = a; }
    @Override public int compareTo(Order that) { 
      double d = price - that.getPrice(); return d < 0 ? -1 : d > 0 ? 1 : 0; }    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getStock() { return stock; }
    public void setStock(String stock) { this.stock = stock; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price;  }    
    public double getBump() { return bump; }
    public void setBump(double bump) { this.bump = bump;  }    
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count;  }     
    public int getN() { return n; }
    public void setN(int n) { this.n = n;  }     
    public int getQuant() { return quant; }
    public void setQuant(int quant) { this.quant = quant;  }   
    public int getAcct() { return acct; }
    public void setAcct(int acct) { this.acct = acct; } 
    @Override public String toString() { return "(t="+type+",i="+id+",s="+stock
        +",p="+price+",b="+bump+",c="+count+",n="+n+",q="+quant+",a="+acct+")"; }
    @Override public int hashCode() { 
      return Objects.hash(type,id,stock,price,bump,count,n,acct); }  
    @Override public boolean equals(Object obj) {
      if (this == obj) return true; if (obj == null) return false;
      if (getClass() != obj.getClass()) return false; Order other = (Order) obj;
      if (acct != other.acct) return false; if (id != other.id) return false;
      if (price != other.price) return false; if (bump != other.bump) return false;
      if (count != other.count) return false; if (n != other.n) return false;
      if (quant != other.quant) return false;
      if (stock == null) { if (other.stock != null) return false; }
      else if (!stock.equals(other.stock)) return false;     
      if (type == null) { if (other.type != null) return false; }
      else if (!type.equals(other.type)) return false; return true; }
  }
  
  public static SecureRandom r = new SecureRandom();
  
  public static double nextPrice() { return (double) (r.nextInt(120)+10); } // [10..119]
  
  public static int nextN() {  return r.nextInt(10)+1; } // [1..9]
  
  public static int nextQuant() { return (r.nextInt(20)+1)*50; } // [50,950] by 50
  
  public static void market(MaxPQ<Order> buy, MinPQ<Order> sell) {
    // simlulate a very simple dynamic market. sales are made only when buy price
    // equal to or greater than sell price and is always done at the sell price.
    // if a sale cannot be made it's eventually adjusted upwards for buyers and 
    // downwards for sellers according to their settings of n and count: every 
    // time a sale is available the current buyer and seller counts are increased 
    // by 1 and if either reaches its respective n value its respective price is 
    // adjusted up or down according to its respective bump value and its respective 
    // count is reset to 0. If the buy and sell quantities don't match, as much as 
    // can be sold is sold and wichever has remaining stock is left in their queue  
    // with the remaining quantity of shares. This continues until the buy price is 
    // >= to the sell price. When a buyer has bought all it wants it's removed from
    // its queue. When a seller has sold all it has it's removed from its queue.
    
    // counters to be output at end
    int sellorders = sell.size(); int buyorders = buy.size();
    int sellPriceAdjusted = 0, buyPriceAdjusted = 0; 
    int sellQuantityAdjusted = 0, buyQuantityAdjusted = 0;
    int sales = 0;
    
    System.out.println("transactions:");
    while (true) {
      Order sord = sell.min(); Order bord = buy.max();
      int sc = sord.getCount(); sord.setCount(++sc);
      int bc = bord.getCount(); bord.setCount(++bc);
      int sn = sord.getN(); int bn = bord.getN();
      if (sc == sn) {
        sord.setPrice(round(sord.getPrice() * (1+sord.getBump())));
        sellPriceAdjusted++;
        sord.setCount(0);
      }
      if (bc == bn) {
        bord.setPrice(round(bord.getPrice() * (1+bord.getBump()))); 
        buyPriceAdjusted++;
        bord.setCount(0);
      }
      double sprice = sord.getPrice(); double bprice = bord.getPrice();
      int squant = sord.getQuant(); int bquant = bord.getQuant();
      if (sprice > bprice) {
        //for insight and debugging:
        //System.out.println("sell");
        //Order[] s = sell.toArray(); for (int i=0;i<s.length;i++) System.out.println(s[i]);
        //System.out.println("buy");
        //Order[] b = buy.toArray(); for (int i=0;i<b.length;i++) System.out.println(b[i]);
        //System.out.println();
        continue; // cause sprice and bprice to come together
      } else {
        // sell min(squant,bquant) at sprice
        int shares = min(squant,bquant);
        int sacct = sord.getAcct(); int bacct = bord.getAcct();
        System.out.println(shares+" shares of X sold @ "+sprice+" by "+sacct+" to "+bacct);
        sales++;
        if (squant - shares == 0) sell.delMin();
        else {
          sord.setQuant(squant - shares);
          sellQuantityAdjusted++;
        }
        if (bquant - shares == 0) buy.delMax();
        else {
          bord.setQuant(bquant - shares);
          buyQuantityAdjusted++;
        }
      }
      if (sell.isEmpty() || buy.isEmpty()) break;
    }
    System.out.println("\ninitially there were "+sellorders+" sell orders and "+
        +buyorders+" buy orders");
    System.out.println(sales+" transactions were done");
    System.out.println(buy.size()+" orders remain in the buy queue");
    System.out.println(sell.size()+" orders remain in the sell queue");
    System.out.println("\nthe buy price was adjusted "+ buyPriceAdjusted+" times");
    System.out.println("the sell price was adjusted "+ sellPriceAdjusted+" times");
    System.out.println("\nthe buy quantity was adjusted "+ buyQuantityAdjusted+" times");
    System.out.println("the sell quantity was adjusted "+ sellQuantityAdjusted+" times");
  }
  
  public static void main(String[] args) {
    
    int id = 1, price = 1, shares = 1, bacct = 1, sacct = 1001; 
    // using fixed bumps of -0.1 for sells and +0.1 for buys
    
    MinPQ<Order> sell = new MinPQ<>(); MaxPQ<Order> buy = new MaxPQ<>(); int c = 0;
    while (c < 10) {
      sell.insert(new Order("sell",id++,"X",nextPrice(),-0.1,nextN(),nextQuant(),sacct++));
      buy.insert(new Order("buy",id++,"X",nextPrice(),0.1,nextN(),nextQuant(),bacct++)); 
      c++; 
    }

    market(buy,sell);
/*
    transactions:
    100 shares of X sold @ 13.0 by 1008 to 9
    600 shares of X sold @ 20.0 by 1005 to 9
    600 shares of X sold @ 35.0 by 1009 to 1
    400 shares of X sold @ 43.0 by 1004 to 1
    350 shares of X sold @ 43.0 by 1004 to 7
    350 shares of X sold @ 50.0 by 1001 to 7
    650 shares of X sold @ 45.0 by 1001 to 2
    200 shares of X sold @ 76.0 by 1007 to 2
    200 shares of X sold @ 76.0 by 1007 to 6
    400 shares of X sold @ 75.0 by 1003 to 6
    150 shares of X sold @ 45.0 by 1003 to 10
    150 shares of X sold @ 27.0 by 1003 to 4
    200 shares of X sold @ 24.0 by 1003 to 8
    150 shares of X sold @ 49.0 by 1006 to 8
    350 shares of X sold @ 56.0 by 1010 to 8
    550 shares of X sold @ 61.0 by 1002 to 5
    350 shares of X sold @ 30.0 by 1002 to 3
    
    initially there were 10 sell orders and 10 buy orders
    17 transactions were done
    0 orders remain in the buy queue
    0 orders remain in the sell queue
    
    the buy price was adjusted 25 times
    the sell price was adjusted 39 times
    
    the buy quantity was adjusted 7 times
    the sell quantity was adjusted 7 times    
*/
  }

}


