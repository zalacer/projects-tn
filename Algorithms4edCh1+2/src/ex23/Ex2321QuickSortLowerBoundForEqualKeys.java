package ex23;

public class Ex2321QuickSortLowerBoundForEqualKeys {

  /* p306  
  2.3.21  Lower bound for sorting with equal keys. Complete the first part* of the proof
  of Proposition M by following the logic in the proof of Proposition I and using the
  observation that there are N!/f(1)!f(2)! . . . f(k!) different ways to arrange keys 
  with k different values, where the i th value appears with frequency f(i)(= Npᵢ, in 
  the notation of Proposition M), with f(1)+. . . +f(k) = N.
  
  1. Since there are N!/(f(1)!f(2)! . . . f(k!)) permutations with k different values
     there are at least that many leaves in the decision tree T of all possible sequences
     of compares.
     
  2. Therefore, following the logic of the derivation of Proposition I on p280 the worst
     case (i.e. lower bound) height of T is h(T) = lg(N!/(f(1)!f(2)! . . . f(k!))).
     
  3. Using Ramanujan's more accurate approximation† (lg(n!)=nlg(n)−n+O(lg(n)) for N!, since
     it's larger hence its accuracy is more important, and the less accurate Stirling's 
     approximation from p185 (lg(N!) ~ NlgN) for the other components since they're smaller
     and less important (i.e. fudging to get the stipulated result):
     h(T) ~ Nlg(N) - N - f(1)lg(f(1) - f(2)lg(f(2)-...-f(k)lg(f(k))
     
  4. Entropy as defined on p300 is H = -(p(1)lg(p(1) + p(2)lg(p(2)+...+p(k)lg(p(k))
  
  5. Substituting p(i) = f(i)/N in the expression for H
     H = -((f(1)/N)(lg((f(1)/N)) + (f(2)/N)(lg((f(2)/N))+...+(f(k)/N)(lg((f(k)/N)))
     
  6. Since lg((f(i)/N) = -lg((N/f(i)) 
     H = (f(1)/N)(lg((N/f(1))) + (f(2)/N)(lg((N/f(2)))+...+(f(k)/N)(lg((N/f(k)))
     
  7. Since (f(i)/N)(lg((N/f(i))) = (f(i)/N)(lg(N)-lg(f(i)) = 
     and f(1)+. . . +f(k) = N
     H = (1/N)(Nlg(N) - f(1)lg(f(1) - f(2)lg(f(2)-...-f(k)lg(f(k))
     
  8. And HN = Nlg(N) - f(1)lg(f(1) - f(2)lg(f(2)-...-f(k)lg(f(k)
  
  9. Comparing 8 to 3 gives h(T) = HN - N.
  
 10. However, using Ramanujan's approximation all the way through, instead of 3 we get
     h(T) ~ Nlg(N) - N - f(1)lg(f(1) + f(1) - f(2)lg(f(2) + f(2)...-f(k)lg(f(k) + f(k)
     
 11. Since f(1)+. . . +f(k) = N the sum of the f(i) in 10 exactly cancel out -N
     resulting in h(T) ~ Nlg(N) - f(1)lg(f(1) - f(2)lg(f(2)-...-f(k)lg(f(k))
     and h(T) = HN.
     
 12. I believe h(T) = HN is the correct lower bound on the number of compares because
     it uses the same and more accurate approximation for n! throughout showing that
     the relativly smaller factors have terms which in total exactly cancels -N, and
     because the derivation by different means at
       ‡https://cs.stackexchange.com/questions/29195/sorting-when-there-are-only-olog-n-many-different-numbers
     gives the same result. But due to the approximations it should be written as
     h(t) = HN + O(log(N)) or h(t) ~ HN.
     
  *  As far as I can tell Proposition M has no proof in the text and the Proof Sketch on
     p300 doesn't indicate that it has more than one part. What is the second part of the
     the proof?
  
  † More information about Ramanujan's approximation and a refinement of it is at
      https://math.stackexchange.com/questions/152342/ramanujans-approximation-to-factorial
    and in this project at 
      RamanujansApproximationToFactorial.pdf
    
  ‡ This stackexchange Q&A is available in this project at
      DerivationOfLowerBoundOnComparesInTermsOfEntropy.pdf

  */ 
  
  public static void main(String[] args) {


  }

}

