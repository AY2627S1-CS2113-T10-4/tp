package seedu.unienable.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Owns console reading and message output without closing caller-owned streams.
 */
public class Ui {
    private final Scanner input;
    private final PrintStream output;

    public Ui(InputStream input, PrintStream output) {
        this.input = new Scanner(input);
        this.output = output;
    }

    public boolean hasNextLine() {
        return input.hasNextLine();
    }

    public String readLine() {
        return input.nextLine();
    }

    public void showMessage(String message) {
        output.println(message);
    }
}
