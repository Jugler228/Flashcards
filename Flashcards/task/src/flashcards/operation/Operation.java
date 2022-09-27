package flashcards.operation;

import flashcards.model.Flashcard;

import java.io.IOException;

public interface Operation {
    public Flashcard createCard() throws IOException;
    public String checkAnswer(int index) throws IOException;
}
