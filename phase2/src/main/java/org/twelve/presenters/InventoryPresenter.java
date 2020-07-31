package org.twelve.presenters;

import java.util.List;

public interface InventoryPresenter {

    void setInventoryItems(List<String> approvedItems, List<String> pendingItems);
    List<String> getApprovedItems();
    List<String> getPendingItems();

}
