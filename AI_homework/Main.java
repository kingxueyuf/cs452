/***********
 * Template code for ACM contest entries
 ***********/
import java.io.*;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		Main m = new Main();
		System.out.println(args[0]);
		m.start();

	}

	private void start() {
		/*
		 * Scanner will read from standard input channel (System.in).
		 * 
		 * You can test the code interactively by running Main, and then
		 * entering inputs one at a time to see the output (to System.out).
		 * 
		 * Alternatively, create a text-file with the inputs you want, and then
		 * re-direct the standard input to read from same (as the judging
		 * program will do). An example of how this would be called is:
		 * 
		 * java Main < input.txt
		 * 
		 * which will take each line of input.txt, one by one, and feed in to
		 * the Scanner via System.in
		 */
		Scanner scan = new Scanner(System.in);

		while (scan.hasNext()) {
			// NEW CODE HERE FOR EACH PROBLEM HERE

			/* Simple example: echo the input in each case */
			echo(scan.next());
		}
	}

	// SAMPLE METHOD
	private void echo(String s) {
		System.out.println(s);
	}

}
