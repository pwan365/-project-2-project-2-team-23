package com.softeng306.p2.Database;

public class VehicleService {

    private static VehicleService _vehicleService = null;
    private static VehicleDataAccess _vda;

    private VehicleService(){
        _vda = new VehicleDataAccess();
    }

    public static VehicleService getInstance(){
        if (_vehicleService == null){
            _vehicleService = new VehicleService();
        }
        return _vehicleService;
    }

    public static void InjectService(CoreActivity activity){
        activity.SetDataAccess(_vda);
    }
}
