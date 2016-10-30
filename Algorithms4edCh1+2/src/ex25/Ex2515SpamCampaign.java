package ex25;

/* p355
  2.5.15 Spam campaign. To initiate an illegal spam campaign, you have a 
  list of email addresses from various domains (the part of the email 
  address that follows the @ symbol). To better forge the return addresses 
  you want to send the email from another user at the same domain. For 
  example, you might want to forge an email from wayne@princeton.edu to  
  rs@princeton.edu . How would you process the email list to make this an 
  efficient task?
  
  I would use Qmail because it makes it easy to forge from addresses, although
  it's not difficult with sendmail either. But you probably didn't mean how
  to implement email forging at that level of implementation. So first I would
  sort all the addresses from each domain into separate containers of some
  kind starting with sets (HashSet) to deduplicate then convert them to lists
  (ArrayList) for ease in selecting random elements by index and removal of
  elements. Suppose this is done and now its time to forge the from address for
  a message set to x@y. Then pick a random address from the list for y and if 
  it's not x use it as the from address. If it's x the pick another random address
  and continue until getting one that's not x. Depending on how many times it's
  useful or necessary to reuse from addresses and how many times the from address 
  currently selected has already been reused it may be appropriate to remove it
  from the list for y and put it in a list of used addresses for y. It would be
  wise not to reuse forged address too much if they belong to a real person since
  then this might come to their attention causing them to escalate the issue with
  the authorities. Depending on the target mail server it may also be possible to
  use fictitious from addresses that don't bounce. It would also be useful to spoof
  the sending IP address and other email headers perhaps even to the extent of using 
  different ones for each message sent to make it more difficult to identify the 
  origin of the spam. Another tactic is to route the spam through several or more
  email servers positioned at international locations and for which there is
  sufficient access to equip them with email header rewriting capabilities. To what
  extent this is done depends on how much the spam operation is worth. It may be
  cheaper and most efficient to pay a professional spam house to do the sending or 
  at least sign on with a company that offers email campaign management tools. To
  find them start with a google search for "email campaign services".
  
 */

public class Ex2515SpamCampaign {

  public static void main(String[] args) {
    
  
  }

}


