package org.twelve.controllers;

import org.twelve.gateways.AccountGateway;
import org.twelve.gateways.GatewayPool;
import org.twelve.gateways.ItemsGateway;
import org.twelve.presenters.WishlistPresenter;
import org.twelve.usecases.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for managing personal wishlist.
 */
public class WishlistController {

    private final WishlistManager wishlistManager;
    private final SessionManager sessionManager;
    private final ItemManager itemManager;
    private WishlistPresenter wishlistPresenter;
    private final AccountRepository accountRepository;

    private final AccountGateway accountGateway;
    private final ItemsGateway itemsGateway;

    /**
     * Constructor of controller for managing personal wishlist.
     * @param useCasePool An instance of {@link org.twelve.usecases.UseCasePool}.
     * @param gatewayPool An instance of {@link org.twelve.gateways.GatewayPool}.
     */
    public WishlistController(UseCasePool useCasePool, GatewayPool gatewayPool) {

        this.wishlistManager = useCasePool.getWishlistManager();
        this.sessionManager = useCasePool.getSessionManager();
        this.itemManager = useCasePool.getItemManager();
        this.accountRepository = useCasePool.getAccountRepository();
        this.accountGateway = gatewayPool.getAccountGateway();
        this.itemsGateway = gatewayPool.getItemsGateway();

    }

    /**
     * Updates items in the wishlist.
     */
    public void updateItems() {

        accountGateway.populate(accountRepository);
        itemsGateway.populate(itemManager);

        List<String> wishlistItems = new ArrayList<>();

        for (int id : wishlistManager.getWishlistFromID(sessionManager.getCurrAccountID())) {

            wishlistItems.add(itemManager.getItemNameById(id));

        }

        List<String> warehouseItems = new ArrayList<>();

        for (int id : itemManager.getNotInAccountIDs(sessionManager.getCurrAccountID())) {

            warehouseItems.add(itemManager.getItemNameById(id));

        }

        wishlistPresenter.setWishlistItems(wishlistItems);
        wishlistPresenter.setWarehouseItems(warehouseItems);

    }

    /**
     * Provides the wishlist controller with an appropriate presenter.
     * @param wishlistPresenter An instance of a class that implements {@link org.twelve.presenters.WishlistPresenter}.
     */
    public void setWishlistPresenter(WishlistPresenter wishlistPresenter) {
        this.wishlistPresenter = wishlistPresenter;
    }

    /**
     * Add item at itemIndex to the wishlist.
     * @param itemIndex Index of item to add to the wishlist.
     */
    public void addToWishlist(int itemIndex) {

        wishlistManager.addItemToWishlist(sessionManager.getCurrAccountID(),
                itemManager.getNotInAccountIDs(sessionManager.getCurrAccountID()).get(itemIndex));

        updateItems();

    }

    /**
     * Remove item at itemIndex from the wishlist.
     * @param itemIndex Index of item to rmeove from the wishlist.
     */
    public void removeFromWishlist(int itemIndex) {

        wishlistManager.removeItemFromWishlist(sessionManager.getCurrAccountID(),
                wishlistManager.getWishlistFromID(sessionManager.getCurrAccountID()).get(itemIndex));

        updateItems();

    }

    /**
     * Changes which item the user has selected from the wishlist and updates presenter with new name & desc.
     * @param itemIndex Index of selected wishlist item.
     */
    public void changeSelectedWishlistItem(int itemIndex) {

        int itemId = wishlistManager.getWishlistFromID(sessionManager.getCurrAccountID()).get(itemIndex);

        wishlistPresenter.setSelectedItemName(itemManager.getItemNameById(itemId));
        wishlistPresenter.setSelectedItemDesc(itemManager.getItemDescById(itemId));

    }

    /**
     * Changes which item the user has selected from the warehouse and updates presenter with new name & desc.
     * @param itemIndex Index of selected warehouse item.
     */
    public void changeSelectedWarehouseItem(int itemIndex) {

        int itemId = itemManager.getNotInAccountIDs(sessionManager.getCurrAccountID()).get(itemIndex);

        wishlistPresenter.setSelectedItemName(itemManager.getItemNameById(itemId));
        wishlistPresenter.setSelectedItemDesc(itemManager.getItemDescById(itemId));

    }

}
