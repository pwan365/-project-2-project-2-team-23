package com.softeng306.p2.Database;

/**
 * This is the injector class used to inject service to CoreActivity
 */
public class VehicleService {

    private static VehicleService _vehicleService = null;
    private static VehicleDataAccess _vda;

    /**
     * private constructor to prevent duplication
     */
    private VehicleService(){
        _vda = new VehicleDataAccess();
    }

    /**
     * This is the method that is used to return the class itself for singleton structure
     * @return this static class
     */
    public static VehicleService getInstance(){
        if (_vehicleService == null){
            _vehicleService = new VehicleService();
        }
        return _vehicleService;
    }

    /**
     * Method use to inject the service into a CoreActivity
     * @param activity activity to be injected
     */
    public static void InjectService(CoreActivity activity){
        activity.setDataAccess(_vda);
    }
}
