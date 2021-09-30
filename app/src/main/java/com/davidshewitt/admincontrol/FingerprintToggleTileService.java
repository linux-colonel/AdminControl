package com.davidshewitt.admincontrol;

import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.N)
public class FingerprintToggleTileService extends TileService {

    private AdminControls adminControls;

    public static void triggerUpdate(Context context) {
        requestListeningState(context,
                new ComponentName(context, FingerprintToggleTileService.class));
    }

    @Override
    public void onStartListening() {
        updateTile();
    }

    private void updateTile() {
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
            unlockAndRun(() -> {
                toggle();
                triggerUpdate(this);
            });
        } else {
            toggle();
            updateTile();
        }
    }

    private void toggle() {
        AdminControls adminControls = getAdminControls();
        adminControls.setFingerprintEnabled(!adminControls.isFingerprintEnabled());
    }

    private AdminControls getAdminControls() {
        if (adminControls == null) {
            adminControls = new AdminControls(this);
        }
        return adminControls;
    }

}
