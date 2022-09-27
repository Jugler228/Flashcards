package flashcards.model;

import java.io.Serializable;

public class Flashcard implements Serializable {
    private String term;
    private String definition;

    private int mistakes = 0;

    public Flashcard() {
    }
    public Flashcard(String term, String definition) {
        this.term = term;
        this.definition = definition;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public int getMistakes() {
        return mistakes;
    }
    public String getDefinition() {
        return definition;
    }

    public void setMistakes(int mistakes) {
        this.mistakes = mistakes;
    }
    public void setDefinition(String definition) {
        this.definition = definition;
    }

    @Override
    public String toString() {
        return "Card:\n" + term + '\n' +
                "Definition:\n" + definition;
    }

    public void increaseMistakes(){
        mistakes++;
    }

    public void resetMistakes(){
        mistakes = 0;
    }
}
