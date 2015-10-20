package objects;

import java.util.ArrayList;

/**
 * Created by federico on 07/10/2015.
 */

public class Category {
    String name;
    String englishName;

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    private ArrayList<String> phrases = new ArrayList<String>();

    public Category() {

    }

    public void addPhrase(String phrase){
        phrases.add(phrase);
    }

    public ArrayList<String> getPhrases() {
        return phrases;
    }

    public void setPhrases(ArrayList<String> phrases) {
        this.phrases = phrases;
    }

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
