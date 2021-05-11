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
            password = sc.nextLine();
            System.out.print("Reenter vault master password: ");
            String confirmPassword = sc.nextLine();
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
            password = sc.nextLine();
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

        while (true) {
            System.out.println("1) View All Entry Names\n" +
                    "2) Edit an entry\n" +
                    "3) Add an entry\n" +
                    "4) View Details of an entry\n" +
                    "5) Save changes\n" +
                    "6) Save and quit\n" +
                    "7) Quit without saving");
            System.out.print("Enter your choice (1-5): ");
            int choice = 0;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number entered.");
                System.out.println();
                continue;
            }
            switch (choice) {
                case 1: printAllEntryNames(passwordDB); break;
                case 2: editEntry(passwordDB); break;
                case 3: addEntry(passwordDB); break;
                case 4: getDetailsOfEntry(passwordDB); break;
                case 5: save(passwordDB, vaultFileName, password); break;
                case 6: save(passwordDB, vaultFileName, password); return;
                case 7: return;
                default:
                    System.out.println("Invalid choice entered");
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
}
