package ch12.datetime;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

//  7. Implement a TimeInterval class that represents an interval of time, suitable for
//  calendar events (such as a meeting on a given date from 10:00 to 11:00). Provide a
//  method to check whether two intervals overlap.

public class Ch1207TimeInterval  {
  // this is for local meetings
  // could be extended to global with ZonedDateTime
  // for pre-Java8 inspiration see
  //http://cdaweb.gsfc.nasa.gov/WebServices/SOAP/public/api/gov/nasa/gsfc/spdf/cdas/TimeInterval.html
  public static class TimeInterval implements Comparable<TimeInterval>{
    LocalDateTime b;
    LocalDateTime e;
    
    TimeInterval(){}
    
    // maintain b and e such that b.compareTo(e) <= 0
    // and require Duration.between(b, e).toMinutes() >= 10.
    TimeInterval(LocalDateTime b, LocalDateTime e) {
      if (Math.abs((double) Duration.between(b, e).toMinutes()) < 10.) {
        throw new IllegalArgumentException("TimeInterval duration must be 10 or more minutes");
      }
      if (b.compareTo(e) <= 0) {
        this.b = b;
        this.e = e;
      } else {
        this.b = e;
        this.e = b;
      }
    }
    
    public LocalDateTime getB() {
      return b;
    }

    public LocalDateTime getE() {
      return e;
    }

    // this may interchange values of b and e to preserve ordering
    public void setB(LocalDateTime b) {
      if (Math.abs((double) Duration.between(b, this.e).toMinutes()) < 10.) {
        throw new IllegalArgumentException("TimeInterval duration must be more than 10 minutes");
      }
      if (b.compareTo(this.e) <= 0) {
        this.b = b;
      } else {
        this.b = this.e;
        this.e = b;
      }     
    }

    // this may interchange values of b and e to preserve ordering
    public void setE(LocalDateTime e) {
      if (Math.abs((double) Duration.between(this.b, e).toMinutes()) < 10.) {
        throw new IllegalArgumentException("TimeInterval duration must be more than 10 minutes");
      }
      if (this.b.compareTo(e) <= 0) {
        this.e = e;
      } else {
        this.e = this.b;
        this.b = e;
      }
    }

    public boolean contains(TimeInterval ti) {
      return (this.b.compareTo(ti.b) <= 0 && this.e.compareTo(ti.e) >= 0);
    }
    
    public boolean overlaps(TimeInterval ti) {
      // if this.b is in ti
      // or if this.e is in ti
      // or if ti.b is in this
      // or if ti.e is in this
      // then overlap is true else it's false
      // using > and < instead of >= and <= for more likely non-trivial overlap
      // could later enforce a given minimum overlap such as 1 minute for it to matter
      if ((this.b.compareTo(ti.b) > 0 && this.b.compareTo(ti.e) < 0) ||
          (this.e.compareTo(ti.b) > 0 && this.e.compareTo(ti.e) < 0) ||
          (ti.b.compareTo(this.b) > 0 && ti.b.compareTo(this.e) < 0) ||
          (ti.e.compareTo(this.b) > 0 && ti.e.compareTo(this.e) < 0)) { 
        return true;
      }
      return false;
    }
    
    @Override
    public int compareTo(TimeInterval ti) {
      int bCompare = this.b.compareTo(ti.b);
      return bCompare != 0 ? bCompare : this.e.compareTo(ti.e);
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((b == null) ? 0 : b.hashCode());
      result = prime * result + ((e == null) ? 0 : e.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      TimeInterval other = (TimeInterval) obj;
      if (b == null) {
        if (other.b != null)
          return false;
      } else if (!b.equals(other.b))
        return false;
      if (e == null) {
        if (other.e != null)
          return false;
      } else if (!e.equals(other.e))
        return false;
      return true;
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("TimeInterval[b=");
      builder.append(b);
      builder.append(", e=");
      builder.append(e);
      builder.append("]");
      return builder.toString();
    }
  }
  
  public static void main(String[] args) {
    
    TimeInterval ti1 = new TimeInterval(
        LocalDateTime.of(2016, 1, 5, 10, 0), LocalDateTime.of(2016, 1, 5, 12, 0));
    System.out.println(ti1); //TimeInterval[b=2016-01-05T10:00, e=2016-01-05T12:00]
    
    TimeInterval ti2 = new TimeInterval(
        LocalDateTime.of(2016, 1, 5, 11, 0), LocalDateTime.of(2016, 1, 5, 13, 0));
    System.out.println(ti2); // TimeInterval[b=2016-01-05T11:00, e=2016-01-05T13:00]
    
    System.out.println(ti1.overlaps(ti2)); //true
    
    TimeInterval ti3 = new TimeInterval(
        LocalDateTime.of(2016, 1, 5, 10, 0), LocalDateTime.of(2016, 1, 5, 11, 0));
    
    System.out.println(ti1.overlaps(ti3)); // true
    
    TimeInterval ti4 = new TimeInterval(
        LocalDateTime.of(2016, 1, 5, 8, 0), LocalDateTime.of(2016, 1, 5, 9, 0));
    
    System.out.println(ti1.overlaps(ti4)); // false
    
    TimeInterval ti5 = new TimeInterval(
        LocalDateTime.of(2016, 1, 5, 9, 0), LocalDateTime.of(2016, 1, 5, 10, 0));
    
    System.out.println(ti1.overlaps(ti5)); // false - trivial overlap at 10 AM instant
    
    TimeInterval ti6 = new TimeInterval(
        LocalDateTime.of(2016, 1, 5, 9, 0), LocalDateTime.of(2016, 1, 5, 10, 1));
    
    System.out.println(ti1.overlaps(ti6)); // true - 1 minute overlap 
    
    System.out.println(ti1.contains(ti6)); // false
    
    System.out.println(ti1.contains(ti3)); // true
    
    TimeInterval[] tia = new TimeInterval[]{ti1, ti2, ti3, ti4, ti5, ti6};
    Arrays.sort(tia);
    for (TimeInterval ti : tia) System.out.println(ti);
//  output:
//  TimeInterval[b=2016-01-05T08:00, e=2016-01-05T09:00]
//  TimeInterval[b=2016-01-05T09:00, e=2016-01-05T10:00]
//  TimeInterval[b=2016-01-05T09:00, e=2016-01-05T10:01]
//  TimeInterval[b=2016-01-05T10:00, e=2016-01-05T11:00]
//  TimeInterval[b=2016-01-05T10:00, e=2016-01-05T12:00]
//  TimeInterval[b=2016-01-05T11:00, e=2016-01-05T13:00]
    
  }

}
