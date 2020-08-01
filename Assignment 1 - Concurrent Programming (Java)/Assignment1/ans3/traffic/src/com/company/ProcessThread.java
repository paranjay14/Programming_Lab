package com.company;

class ProcessThread implements Runnable{
    boolean flag;
    Que qSE;
    Que qES;
    Que qSW;
    Que qWS;
    Que qWE;
    Que qEW;
    int queueNo;
    Vehicle v;
    Time time;

    // Constructor for class to initialise local variables
    ProcessThread(Time time, boolean flg, Que queSE, Que queES, Que queSW, Que queWS, Que queWE, Que queEW, int qNo, Vehicle vehicle) {
        this.qSE = queSE;
        this.qES = queES;
        this.qSW = queSW;
        this.qWS = queWS;
        this.qWE = queWE;
        this.qEW = queEW;
        this.flag = flg;
        this.queueNo = qNo;
        this.v = vehicle;
        this.time = time;
    }

    // Constructor for class to initialise local variables
    ProcessThread(Time time, boolean flg, Que queSE, Que queES, Que queSW, Que queWS, Que queWE, Que queEW, int qNo) {
        this.qSE = queSE;
        this.qES = queES;
        this.qSW = queSW;
        this.qWS = queWS;
        this.qWE = queWE;
        this.qEW = queEW;
        this.flag = flg;
        this.queueNo = qNo;
        this.time = time;
    }

    // thread runs which either adds a new vehicle to a queue, or updates all the queues when the timer clicks.
    public void run() {
        try {
            if(!flag){
                long prevTRem = 0;
                switch (queueNo){
                    case 1  :   if(qSE.vehicles.isEmpty())
                        qSE.addV(v, time, 0, 1);
                    else{
                        prevTRem = qSE.vehicles.get(qSE.vehicles.size()-1).remTime ;
                        qSE.addV(v, time, prevTRem, 1);
                    }
                        break;
                    case 2  :   if(qES.vehicles.isEmpty())
                        qES.addV(v, time, 0, 0);
                    else{
                        prevTRem = qES.vehicles.get(qES.vehicles.size()-1).remTime ;
                        qES.addV(v, time, prevTRem, 0);
                    }
                        break;
                    case 3  :   if(qSW.vehicles.isEmpty())
                        qSW.addV(v, time, 0, 0);
                    else{
                        prevTRem = qSW.vehicles.get(qSW.vehicles.size()-1).remTime ;
                        qSW.addV(v, time, prevTRem, 0);
                    }
                        break;

                    case 4  :   if(qWS.vehicles.isEmpty())
                        qWS.addV(v, time, 0, 2);
                    else{
                        prevTRem = qWS.vehicles.get(qWS.vehicles.size()-1).remTime ;
                        qWS.addV(v, time, prevTRem, 2);
                    }
                        break;
                    case 5  :   if(qWE.vehicles.isEmpty())
                        qWE.addV(v, time, 0, 0);
                    else{
                        prevTRem = qWE.vehicles.get(qWE.vehicles.size()-1).remTime ;
                        qWE.addV(v, time, prevTRem, 0);
                    }
                        break;
                    case 6  :   if(qEW.vehicles.isEmpty())
                        qEW.addV(v, time, 0, 3);
                    else{
                        prevTRem = qEW.vehicles.get(qEW.vehicles.size()-1).remTime ;
                        qEW.addV(v, time, prevTRem, 3);
                    }
                        break;
                    default	:   break;
                }
            }
            else{
                switch (queueNo){
                    case 1 : qSE.updateLastVTime(false);
                        qSE.updateRemainTime();
                        break;
                    case 2 : qES.updateLastVTime(false);
                        qES.updateRemainTime();
                        break;
                    case 3 : qSW.updateLastVTime(false);
                        qSW.updateRemainTime();
                        break;
                    case 4 : qWS.updateLastVTime(false);
                        qWS.updateRemainTime();
                        break;
                    case 5 : qWE.updateLastVTime(false);
                        qWE.updateRemainTime();
                        break;
                    case 6 : qEW.updateLastVTime(false);
                        qEW.updateRemainTime();
                        break;
                    default : break;
                }

            }
        }
        catch (final Exception e) {
            System.out.println("Exception has occurred in ProcessThread");
        }
    }
}