package Main.Objects.Characters.NPC;

import Main.Objects.Entity;
import Main.Utils.Messenger;
import Main.Utils.PersonLoader;

import java.util.List;

public class Peasant extends NonPlayerCharacter {

    private static final int ID = 7;
    private static final int DEFAULT_CID = 0;
    private static final String name = PersonLoader.loadName(DEFAULT_CID);
    private static List<Speech> speeches = PersonLoader.loadSpeeches(DEFAULT_CID);

    static {
        try {
            Entity.addInstance(7, Peasant.class);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    public Peasant() {
        super("peasant", DEFAULT_CID);
        Messenger.systemMessage("instance initiated", Peasant.class);
    }

    @Override
    public int getId() {
        return ID;
    }


    @Override
    public void talk() {

    }
}
