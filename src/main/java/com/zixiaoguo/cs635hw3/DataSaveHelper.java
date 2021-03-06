package com.zixiaoguo.cs635hw3;

import com.zixiaoguo.cs635hw3.commands.AddBookCommand;
import com.zixiaoguo.cs635hw3.commands.Command;
import com.zixiaoguo.cs635hw3.inventory.BaseInventory;
import com.zixiaoguo.cs635hw3.inventory.Inventory;

import java.io.*;
import java.util.ArrayList;

public class DataSaveHelper {
    //TODO: add originator caretaker objects
    private static int commandCounter = 0;
    private static ArrayList<Command> commands = new ArrayList<>();
    private static Inventory inventory;

    private static Caretaker caretaker;
    private static Originator originator;


    public static void setParameters(Inventory inventory, Caretaker caretaker, Originator originator) {
        DataSaveHelper.inventory = inventory;
        DataSaveHelper.caretaker = caretaker;
        DataSaveHelper.originator = originator;
    }

    public static void saveCommand(Command command) throws IOException {
        // save one memento every 10 commands

        try {
            FileOutputStream f = new FileOutputStream(new File("savedCommand.ser"));
            ObjectOutputStream o = new ObjectOutputStream(f);

            commands.add(command);
            o.writeObject(commands);
            commandCounter++;


            o.close();
            f.close();


        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
            e.printStackTrace();
        }
        if (commandCounter == 10) {
            saveMemento(inventory);

        }
    }

    //TODO: save memento rather than inventory
    public static void saveMemento(Inventory inventory) throws IOException {
        ArrayList<Book> books = inventory.getBooks();
        originator.setState(books);
        caretaker.add(originator.createMemento());
        try {
            FileOutputStream f = new FileOutputStream(new File("savedMemento.ser"));
            ObjectOutputStream o = new ObjectOutputStream(f);

            o.writeObject(caretaker.getMomento());

            o.close();
            f.close();


        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
            e.printStackTrace();
        }
        commands.clear();   //we want to save empty commands and overwrite commands saved before for new memento
        new FileOutputStream("savedCommand.ser").close();   //delete the contents in the savedCommand file
        commandCounter = 0;
    }

    public static void restoreFromMemento (Inventory inventory) {
        Memento memento = null;

        try {

            FileInputStream f = new FileInputStream(new File("savedMemento.ser"));
            ObjectInputStream o = new ObjectInputStream(f);

            memento = (Memento) o.readObject();

            System.out.println("memento read from file: " +memento.toString());

            o.close();
            f.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        inventory.setBooks(originator.restoreState(memento));
    }

    public static void restoreFromCommands () throws IOException {
        ArrayList<Command> commands = null;
        try {

            FileInputStream f = new FileInputStream(new File("savedCommand.ser"));
            ObjectInputStream o = new ObjectInputStream(f);

            commands = (ArrayList<Command>) o.readObject();

            System.out.println("command read from file: " +commands.toString());

            o.close();
            f.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        for (Command command : commands) {
            inventory.setBooks(command.execute(inventory.getBooks()));
        }
    }



}
