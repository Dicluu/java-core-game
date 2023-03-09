package Main.Singletones.Utils;

import Main.Items.Item;
import Main.Items.Tools.Tool;
import Main.Maps.Map;
import Main.Maps.Cell;
import Main.Objects.Characters.Character;
import Main.Objects.Characters.Player;
import Main.Objects.Entity;
import Main.Objects.Materials.Material;
import Main.Objects.Unique.Entrance;
import Main.Singletones.GameExecutor;
import Main.Utils.Messenger;
import Main.Utils.Timers.TimeCounter;
import Main.Utils.Timers.Timer;

import java.util.*;


public class CommandManager {

    private static GameExecutor ge = GameExecutor.getGame();
    private static Scanner num = new Scanner(System.in);

    public static void move(String direction) {
        Player currentPlayer = ge.getCurrentPlayer();
        Map currentMap = ge.getCurrentMap();
        switch (direction) {
            case "up":
                currentPlayer.setY(currentPlayer.getY() - 1);
                if (currentPlayer.getY() < 0) {
                    currentPlayer.setY(0);
                    Messenger.helpMessage("You stuck in the edge of map");
                }
                break;
            case "down":
                currentPlayer.setY(currentPlayer.getY() + 1);
                if (currentPlayer.getY() >= currentMap.getY()) {
                    currentPlayer.setY(currentMap.getY() - 1);
                    Messenger.helpMessage("You stuck in the edge of map");
                }
                break;
            case "right":
                currentPlayer.setX(currentPlayer.getX() + 1);
                if (currentPlayer.getX() >= currentMap.getX()) {
                    currentPlayer.setX(currentMap.getX() - 1);
                    Messenger.helpMessage("You stuck in the edge of map");
                }
                break;
            case "left":
                currentPlayer.setX(currentPlayer.getX() - 1);
                if (currentPlayer.getX() < 0) {
                    currentPlayer.setX(0);
                    Messenger.helpMessage("You stuck in the edge of map");
                }
                break;
        }
        renderObjects();
        updateMapAfterMove(currentPlayer);
    }

    public static void updateMapAfterMove(Player currentPlayer) {
        GameExecutor.getGame().getCurrentMap().setObject(currentPlayer);
        GameExecutor.getGame().getCurrentMap().clearCellsFromObject(currentPlayer);
        GameExecutor.getGame().getCurrentMap().showMap();
    }

    public static void renderObjects() {
        Map cm = ge.getCurrentMap();
        Player cp = ge.getCurrentPlayer();
        Set<Entity> objects = cm.getObjects(cp.getX(), cp.getY());
        boolean isMaterial = false, isEntrance = false;
        for (Entity object : objects) {
            if (Material.getMaterialById(object.getId()) != null) {
                isMaterial = true;
            }
            if (object.getId() == 2) {
                isEntrance = true;
            }
        }
        if (isMaterial) {
            Messenger.ingameMessage("Here's some materials you can get by command 'get'");
        }
        if (isEntrance) {
            Messenger.ingameMessage("Here's portal you can come in by command 'come'");
        }
    }

    public static void showInventory(Character person) {
        person.showInventory();
    }

    public static void getAction(TimeCounter timecounter) {
        Player cp = GameExecutor.getGame().getCurrentPlayer();
        Cell cc = cp.getCurrentCell();
        List<Material> cellMaterials = new ArrayList<>();
        for (Entity entity : cc.getObjects()) {
            if (Material.getMaterialById(entity.getId()) != null) {
                cellMaterials.add((Material) entity);
            }
        }
        switch (cellMaterials.size()) {
            case 0:
                Messenger.ingameMessage("Here is nothing to get");
                break;
            case 1:
                extract(cp, timecounter, cc, cellMaterials.get(0));
                break;
            default:
                askWhichMaterialToGet(cp, cellMaterials, timecounter, cc);
                break;
        }
    }

    private static void extract(Character person, TimeCounter timecounter, Cell cc, Material material) throws CloneNotSupportedException {
        if (person.isPresence(material.getMaterial().getTOOLID())) {
            if (!person.putItem(Item.newInstance(material.getMaterial().toItem().getId()))) {
                Messenger.ingameMessage("Your inventory is full");
            } else {
                Tool ct = chooseTool((Player) person, material);
                playAnimation((long) material.getMaterial().getComplexity(), (long) ct.getEfficiency());
                timecounter.createObjectTimeline(material, cc);
                Messenger.ingameMessage("You got a " + material.getMaterial().toItem().getName());
            }
        } else {
            Messenger.ingameMessage("You don't have necessary tools to get this material");
        }
    }

    public static void askWhichMaterialToGet(Character cp, List<Material> materials, TimeCounter timecounter, Cell cc) {
        for (int i = 0; i < materials.size(); i++) {
            System.out.print(i + 1 + ") " + materials.get(i).getMaterial().getName() + " ");
        }
        System.out.println(" ");
        Messenger.ingameMessage("Type id of material you want to get");
        int answer = num.nextInt();
        if (answer > 0 && answer <= materials.size()) {
            extract(cp, timecounter, cc, materials.get(answer - 1));
        } else {
            Messenger.ingameMessage("You wrote wrong id");
        }
    }

    public static void come() {
        Player cp = GameExecutor.getGame().getCurrentPlayer();
        Cell cc = cp.getCurrentCell();
        for (Entity entity : cc.getObjects()) {
            if (entity.getId() == 2) {
                Entrance entrance = (Entrance) entity;
                GameExecutor.getGame().setCurrentMap(Map.getMapById(entrance.getReferMapId()));
                GameExecutor.getGame().getCurrentMap().setObject(cp);
                Messenger.ingameMessage("You moved to new place");
                Messenger.systemMessage("Map changed to id: " + entrance.getReferMapId());
                showMap();
            }
        }
    }

    public static void showMap() {
        GameExecutor.getGame().getCurrentMap().showMap();
    }

    public static void switchSettings() {
        Scanner num = new Scanner(System.in);
        Messenger.ingameMessage("write which setting you want to switch");
        String status = num.nextLine();
        switch (status) {
            case "help":
                if (Messenger.isHelp()) {
                    Messenger.setHelp(false);
                } else {
                    Messenger.setHelp(true);
                }
                Messenger.ingameMessage("Help information switched");
                break;
            case "system":
                if (Messenger.isSystem()) {
                    Messenger.setSystem(false);
                } else {
                    Messenger.setSystem(true);
                }
                Messenger.ingameMessage("System information switched");
                break;
            default:
                Messenger.ingameMessage("setting's attribute is wrong");
        }
    }

    private static void playAnimation(Long complexity, Long efficiency) {
        Long interval = ((complexity * 1000) / efficiency);
        Timer timer = new Timer(interval);
        System.out.print("[");
        while (!timer.touch()) {
            Timer microtimer = new Timer((interval / 15));
            while (!microtimer.touch()) {
            }
            System.out.print("|");
        }
        System.out.println("]");
    }

    public static void showWallet() {
        Character cp = GameExecutor.getGame().getCurrentPlayer();
        Messenger.ingameMessage("You have " + cp.getWallet() + " $ in your bag");
    }

    private static Tool chooseTool(Player cp, Material cm) {
        ArrayList<Item> desired = cp.getAllOfKind(cm.getMaterial().getTOOLID());
        Scanner num = new Scanner(System.in);
        int id;
        if (desired.size() > 1) {
            Messenger.ingameMessage("Choose what a tool you want to use");
            showDesired(desired);
            try {
                id = num.nextInt();
                System.out.println(desired.get(id-1).getUID());
                return (Tool) desired.get(id-1);
            } catch (InputMismatchException | ArrayIndexOutOfBoundsException | NullPointerException ae) {
                Messenger.systemMessage("Exception in method chooseTool() catch", CommandManager.class);
            }
        } else {
            return (Tool) desired.get(0);
        }
        return null;
    }

    private static void showDesired(ArrayList<Item> items) {
        for (int i = 0; i < items.size(); i++) {
            Messenger.ingameMessage("id: " + (i+1) + "; name: " + items.get(i).getName());
        }
    }
}