package flashcards.model;
import flashcards.operation.Operation;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static flashcards.Main.buffer;
import static flashcards.Main.clientInput;

public class Quiz implements Operation {
    private List<Flashcard> flashcardList;
    Scanner in = new Scanner(System.in);

    String exportFileName;
    public Quiz() {
        flashcardList = new ArrayList<>();
    }

    public void createQuiz() throws IOException {
        System.out.println("The card:");
        Flashcard flashcard = createCard();
        if(flashcard != null)
            flashcardList.add(flashcard);
    }

    public void removeFlashcard() throws IOException {
        String term = in.nextLine();
        clientInput = term + "\n";
        buffer.write(clientInput.getBytes());
        for(int i = 0; i < flashcardList.size(); i++){
            if(flashcardList.get(i).getTerm().equals(term)){
                flashcardList.remove(i);
                System.out.println("The card has been removed.");
                return;
            }
        }
        System.out.println("Can't remove \"" + term +"\": there is no such card.");
        return;
    }

    public void checkQuiz(int times) throws IOException {
        int counter = 0;
        for(int i = 0; i < times; i++){
            if(counter == flashcardList.size())
                counter = 0;
            String response = checkAnswer(counter);
            System.out.println(response);
            counter++;
        }
    }

    public void saveToFile() throws IOException {
        int counter = 0;
        File file = new File(in.nextLine());
        clientInput = file.getName() + "\n";
        buffer.write(clientInput.getBytes());
        try(FileOutputStream buff = new FileOutputStream(file)) {
            ObjectOutputStream oos = new ObjectOutputStream(buff);
            for (Flashcard flash: flashcardList) {
                oos.writeObject(flash);
                counter++;
            }
            System.out.println(counter +" cards have been saved.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveToFile(String filename) throws IOException {
        exportFileName = filename;
        int counter = 0;
        File file = new File(filename);
        clientInput = file.getName() + "\n";
        buffer.write(clientInput.getBytes());
        try(FileOutputStream buff = new FileOutputStream(file)) {
            ObjectOutputStream oos = new ObjectOutputStream(buff);
            for (Flashcard flash: flashcardList) {
                oos.writeObject(flash);
                counter++;
            }
            System.out.println(counter +" cards have been saved.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void readFromFile() throws IOException {
        File file = new File(in.nextLine());
        clientInput = file.getName() + "\n";
        buffer.write(clientInput.getBytes());

        int counter = 0;
        if(!file.exists()){
            System.out.println("File not found.");
            return;
        }
        try(FileInputStream reader = new FileInputStream(file)) {
            ObjectInputStream ois = new ObjectInputStream(reader);
            Flashcard flashcard;
            while ((flashcard = (Flashcard) ois.readObject()) != null){
                importCheck(flashcard);
                counter++;
            }
        } catch (IOException e) {
            System.out.println(counter + " cards have been loaded.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void readFromFile(String filename) throws IOException {
        File file = new File(filename);
        clientInput = file.getName() + "\n";
        buffer.write(clientInput.getBytes());

        int counter = 0;
        if(!file.exists()){
            System.out.println("File not found.");
            return;
        }
        try(FileInputStream reader = new FileInputStream(file)) {
            ObjectInputStream ois = new ObjectInputStream(reader);
            Flashcard flashcard;
            while ((flashcard = (Flashcard) ois.readObject()) != null){
                importCheck(flashcard);
                counter++;
            }
        } catch (IOException e) {
            System.out.println(counter + " cards have been loaded.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Flashcard createCard() throws IOException {
        Flashcard flashcard = new Flashcard();
        String term;
        String def;
        term = in.nextLine();
        clientInput = term + "\n";
        buffer.write(clientInput.getBytes());
        if (containsTerm(term)) {
            System.out.println("The card \"" + term + "\" already exists.");
            return null;
        }
        flashcard.setTerm(term);

        System.out.println("The definition of the card:");
        def = in.nextLine();
        clientInput = def + "\n";
        buffer.write(clientInput.getBytes());
        if (containsDefinition(def)) {
            System.out.println("The definition \"" + def + "\" already exists.");
            return null;
        }
        flashcard.setDefinition(def);

        System.out.println("The pair ("+ "\""+flashcard.getTerm()+"\"" +":"
                +"\"" + flashcard.getDefinition() + "\"" +") has been added.");
        return flashcard;
    }

    public void importCheck(Flashcard flashcard){
        for(int i = 0; i < flashcardList.size(); i++){
            if(flashcardList.get(i).getTerm().equals(flashcard.getTerm())){
                flashcardList.set(i,flashcard);
                return;
            }
        }
        flashcardList.add(flashcard);
    }

    @Override
    public String checkAnswer(int index) throws IOException {
        System.out.println("Print the definition of " + '\"' +flashcardList.get(index).getTerm()+ '\"' +":");
        String answer = in.nextLine();
        clientInput = answer + "\n";
        buffer.write(clientInput.getBytes());
        if(answer.equals(flashcardList.get(index).getDefinition())) {
            return "Correct!";
        }
        else {
            flashcardList.get(index).increaseMistakes();
            String anotherTerm = checkAnotherDef(answer);
            if(!anotherTerm.isEmpty()){
                return "Wrong. The right answer is " + '\"' + flashcardList.get(index).getDefinition() + '\"' +
                        ",but your definition is correct for " +'\"' +anotherTerm +'\"' +".";
            }else
                return "Wrong. The right answer is " + '\"' + flashcardList.get(index).getDefinition() + '\"' + ".";
        }
    }

    public Boolean containsTerm(String term){
        for (Flashcard flashcard : flashcardList){
            if(flashcard.getTerm().equals(term))
                return true;
        }
        return false;
    }

    public Boolean containsDefinition(String definition){
        for (Flashcard flashcard : flashcardList){
            if(flashcard.getDefinition().equals(definition))
                return true;
        }
        return false;
    }

    public String checkAnotherDef(String def){
        for (Flashcard flashcard : flashcardList){
            if(flashcard.getDefinition().equals(def)) {
                return flashcard.getTerm();
            }
        }
        return "";
    }

    public void getHardestCards(){
        if(flashcardList.size() == 0){
            System.out.println("There are no cards with errors.");
            return;
        }
        List<Flashcard> list = new ArrayList<>();
        list.add(flashcardList.get(0));
        for(int i = 0; i < flashcardList.size(); i++){
            for(int j = 0; j < flashcardList.size(); j++){
                if(list.get(0).getMistakes() == flashcardList.get(j).getMistakes()) {
                    if(!list.contains(flashcardList.get(j)))
                        list.add(flashcardList.get(j));
                }
                else if(list.get(0).getMistakes() < flashcardList.get(j).getMistakes()){
                    list.clear();
                    list.add(flashcardList.get(j));
                }
            }
        }
        if(list.get(0).getMistakes() == 0) {
            System.out.println("There are no cards with errors.");
            return;
        }
        if(list.size() > 1){
            System.out.print("The hardest cards are ");
            for (int i = 0; i < list.size(); i++){
                System.out.print("\"" +list.get(i).getTerm() +"\"");
                if(i == (list.size() - 1)) {
                    System.out.println(".");
                    System.out.println("You have " +list.get(0).getMistakes() +" errors answering them.");
                }
                else System.out.print(", ");
            }
        }
        else{
            System.out.print("The hardest card is " +"\"" +list.get(0).getTerm() +"\". ");
            System.out.println("You have " +list.get(0).getMistakes() +" errors answering it");
        }
    }

    public void resetStats(){
        for(int i = 0; i < flashcardList.size(); i++){
            flashcardList.get(i).resetMistakes();
        }
    }
}
