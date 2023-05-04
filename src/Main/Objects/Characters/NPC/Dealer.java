package Main.Objects.Characters.NPC;

import Main.Objects.Entity;
import Main.Utils.Messenger;
import Main.Utils.PersonLoader;

import java.util.List;

public class Dealer extends NonPlayerCharacter {


    private static final int ID = 4;
    public static final int DEFAULT_CID = -2;
    private final String name = PersonLoader.loadName(-2);
    private List<Speech> speeches = PersonLoader.loadSpeeches(-2);

    static {
        try {
            Entity.addInstance(4, Dealer.class);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * ONLY FOR INITIALIZING
     */
    public Dealer() {
        super("Dealer", DEFAULT_CID);
        Messenger.systemMessage("instance initiated", Dealer.class);
    }

    public Dealer(String name, int x, int y) {
        super(name, x, y, ID, DEFAULT_CID);
    }

    public Dealer(String name, int x, int y, int CID) {
        super(name, x, y, ID, CID);
    }

    @Override
    public char getSymbol() {
        return "$".charAt(0);
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public void talk() {

    }


}
