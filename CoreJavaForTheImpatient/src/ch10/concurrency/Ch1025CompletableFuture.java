package ch10.concurrency;

import java.net.PasswordAuthentication;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.Supplier;

// 25. Write a method
//  public static <T> CompletableFuture<T> 
//      repeat(//Supplier<T> action, Predicate<T> until)
// that asynchronously repeats the action until it produces a value that is
// accepted by the until function, which should also run asynchronously. 
// Test with a function that reads a java.net.PasswordAuthentication from the
// console, and a function that simulates a validity check by sleeping for a 
// second and then checking that the password is "secret". Hint: Use recursion.

public class Ch1025CompletableFuture {

  public static Scanner in = new Scanner(System.in);

  public static String getInput(String prompt) {
    System.out.print(prompt + ": ");
    return in.nextLine();
  }

  public static <T> CompletableFuture<T> 
      repeat(Supplier<T> action, Predicate<T> until) {
    return CompletableFuture.supplyAsync(action).thenApplyAsync((T r) -> {
      return until.test(r) ? r : repeat(action, until).join();
    });
  }

  public static void main(String[] args) {

    CompletableFuture<PasswordAuthentication> f = repeat(() -> {
      String name = getInput("name");
      String pwd = getInput("password");
      return new PasswordAuthentication(name, pwd.toCharArray());
    } , (a) -> {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException ignored) {}
      return new String(a.getPassword()).equals("secret");
    });

    f.thenAccept((b) -> 
    System.out.printf("Logged in: %s %s%n", b.getUserName(), new String(b.getPassword())));

    ForkJoinPool.commonPool().awaitQuiescence(10, TimeUnit.SECONDS);

  }

}

// PasswordAuthentication test
//PasswordAuthentication p = new PasswordAuthentication("z", "secret".toCharArray());
//if (p.getUserName() == "z") System.out.println("p name ok");
//if ((new String(p.getPassword())).equals("secret")) System.out.println("p pass ok");
