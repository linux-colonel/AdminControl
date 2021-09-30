package com.davidshewitt.admincontrol;

import android.os.Build;
import android.service.quicksettings.TileService;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.N)
public class LockNowTileService extends TileService {

    @Override
    public void onClick() {
        new AdminControls(this).lockRequiringStrongAuth();
    }

}
