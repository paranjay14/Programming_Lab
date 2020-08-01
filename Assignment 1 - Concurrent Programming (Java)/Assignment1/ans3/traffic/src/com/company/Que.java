package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

class Que{
    Time time;
    String qSrc, qDest;
    Semaphore sem;
    List<Vehicle> vehicles ;
    long lastVTime;
    Vehicle v;
    long lastCarNo;

    // Constructor for class to initialise local variables
    Que(Time t, String src, String dest){
        sem = new Semaphore(1);
        time = t;
        qSrc = src;
        qDest = dest;
        vehicles = new ArrayList<Vehicle>() ;
        lastVTime = 0;
        lastCarNo = -1;
    }

    // Removes first vehicle from queue and updates its status as 'Pass'
    Vehicle removeV(){
        synchronized (this) {
        try {
                updateLastVTime(true);

                Vehicle v = vehicles.remove(0);
                lastCarNo = v.vNo;
                v.updateStatus("Pass");
            }
        catch(Exception e){
                System.out.println("Exception has occurred in removeV");
            }
        }
        return v;
    }

    // Adds incoming vehicle to queue and updates its remaining time in queue and its status as 'Pass' or 'Wait'
    void addV(Vehicle v, Time time, long prevRemTime, long vLightNo){
        synchronized (this) {
        try {
                long addTime = 0;
                long curTotal = 54;
                long cycle = 180;
                long incr = 0;
                long comp = 0;
                long cur_time = time.cur_time;
                long tLightNo;
                if (time.cur_time < 60)
                    tLightNo = 1;
                else if (time.cur_time < 120){
                    tLightNo = 2;
                    cur_time -= 60;
                }
                else{
                    tLightNo = 3;
                    cur_time -= 120;
                }

                if (vLightNo == 0) {
                    if (vehicles.isEmpty())
                        v.remTime = lastVTime;
                    else {
                        v.remTime = prevRemTime + 6;
                    }
                } else {
                    long lightDiff = vLightNo - tLightNo;
                    if (lightDiff < 0)
                        lightDiff += 3;
                    switch ((int) ((lightDiff) % 3)) {
                        case 0:
                            addTime = 0;
                            break;
                        case 1:
                            addTime = 60 - cur_time;
                            cur_time = 0;
                            break;
                        case 2:
                            addTime = 120 - cur_time;
                            cur_time = 0;
                            break;
                    }
                    if (vehicles.isEmpty()) {
                        if (curTotal - cur_time - lastVTime >= 0)
                            v.remTime = lastVTime + addTime;
                        else {
                            v.remTime = cycle - cur_time + addTime;
                        }
                    } else {
                        if (curTotal - 6 - cur_time - prevRemTime + addTime >= 0)
                            v.remTime = prevRemTime + 6 ;
                        else {
                            while (true) {
                                comp = prevRemTime - cycle - incr + cur_time;
                                if (curTotal - 6 - comp >= 0) {
                                    if (comp < 0)
                                        v.remTime = cycle + incr - cur_time + addTime;
                                    else
                                        v.remTime = prevRemTime + 6;
                                    break;
                                }
                                incr += 120;
                            }
                        }
                    }
                }

                if (v.remTime == 0)
                    v.updateStatus("Pass");
                else
                    v.updateStatus("Wait");

                vehicles.add(v);
            }
        catch(Exception e){
                System.out.println("Exception has occurred in addV");
            }
        }
    }

    // updates lastVTime such that the vehicles in the queue wait lastVTime more seconds for the last vehicle that left to pass completely.
    void updateLastVTime(boolean b){
            synchronized(this){
            try {

                if (!b) {
                    lastVTime--;
                    if (lastVTime < 0) {
                        lastVTime = 0;
                        lastCarNo = -1;
                    }
                } else lastVTime = 6;
            } catch (Exception e) {
                System.out.println("Exception has occurred in updateLastVTime");
            }
        }
    }

    // updates remaining time in queue for each vehicle and removes a vehicle if its remaining time reaches 0
    void updateRemainTime(){
        synchronized (this) {
        try {
                for (Vehicle v : vehicles)
                    v.remTime--;
                if ((vehicles.size() > 0) && (vehicles.get(0).remTime <= 0))
                    removeV();
            }
        catch(Exception e){
                System.out.println("Exception has occurred in updateRemainTime");
            }
        }
    }
}
