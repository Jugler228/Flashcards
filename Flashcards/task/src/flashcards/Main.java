package flashcards;

import flashcards.model.Quiz;

import java.io.*;

public class Main {
    public static ByteArrayOutputStream buffer;

    public static String clientInput;
    static BufferedReader reader = new BufferedReader(
            new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {
        
        String exportFilename = "";
        String importFilename = "";
        if(args.length != 0) {
            for(int i = 0; i < args.length; i++){
                if(args[i].equals("-export")){
                    if((i + 1) < args.length){
                        if(!args[i + 1].equals("-import"))
                            exportFilename = args[i + 1];
                    }
                }
                if(args[i].equals("-import")){
                    if((i + 1) < args.length){
                        if(!args[i + 1].equals("-export"))
                            importFilename = args[i + 1];
                    }
                }
            }
        }
        Quiz quiz = new Quiz();
        String choice;

        buffer = new ByteArrayOutputStream();
        OutputStream teeStream = new TeeOutputStream(System.out, buffer);

        System.setOut(new PrintStream(teeStream));

        if(!importFilename.isEmpty()){
            quiz.readFromFile(importFilename);
        }

        while (true){
            System.out.println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            choice = reader.readLine();
            clientInput = choice + "\n";
            buffer.write(clientInput.getBytes());
            switch (choice){
                case "add":
                    quiz.createQuiz();
                    break;

                case "remove":
                    System.out.println("Which card?");
                    quiz.removeFlashcard();
                    System.out.println();
                    break;

                case "export":
                    System.out.println("File name:");
                    //exportFilename = reader.readLine();
                    quiz.saveToFile();
                    System.out.println();
                    break;

                case "import":
                    System.out.println("File name:");
                    quiz.readFromFile();
                    System.out.println();
                    break;

                case "hardest card":
                    quiz.getHardestCards();
                    System.out.println();
                    break;

                case "log":
                    System.out.println("File name:");
                    saveToFile();
                    System.out.println("The log has been saved.");
                    System.out.println();
                    break;

                case "reset stats":
                    quiz.resetStats();
                    System.out.println("Card statistics have been reset.");
                    System.out.println();
                    break;

                case "ask":
                    System.out.println("How many times to ask?");
                    String times = reader.readLine();
                    clientInput = times + "\n";
                    buffer.write(clientInput.getBytes());
                    quiz.checkQuiz(Integer.parseInt(times));
                    System.out.println();
                    break;

                case "exit":
                    System.out.println("Bye bye!");
                    if(!exportFilename.isEmpty())
                        quiz.saveToFile(exportFilename);
                    System.out.println();
                    System.exit(1);

                default:
                    System.out.println("There isn`t such command!");
                    System.out.println();
            }
        }
    }
    static public void saveToFile() throws IOException {
        File file = new File(reader.readLine());
        clientInput = file.getName() + "\n";
        buffer.write(clientInput.getBytes());
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(String.valueOf(buffer));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
