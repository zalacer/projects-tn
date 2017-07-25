package ex21;

/* p265
  2.1.13 Deck sort. Explain how you would put a deck of cards in order by suit (in the
  order spades, hearts, clubs, diamonds) and by rank within each suit, with the restriction
  that the cards must be laid out face down in a row, and the only allowed operations are
  to check the values of two cards and to exchange two cards (keeping them face down).
  
  If all the cards have to be stuck in one long row then some kind of simple sort could be 
  done starting with designating an ordering of suits such clubs < diamonds < hearts < spades 
  and using the numerical values to order the ranks with ace == 1, jack == 11, queen == 12 and 
  king == 13. Assign numerical positions (indices) to the positions of the cards, starting with
  0 on the far left. Look at the first two cards on the far left, cards 0 and 1, and exchange
  them if they're not in proper order from least to greatest. Then look at cards 1 and 2 and do
  the same thing. If cards 1 and 2 were exchanged, propagate the change to the card in position 
  1 peeking at cards 0 and 1 again and exchanging them if necessary. Proceed to cards 3 and 4 by
  peeking at them, exchanging them if necessary and if they were exchanged propagating the change
  as far left as possible. Proceed likewise until the end of the row of cards has been reached.
  
  Let's look at a short example of this process with just three cards for simplicity. Suppose the 
  cards are the ace, deuce and three of hearts so all that matters are their ranks where 
  ace < deuce < three and they are initially ordered three, deuce, ace and are face down. First 
  turn over the first two cards and see they are the three and deuce and since they are out of 
  order exchange them. The take a look at the second and third cards, see they are the three and 
  ace and are out of order so exchange them.  Now the cards are in order deuce, ace and three. 
  Since the last two cards were exchanged it's now possible that the first two are out of order 
  again. So take a look at them, observer they are the deuce and ace which is out of order so 
  exchange them. Now the cards are completely in order ace, deuce, three.

 */

public class Ex2113ManualDeckOfCardsSort {

}
