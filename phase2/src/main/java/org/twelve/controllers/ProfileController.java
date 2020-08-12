package org.twelve.controllers;

import org.twelve.entities.Roles;
import org.twelve.gateways.CitiesGateway;
import org.twelve.gateways.GatewayPool;
import org.twelve.presenters.ProfilePresenter;
import org.twelve.usecases.*;

/**
 * Controller for managing account settings.
 */
public class ProfileController {

    private final StatusManager statusManager;
    private ProfilePresenter profilePresenter;
    private final SessionManager sessionManager;
    private final LoginManager loginManager;
    private final CityManager cityManager;
    private final TradeManager tradeManager;
    private final InputHandler inputHandler;
    private final CitiesGateway citiesGateway;

    /**
     * Constructor of controller for managing account settings.
     * @param useCasePool An instance of {@link org.twelve.usecases.UseCasePool}.
     * @param gatewayPool An instance of {@link org.twelve.gateways.GatewayPool}.
     */
    public ProfileController(UseCasePool useCasePool, GatewayPool gatewayPool) {

        statusManager = useCasePool.getStatusManager();
        sessionManager = useCasePool.getSessionManager();
        loginManager = useCasePool.getLoginManager();
        tradeManager = useCasePool.getTradeManager();
        cityManager = useCasePool.getCityManager();
        inputHandler = new InputHandler();
        citiesGateway = gatewayPool.getCitiesGateway();

    }

    /**
     * Updates profile info.
     */
    public void updateProfile() {

        profilePresenter.setVacationStatus(statusManager.getRoleOfAccount(sessionManager.getCurrAccountID()) == Roles.VACATION);
        profilePresenter.setCanVacation(statusManager.canVacation(sessionManager.getCurrAccountID()));
        profilePresenter.setCanBecomeTrusted(tradeManager.canBeTrusted(sessionManager.getCurrAccountID()));
        profilePresenter.setCanRequestUnfreeze(!statusManager.isPending(sessionManager.getCurrAccountID())
                && statusManager.getRoleOfAccount(sessionManager.getCurrAccountID()) == Roles.FROZEN);

        citiesGateway.populate(cityManager);
        profilePresenter.setExistingCities(cityManager.getAllCities());
        profilePresenter.setCurrentLocation(cityManager.getLocationOfAccount(sessionManager.getCurrAccountID()));

    }

    /**
     * Provides the profile controller with an appropriate presenter.
     * @param profilePresenter An instance of a class that implements {@link org.twelve.presenters.ProfilePresenter}.
     */
    public void setProfilePresenter(ProfilePresenter profilePresenter) {

        this.profilePresenter = profilePresenter;

    }

    /**
     * Changes password of currently logged in account if oldPassword is correct and newPassword is valid.
     * @param oldPassword Old password.
     * @param newPassword New password.
     * @return Whether the password was successfully changed.
     */
    public boolean changePassword(String oldPassword, String newPassword) {

        if (newPassword.isBlank()) {
            profilePresenter.setPasswordError("newPwdError");
            return false;
        } else if (!loginManager.changePassword(sessionManager.getCurrAccountID(), oldPassword, newPassword)) {
            profilePresenter.setPasswordError("oldPwdError");
            return false;
        } else {
            profilePresenter.setPasswordError("");
            return true;
        }

    }

    /**
     * Changes location of currently logged in account if newLocation is valid.
     * @param newLocation New location.
     * @return Whether the location was successfully changed.
     */
    public boolean changeLocation(String newLocation) {

        citiesGateway.populate(cityManager);
        if (!cityManager.getAllCities().contains(newLocation)) {
            if (inputHandler.isValidLocation(newLocation)) {
                cityManager.createCity(newLocation);
            } else {
                profilePresenter.setLocationError("badLocation");
                return false;
            }
        }

        cityManager.changeAccountLocation(sessionManager.getCurrAccountID(), newLocation);
        return true;

    }

    /**
     * Changes whether currently logged in account is on vacation or not.
     * @param vacationStatus New vacation status.
     */
    public void changeVacationStatus(boolean vacationStatus) {

        if (vacationStatus) {
            statusManager.requestVacation(sessionManager.getCurrAccountID());
        } else {
            statusManager.completeVacation(sessionManager.getCurrAccountID());
        }

    }

    /**
     * Requests unfreeze for currently logged in account.
     */
    public void requestUnfreeze() {

        statusManager.requestUnfreeze(sessionManager.getCurrAccountID());

    }

    /**
     * Trusts currently logged in account.
     */
    public void becomeTrusted() {
        statusManager.trustAccount(sessionManager.getCurrAccountID());
    }
}
