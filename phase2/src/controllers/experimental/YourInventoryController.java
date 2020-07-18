package controllers.experimental;

import java.util.List;

import controllers.InputHandler;
import entities.Item;
import gateways.UseCasePool;
import presenters.experimental.YourInventoryPresenter;
import usecases.AuthManager;
import usecases.ItemManager;
import usecases.AccountManager;
import usecases.ItemUtility;

/**
 * Controller that deals with your inventory.
 *
 * @author Ethan (follow @ethannomiddlenamelam on instagram)
 */
public class YourInventoryController {

    /**
     * The presenter counterpart to this class
     */
    private final YourInventoryPresenter yourInventoryPresenter;

    /**
     * An instance of ItemManager to access items
     */
    private final ItemManager itemManager;

    /**
     * An instance of AccountManager to access accounts
     */
    private final AccountManager accountManager;

    /**
     * An instance of ItemUtility to utilize items
     */
    private final ItemUtility itemUtility;

    /**
     * An instance of AuthManager to check permissions
     */
    private final AuthManager authManager;

    /**
     * An instance of ControllerHelper for helper methods
     */
    private final InputHandler inputHandler;

    /**
     * Constructor to initialize all the instances, from ManualConfig,
     * and add options to actions depending on the user's permissions
     *
     * @param useCasePool       the configuration for the program
     * @param yourInventoryPresenter the presenter for displaying the inventory
     */
    public YourInventoryController(UseCasePool useCasePool, YourInventoryPresenter  yourInventoryPresenter) {
        this.itemManager = useCasePool.getItemManager();
        this.accountManager = useCasePool.getAccountManager();
        this.itemUtility = useCasePool.getItemUtility();
        this.yourInventoryPresenter = yourInventoryPresenter;
        this.authManager = useCasePool.getAuthManager(); //TODO: figure out how we're using permissions to dictate what the view shows the user
        this.inputHandler = new InputHandler();
    }

    private void displayAllYourInventory() {
        List<String> allYourItems = itemUtility.getAllInventoryOfAccountString(accountManager.getCurrAccountID());
        this.yourInventoryPresenter.displayInventory(allYourItems);
    }

    /**
     * Creates an item
     *
     * @param name The name of the item
     * @param description The description of the item
     */
    private boolean createItem(String name, String description) {

        if(inputHandler.isValidCSVStr(name) && inputHandler.isValidCSVStr(description)) {
            itemManager.createItem(name, description, accountManager.getCurrAccountID());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes an item from the inventory
     *
     * @param ind The index in the list of items in this user's
     */
    private boolean removeFromYourInventory(String ind) {
        List<Item> items = itemUtility.getAllInventoryOfAccount(accountManager.getCurrAccountID());
        return itemManager.removeItem(items.get(Integer.parseInt(ind)));
    }

}
