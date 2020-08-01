package com.company;

class Vehicle{
    long vNo, remTime;
    String vSrc, vDest, vStatus;

    // Constructor for class to initialise local variables
    Vehicle(long vN, long rT, String vS, String vD){
        vNo = vN;
        remTime = rT;
        vSrc = vS;
        vDest = vD;
        vStatus = "Wait";
    }

    // Updates status of vehicle
    void updateStatus(String status){
        vStatus=status;
    }

    // returns status of vehicle
    String getStatus(){
        return vStatus;
    }

    // returns remaining time of vehicle in its queue
    long getRemTime(){
        return remTime;
    }
}

