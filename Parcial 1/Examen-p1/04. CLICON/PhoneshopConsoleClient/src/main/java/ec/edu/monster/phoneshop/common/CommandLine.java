package ec.edu.monster.phoneshop.common;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class CommandLine {
    private static CommandLine instance;
    private final Scanner scanner = new Scanner(System.in);
    Console console = System.console();
    PrintStream consoleOut = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    public static CommandLine getInstance() {
        if (instance == null) {
            try {
                instance = new CommandLine();
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }

        return instance;
    }

    private CommandLine() throws Exception {}

    public void print(String message) {
        consoleOut.println(message);
        consoleOut.flush();
    }

    public String prompt(String message) {
        consoleOut.print(message);
        return scanner.nextLine();
    }

    public String password(String message) {
        EraserThread et = new EraserThread(message, consoleOut);
        Thread mask = new Thread(et);
        mask.start();

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String password = "";

        try {
            password = in.readLine();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        // stop masking
        et.stopMasking();
        // return the password entered by the user
        return password;
    }

    public Double promptDouble(String message) {
        consoleOut.print(message);
        Double result = scanner.nextDouble();
        scanner.nextLine();

        return result;
    }

    public Integer promptInteger(String message) {
        consoleOut.print(message);
        Integer result = scanner.nextInt();
        scanner.nextLine();

        return result;
    }

    public int choose(String message, String... options) {
        consoleOut.println(message);

        for (int i = 0; i < options.length; i++) {
            consoleOut.println((i + 1) + ". " + options[i]);
        }

        int choice = promptInteger("Selecciona una opcion: ");

        if (choice < 1 || choice > options.length) {
            consoleOut.println("Opcion invalida");
            return choose(message, options);
        }

        return choice;
    }

    public boolean confirm(String message) {
        return choose(message, "Si", "No") == 1;
    }
}
