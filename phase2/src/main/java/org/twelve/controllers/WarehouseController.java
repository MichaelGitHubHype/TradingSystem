package org.twelve.controllers;

import org.twelve.gateways.GatewayPool;
import org.twelve.gateways.ItemsGateway;
import org.twelve.presenters.WarehousePresenter;
import org.twelve.usecases.ItemManager;
import org.twelve.usecases.UseCasePool;

import java.util.ArrayList;
import java.util.List;

public class WarehouseController {

    private final ItemManager itemManager;
    private WarehousePresenter warehousePresenter;
    private final ItemsGateway itemsGateway;

    public WarehouseController(UseCasePool useCasePool, GatewayPool gatewayPool) {

        itemManager = useCasePool.getItemManager();
        this.itemsGateway = gatewayPool.getItemsGateway();

    }

    public void setWarehousePresenter(WarehousePresenter warehousePresenter) {
        this.warehousePresenter = warehousePresenter;
    }

    public void approveItem(int itemIndex) {

        itemManager.updateApproval(itemManager.getDisapprovedIDs().get(itemIndex), true);
        updatePendingItems();

    }

    public void changeSelectedItem(int itemIndex) {

        String name = itemIndex >= 0 ? itemManager.getItemNameById(itemManager.getDisapprovedIDs().get(itemIndex)) : "";
        String desc = itemIndex >= 0 ? itemManager.getItemDescById(itemManager.getDisapprovedIDs().get(itemIndex)) : "";

        warehousePresenter.setSelectedItemName(name);
        warehousePresenter.setSelectedItemDesc(desc);

    }

    public void updatePendingItems() {

        itemsGateway.populate(itemManager);

        List<String> pendingItems = new ArrayList<>();

        for (int id : itemManager.getDisapprovedIDs()) {

            pendingItems.add(itemManager.getItemNameById(id));

        }

        warehousePresenter.setPendingItems(pendingItems);

    }
}
