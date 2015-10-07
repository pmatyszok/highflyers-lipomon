package com.highflyers.mwitas.lipomon.model.bt.exceptions;

import com.highflyers.mwitas.lipomon.model.bt.BtManager;

/**
 * TODO - description
 *
 * @author Michał Witas
 * @version 1.0
 */
public class BtInvalidStateException extends Exception {
    public BtManager.BtState state;

    public BtInvalidStateException(BtManager.BtState state) {
        this.state = state;
    }

    @Override
    public String getMessage() {
        return state.text;
    }
}
