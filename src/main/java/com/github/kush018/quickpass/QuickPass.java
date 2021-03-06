package com.github.kush018.quickpass;

import com.github.kush018.quickpass.crypto.DBEncryptor;
import com.github.kush018.quickpass.database.Database;
import com.github.kush018.quickpass.database.Table;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.List;
import java.util.Scanner;

public class QuickPass {
    private static Scanner sc;

    public static void main(String[] args) {
        if (args.length == 0) {
            //no file specified
            System.out.println("No quick pass vault file specified. Exiting program ...");
            return;
        }

        sc = new Scanner(System.in);

        boolean newVaultCreated = false;

        String password = null;

        String vaultFileName = args[0];
        File vaultFile = new File(vaultFileName);
        if (!vaultFile.exists()) {
            //if vault does not exist
            newVaultCreated = true;
            System.out.println("Vault does not exist. Creating new vault ...");
            System.out.print("Enter vault master password: ");
            password = new String(System.console().readPassword());
            System.out.print("Reenter vault master password: ");
            String confirmPassword = new String(System.console().readPassword());
            if (!confirmPassword.equals(password)) {
                //if both password and confirm password dont match
                System.out.println("Passwords dont match. Aborting ...");
                return;
            }
            Database db = new Database();
            db.createNewTable("main");
            byte[] vaultBytes;
            try {
                vaultBytes = DBEncryptor.encryptBytes(DBEncryptor.getBytes(db), password);
                Files.write(Paths.get(vaultFileName), vaultBytes);
                System.out.println("Vault created successfully");
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        Database passwordDB = null;

        if (!newVaultCreated) {
            //if new vault was not created, we need to ask for a password
            System.out.print("Enter master password: ");
            password = new String(System.console().readPassword());
        }
        try {
            byte[] cBytes = Files.readAllBytes(Paths.get(vaultFileName));
            byte[] pBytes = DBEncryptor.decryptBytes(cBytes, password);
            passwordDB = DBEncryptor.getDatabase(pBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        System.out.println();

        System.out.println("Access granted");
        System.out.println();

        System.out.println("Welcome to Quick Pass command line password manager.\n" +
                "Enter command \"help\" to see a list of valid commands");

        while (true) {
            System.out.print("> ");
            String choice = sc.nextLine();
            switch (choice) {
                case "list": printAllEntryNames(passwordDB); break;
                case "edit": editEntry(passwordDB); break;
                case "add": addEntry(passwordDB); break;
                case "details": getDetailsOfEntry(passwordDB); break;
                case "save": save(passwordDB, vaultFileName, password); break;
                case "quit": save(passwordDB, vaultFileName, password); return;
                case "!quit": return;
                case "chpasswd": password = changeMasterPasswordAndSave(passwordDB, vaultFileName, password); break;
                case "generate": generateNewPassword(passwordDB); break;
                case "help":
                    System.out.println("List of valid commands:\n" +
                            "list - list all entry names in database\n" +
                            "edit - edit a particular entry\n" +
                            "add - add a new entry\n" +
                            "details - view details of an entry\n" +
                            "save - save changes\n" +
                            "quit - save changes and quit\n" +
                            "!quit - quit without saving\n" +
                            "chpasswd - change master password\n" +
                            "generate - generate a password for an entry\n" +
                            "help - view help menu");
                default:
                    System.out.println("Invalid command" + choice + ". Use \"help\" for a list of valid commands");
            }
            System.out.println();
        }
    }

    public static void printAllEntryNames(Database db) {
        System.out.println("================NAMES================");
        Table table = db.getTableHashMap().get("main");
        for (Table.Row row : table.getRows()) {
            System.out.println(row.getMap().get("name"));
        }
        System.out.println("================END================");
    }

    public static void editEntry(Database db) {
        System.out.print("Enter name of entry you would like to edit: ");
        String name = sc.nextLine();
        Table table = db.getTableHashMap().get("main");
        List<Table.Row> rowList = table.search("name", name);
        Table.Row currentRow = null;
        if (rowList.size() == 0) {
            System.out.println("No entries found");
        } else {
            currentRow = rowList.get(0);
            System.out.print("Enter new name (hit enter to leave unchanged): ");
            String newName = sc.nextLine();
            System.out.print("Enter new website (hit enter to leave unchanged): ");
            String newWebSite = sc.nextLine();
            System.out.print("Enter new username (hit enter to leave unchanged): ");
            String newUsername = sc.nextLine();
            System.out.print("Enter new password (hit enter to leave unchanged): ");
            String newPassword = sc.nextLine();
            System.out.print("Enter new notes (hit enter to leave unchanged): ");
            String newNotes = sc.nextLine();
            System.out.println();
            System.out.println("Updating database ...");
            if (!newName.equals("")) {
                currentRow.getMap().put("name", newName);
            }
            if (!newWebSite.equals("")) {
                currentRow.getMap().put("website", newWebSite);
            }
            if (!newUsername.equals("")) {
                currentRow.getMap().put("username", newUsername);
            }
            if (!newPassword.equals("")) {
                currentRow.getMap().put("password", newPassword);
            }
            if (!newNotes.equals("")) {
                currentRow.getMap().put("notes", newNotes);
            }
            System.out.println("Done!");
        }
    }

    public static void addEntry(Database db) {
        System.out.print("Enter name: ");
        String name = sc.nextLine();
        System.out.print("Enter website: ");
        String website = sc.nextLine();
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();
        System.out.print("Enter notes: ");
        String notes = sc.nextLine();
        System.out.println("Got all information. Adding new entry ...");
        Table table = db.getTableHashMap().get("main");
        table.addEmptyRow();
        table.getLastRow().putAllArray(new String[]{"name", "website", "username", "password", "notes"},
                new String[]{name, website, username, password, notes});
        System.out.println("Done!");
    }

    public static void getDetailsOfEntry(Database db) {
        System.out.print("Enter name of entry whose details you would like to see: ");
        String name = sc.nextLine();
        Table table = db.getTableHashMap().get("main");
        List<Table.Row> rowList = table.search("name", name);
        Table.Row currentRow = null;
        if (rowList.size() == 0) {
            System.out.println("No entries found");
        } else {
            currentRow = rowList.get(0);
            String entryWebsite = (String)currentRow.getMap().get("website");
            String entryUsername = (String)currentRow.getMap().get("username");
            String entryPassword = (String)currentRow.getMap().get("password");
            String entryNotes = (String)currentRow.getMap().get("notes");
            System.out.println("Details of " + name + ":");
            System.out.println("Website: " + entryWebsite);
            System.out.println("Username: " + entryUsername);
            System.out.println("Password: " + entryPassword);
            System.out.println("Notes: " + entryNotes);
        }
    }

    public static void save(Database db, String fileName, String password) {
        try {
            byte[] pBytes = DBEncryptor.getBytes(db);
            byte[] cBytes = DBEncryptor.encryptBytes(pBytes, password);
            Files.write(Paths.get(fileName), cBytes);
            System.out.println("File saved successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String changeMasterPasswordAndSave(Database db, String fileName, String oldPassword) {
        System.out.print("Enter new password: ");
        String password = new String(System.console().readPassword());
        System.out.print("Confirm password: ");
        String confirm = new String(System.console().readPassword());
        if (!confirm.equals(password)) {
            System.out.println("Passwords dont match. Aborting ...");
            return oldPassword;
        }
        try {
            byte[] pBytes = DBEncryptor.getBytes(db);
            byte[] cBytes = DBEncryptor.encryptBytes(pBytes, password);
            Files.write(Paths.get(fileName), cBytes);
            System.out.println("File saved successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return password;
    }

    public static void generateNewPassword(Database db) {
        System.out.print("Enter name of entry whose password you would like to generate: ");
        String entryName = sc.nextLine();
        Table table = db.getTableHashMap().get("main");
        List<Table.Row> rowList = table.search("name", entryName);
        Table.Row currentRow = null;
        if (rowList.size() == 0) {
            System.out.println("No entries found");
        } else {
            currentRow = rowList.get(0);
            System.out.print("Enter length of password: ");
            int length;
            try {
                length = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number entered.");
                return;
            }

            String symbolsCharSet = "!\";#$%&'()*+,-./:;<=>?@[]^_`{|}~";
            String lowercaseLettersCharSet = "abcdefghijklmnopqrstuvwxyz";
            String upperCaseLettersCharset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            String numbersCharSet = "0123456789";
            StringBuilder charSetBuilder = new StringBuilder();
            charSetBuilder.append(lowercaseLettersCharSet);
            System.out.print("Allow upper case letters? (y/n): ");
            String response = sc.nextLine();
            if (response.equalsIgnoreCase("y")) {
                charSetBuilder.append(upperCaseLettersCharset);
            }
            System.out.print("Allow numbers? (y/n): ");
            response = sc.nextLine();
            if (response.equalsIgnoreCase("y")) {
                charSetBuilder.append(numbersCharSet);
            }
            System.out.print("Allow symbols? (y/n): ");
            response = sc.nextLine();
            if (response.equalsIgnoreCase("y")) {
                charSetBuilder.append(symbolsCharSet);
            }

            StringBuilder passwordBuilder = new StringBuilder();
            String charSet = charSetBuilder.toString();
            for (int i = 0; i < length; i++) {
                char c = charSet.charAt ( (int) (Math.random() * charSet.length()) );
                passwordBuilder.append(c);
            }
            System.out.println("Your generated password is: " + passwordBuilder.toString());
            System.out.print("Would you like to use it? (y/n): ");
            String choice = sc.nextLine();
            if (choice.equalsIgnoreCase("y")) {
                currentRow.getMap().put("password", passwordBuilder.toString());
                System.out.println("The password has been changed");
            } else {
                System.out.println("No change will be done to the original password");
            }
        }
    }
}
