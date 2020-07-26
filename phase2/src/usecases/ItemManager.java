package usecases;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import entities.Item;
import gateways.ItemsGateway;

// TODO javadoc
// TODO add method to parse list of strings to item
/**
 * Manager for items which creates an item or takes in an item to edit.
 *
 * @author Isaac
 */

public class ItemManager extends ItemUtility{

    /**
     * The gateway which deals with items.
     */
    private final ItemsGateway itemsGateway;

    private int generateValidIDCounter;

    /**
     * Constructor for ItemManager which stores an ItemsGateway.
     *
     * @param itemsGateway The gateway for interacting with the persistent storage of items
     */
    public ItemManager(ItemsGateway itemsGateway) {
        this.itemsGateway = itemsGateway;
        generateValidIDCounter = 0;
    }

    public int generateValidID() {
        return generateValidIDCounter++;
    }

    /**
     * Creates a new item and stores in the persistent storage and allows for editing of the
     * new item.
     *
     * @param name        The name of the item
     * @param description The description of the item
     * @param ownerID     The id of the owner of the item
     */
    public void createItem(String name, String description, int ownerID) {
        int id = generateValidID();
        Item item = new Item(id, name, description, ownerID);
        this.items.put(id, item);
    }

    /**
     * Deletes an item in the system and returns if item was successfully deleted.
     *
     * @param itemId The item to be deleted
     * @return Whether the deletion was successful
     */
    public boolean removeItem(int itemId) {
        boolean result = false;
        if (items.containsKey(itemId)) {
            items.get(itemId).setOwnerID(-1);
            result = true;
            this.items.remove(itemId);
        }
        return result;
    }


    /**
     * Get the string representation of item with the id entered.
     *
     * @param itemID ID of the item
     * @return String of item with the entered ID
     */
    public String getItemStringById(int itemID) {
        return super.findItemById(itemID).toString();
    }


    /**
     * Gets the approval status of the item.
     *
     * @param itemId item which information is being returned about
     * @return approval status of the item
     */
    public boolean isApproved(int itemId) {
        return items.get(itemId).isApproved();
    }

    /**
     * Gets the ID of the owner of the item.
     *
     * @param itemID ID of the item which information is being returned about
     * @return ID of the owner of the item (-1 if no item can be found).
     */
    public int getOwnerId(int itemID) {
        Item item = super.findItemById(itemID);
        if (item != null) return item.getOwnerID();
        return -1;
    }

    /**
     * Update the owner of the item.
     *
     * @param itemId    item being updated
     * @param ownerID new owner of the item
     */
    public void updateOwner(int itemId, int ownerID) {
        items.get(itemId).setOwnerID(ownerID);
    }

    /**
     * Update the approval status of the item.
     *
     * @param itemId     item being updated
     * @param approval new approval status of the item
     */
    public void updateApproval(int itemId, boolean approval) {
        if (approval) {
            items.get(itemId).approve();
        } else {
            items.get(itemId).disapprove();
        }
    }

    /**
     * Retrieves all items stored in persistent storage.
     *
     * @return List of all items
     */
    public List<Item> getAllItems() {
        List<Item> Items = new ArrayList<>();
        for (Map.Entry<Integer, Item> entry : items.entrySet()) {
            if (items.get(entry.getKey()).getOwnerID() != -1) {
                Items.add(items.get(entry.getKey()));
            }
        }
        return Items;
    }

    /**
     * Retrieves all items stored in persistent storage in string format.
     *
     * @return List of all items in string format
     */
    public List<String> getAllItemsString() {
        List<String> StringItems = new ArrayList<>();
        for (Item item : getAllItems()) {
            StringItems.add(item.toString());
        }
        return StringItems;
    }

}
