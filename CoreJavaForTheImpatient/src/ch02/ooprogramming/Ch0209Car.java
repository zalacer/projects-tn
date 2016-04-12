package ch02.ooprogramming;

import static utils.Direction.EAST;

import utils.Direction;

// 9. Implement a class Car that models a car traveling along the x-axis, consuming gas
// as it moves. Provide methods to drive by a given number of miles, to add a given
// number of gallons to the gas tank, and to get the current distance from the origin and
// fuel level. Specify the fuel efficiency (in miles/gallons) in the constructor. Should
// this be an immutable class? Why or why not?

// car class should be mutable since a real car can be modified without changing
// registered identity

public class Ch0209Car {

  public static void main(String[] args) {
    Ch0209Car c = new Ch0209Car("1", 20, 14);
    System.out.println(c);
    c.drive(1000, EAST);
    System.out.println(c);
    //        Ch0209Car [vin=1, efficiency=20.0, fuelCapacity=14.0, fuelLevel=5.0,
    //                odometer=0.0, distance=0.0, tripGallons=0.0]
    //        driving 70.0 miles before refuelling
    //        refuelling...added 12.5 gallons
    //        driving 250.0 miles before refuelling
    //        refuelling...added 12.5 gallons
    //        driving 250.0 miles before refuelling
    //        refuelling...added 12.5 gallons
    //        driving 250.0 miles before refuelling
    //        refuelling...added 12.5 gallons
    //        driving the final 180.0 miles
    //        Ch0209Car [vin=1, efficiency=20.0, fuelCapacity=14.0, fuelLevel=5.0,
    //                odometer=1000.0, distance=1000.0, tripGallons=50.0]

  }

  private final String vin;
  private double efficiency = 15.0; // mpg
  private double fuelCapacity = 12.0; // gallons
  private double fuelLevel = 5.0; // gallons
  private double odometer = 0.0; // total miles driven
  private double distance = 0.0; // miles from origin
  private double tripGallons = 0.0; // gallons consumed in instance trips
  private final double reserve = 1.5; // gallons to hold in reserve

  public Ch0209Car(String vin, double efficiency, double fuelCapacity) {
    super();
    this.vin = vin;
    this.efficiency = efficiency;
    this.fuelCapacity = fuelCapacity;
  }

  public Ch0209Car(String vin, double efficiency, double fuelCapacity, 
      double fuelLevel) {
    super();
    this.vin = vin;
    this.efficiency = efficiency;
    this.fuelCapacity = fuelCapacity;
    this.fuelLevel = fuelLevel;
  }

  public Ch0209Car(String vin, double efficiency, double fuelCapacity, 
      double fuelLevel, double odometer, double distance) {
    super();
    this.vin = vin;
    this.efficiency = efficiency;
    this.fuelCapacity = fuelCapacity;
    this.fuelLevel = fuelLevel;
    this.odometer = odometer;
    this.distance = distance;
  }

  public void refuel(double g) {
    double added = 0;
    System.out.print("refuelling...");
    if (g == 0) {
      System.out.println("refuelled with zero gallons as requested");
      return;
    } else if (fuelLevel == fuelCapacity) {
      System.out.println("the tank is already full");
      return;
    } else  if (g < 0) {
      System.out.println("can't refuel by taking gas out of the tank");
      return;
    } else if (g <= (fuelCapacity - fuelLevel)) {
      added = g;
      fuelLevel += g; 
      //System.out.println("added1 = "+added);
    } else {
      added = fuelCapacity - fuelLevel;
      fuelLevel = fuelCapacity; 
      //System.out.println("added2 = "+added);
    }
    if (added > 0) {
      System.out.println("added "+added+" gallons");
    } else {
      System.out.println("done");
    }
  }

  private void drive(double miles, Direction d) {
    if (miles == 0) return;
    // fixing value of efficiency for the drive
    double driveEfficiency = efficiency;
    if ((fuelLevel - reserve) * driveEfficiency >= miles) {
      System.out.println("driving "+miles+" miles without refuelling");
      updateStats(miles, d, fuelLevel, driveEfficiency);
      return;
    }
    if ((fuelLevel - reserve) * driveEfficiency < miles) {
      double initMiles = (fuelLevel - reserve) * driveEfficiency;
      if (initMiles > 10) {
        System.out.println("driving "+initMiles+" miles before refuelling");
        updateStats(initMiles, d, fuelLevel, driveEfficiency);              
        // fixing value of milesPerFullTank() for the trip               
        double mpft = milesPerFullTank(driveEfficiency);
        //System.out.println("mpft = "+mpft);
        double miles2go = miles - initMiles;
        //System.out.println("miles2go1 = "+miles2go);
        refuel(fuelCapacity);
        while(true) {
          if (miles2go <= mpft) {
            System.out.println("driving the final "+miles2go+" miles");
            updateStats(miles2go, d, fuelLevel, driveEfficiency); 
            break;
          } else {
            System.out.println("driving "+mpft+" miles before refuelling");
            updateStats(mpft, d, fuelLevel, driveEfficiency); 
            miles2go -= mpft;
            //System.out.println("miles2go2 = "+miles2go);
            refuel(fuelCapacity);
          }
        }        
      }
    }
  }

  public double milesPerFullTank(double efficiency) {
    return (fuelCapacity - reserve) * efficiency;
  }

  private void updateDistance(double miles, Direction d) {
    distance = d == EAST ? distance + miles : distance - miles;
  }

  private void updateOdometer(double miles) {
    odometer += miles;
  }

  private void updateFuelLevel(double fuelLevel, double efficiency, double miles) {
    this.fuelLevel = fuelLevel - (miles / efficiency);
  }

  private void updateTripGallons(double miles, double efficiency) {
    tripGallons += miles / efficiency;
  }

  private void updateStats(double miles, Direction d, double fuelLevel, double efficiency) {
    updateDistance(miles, d);
    updateOdometer(miles);
    updateFuelLevel(fuelLevel, efficiency, miles);
    updateTripGallons(miles, efficiency);
  }

  /**
   * @return the vin
   */
  public String getVin() {
    return vin;
  }

  /**
   * @return the efficiency
   */
  public double getEfficiency() {
    return efficiency;
  }

  /**
   * @param efficiency the efficiency to set
   */
  public void setEfficiency(double efficiency) {
    this.efficiency = efficiency;
  }

  /**
   * @return the fuelCapacity
   */
  public double getFuelCapacity() {
    return fuelCapacity;
  }

  /**
   * @param fuelCapacity the fuelCapacity to set
   */
  public void setFuelCapacity(double fuelCapacity) {
    this.fuelCapacity = fuelCapacity;
  }

  /**
   * @return the fuelLevel
   */
  public double getFuelLevel() {
    return fuelLevel;
  }

  /**
   * @param fuelLevel the fuelLevel to set
   */
  public void setFuelLevel(double fuelLevel) {
    this.fuelLevel = fuelLevel;
  }

  /**
   * @return the odometer
   */
  public double getOdometer() {
    return odometer;
  }

  /**
   * @return the distance
   */
  public double getDistance() {
    return distance;
  }

  /**
   * @return the tripGallons
   */
  public double getTripGallons() {
    return tripGallons;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(distance);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(efficiency);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(fuelCapacity);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(fuelLevel);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(odometer);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(tripGallons);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + ((vin == null) ? 0 : vin.hashCode());
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Ch0209Car other = (Ch0209Car) obj;
    if (Double.doubleToLongBits(distance) != Double.doubleToLongBits(other.distance))
      return false;
    if (Double.doubleToLongBits(efficiency) != Double.doubleToLongBits(other.efficiency))
      return false;
    if (Double.doubleToLongBits(fuelCapacity) != Double.doubleToLongBits(other.fuelCapacity))
      return false;
    if (Double.doubleToLongBits(fuelLevel) != Double.doubleToLongBits(other.fuelLevel))
      return false;
    if (Double.doubleToLongBits(odometer) != Double.doubleToLongBits(other.odometer))
      return false;
    if (Double.doubleToLongBits(tripGallons) != Double.doubleToLongBits(other.tripGallons))
      return false;
    if (vin == null) {
      if (other.vin != null)
        return false;
    } else if (!vin.equals(other.vin))
      return false;
    return true;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Ch0209Car [vin=");
    builder.append(vin);
    builder.append(", efficiency=");
    builder.append(efficiency);
    builder.append(", fuelCapacity=");
    builder.append(fuelCapacity);
    builder.append(", fuelLevel=");
    builder.append(fuelLevel);
    builder.append(",\n           odometer=");
    builder.append(odometer);
    builder.append(", distance=");
    builder.append(distance);
    builder.append(", tripGallons=");
    builder.append(tripGallons);
    builder.append("]");
    return builder.toString();
  }
}
