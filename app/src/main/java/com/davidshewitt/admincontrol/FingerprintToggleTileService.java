package com.davidshewitt.admincontrol;

import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.N)
public class FingerprintToggleTileService extends TileService {

    private AdminControls adminControls;

    private boolean listening;

    @Override
    public void onStartListening() {
        listening = true;

        updateTile();
    }

    @Override
    public void onStopListening() {
        listening = false;
    }

    private void updateTile() {
        if (!listening) return;

        AdminControls adminControls = getAdminControls();

        boolean enabled = adminControls.isFingerprintEnabled();

        Tile tile = getQsTile();
        tile.setLabel(getString(enabled
                ? R.string.tile_fingerprint_enabled_label
                : R.string.tile_fingerprint_disabled_label));
        tile.setState(!adminControls.hasDeviceAdmin() ? Tile.STATE_UNAVAILABLE
                : enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);

        tile.updateTile();
    }

    @Override
    public void onClick() {
        if (isLocked()) {
            unlockAndRun(this::toggle);
        } else {
            toggle();
        }
    }

    private void toggle() {
        AdminControls adminControls = getAdminControls();
        adminControls.setFingerprintEnabled(!adminControls.isFingerprintEnabled());

        updateTile();
    }

    private AdminControls getAdminControls() {
        if (adminControls == null) {
            adminControls = new AdminControls(this);
        }
        return adminControls;
    }

}
