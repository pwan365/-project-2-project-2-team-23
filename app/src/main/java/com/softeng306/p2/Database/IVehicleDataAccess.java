package com.softeng306.p2.Database;

import com.softeng306.p2.Listeners.OnGetTagListener;

public interface IVehicleDataAccess {
    public void getAllTags(OnGetTagListener listener);
}
