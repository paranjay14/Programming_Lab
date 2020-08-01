package com.company;

import javax.swing.*;
import java.util.*;
import javax.swing.table.*;

public class gui {
    public JFrame jFrame;
    public JTable lightTable;
    public JTable vehicleTable;
    public JButton addButton;
    public JTextField sourceField;
    public JTextField destinationField;
    public JPanel panel;
    public JLabel curTimeLabel;
    public JLabel sourceLabel;
    public JLabel destinationLabel;
    private JScrollPane lightScrollPane;
    private JScrollPane vehicleScrollPane;
    public Time time;
    private long vCounter;
    private Que qSE;
    private Que qES;
    private Que qSW;
    private Que qWS;
    private Que qWE;
    private Que qEW;
    private ArrayList<Vehicle> vehicleData;

    synchronized public ArrayList<Vehicle> getVehicleData(){
        return vehicleData;
    }

    synchronized public void setVehicleData(ArrayList<Vehicle> newData){
        this.vehicleData = new ArrayList<Vehicle> (newData);
    }

    // Initialises all the gui components.
    public void initialise(){
        jFrame.setSize(1000, 1000);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
        addLabel(sourceLabel, "Source Direction", false, 0);
        addDirection(sourceField);
        addLabel(destinationLabel, "Destination Direction", false, 0);
        addDirection(destinationField);
        addVehicle(this);

        Object data[][];
        data = new Object [][] {
                {"T1", "Green", 0},
                {"T2", "Red", 0},
                {"T3", "Red", 0}
        };
        Object column[];
        column = new Object [] {"Traffic Light", "Status", "Time"};
        String tableName = "Light Table";

        addTable(lightTable, data, column, tableName);

        data = new Object [][] {
                {16, "W", "S", "T1", 0},
        };
        column = new Object [] {"Vehicle", "Source", "Destination", "Status", "RemainingTime"};
        addTable(vehicleTable, data, column, tableName);

        panel.add(lightTable);

        lightTable.setFillsViewportHeight(true);
        panel.add(lightScrollPane);

        vehicleTable.setFillsViewportHeight(true);
        panel.add(vehicleTable);

        jFrame.setVisible(true);
        jFrame.add(panel);
    }

    // Sets the time in a local copy of timer
    public void setTime(Time time) {
        long t = time.cur_time;
        addLabel(curTimeLabel, "", true, t);
    }

    // Adds TextField component in frame that stores the source or destination dierction of vehicle
    public void addDirection(JTextField jTextField) {
        jFrame.add(jTextField);
        panel.add(jTextField);
        jTextField.setVisible(true);
    }

    // Takes input from the source and direction textFields on clicking 'Add Vehicle' button and adds the incoming vehicle to the respective queue.
    public void addVehicle(gui tf) {
         addButton.addActionListener(new java.awt.event.ActionListener() {
             public void actionPerformed(java.awt.event.ActionEvent evt) {
                String src = sourceField.getText();
                String dest = destinationField.getText();
                String dir = src + dest;

                 vCounter++;
                 Vehicle v = new Vehicle(vCounter, 0, src, dest);
                 ProcessThread pThread;

                 if(src.equals("S") && dest.equals("E")){
                     pThread = new ProcessThread(time, false, qSE, qES, qSW, qWS, qWE, qEW, 1, v);
                 }
                 else if(src.equals("E") && dest.equals("S")){
                     pThread = new ProcessThread(time, false, qSE, qES, qSW, qWS, qWE, qEW, 2, v);
                 }
                 else if(src.equals("S") && dest.equals("W")){
                     pThread = new ProcessThread(time, false, qSE, qES, qSW, qWS, qWE, qEW, 3, v);
                 }
                 else if(src.equals("W") && dest.equals("S")){
                     pThread = new ProcessThread(time, false, qSE, qES, qSW, qWS, qWE, qEW, 4, v);
                 }
                 else if(src.equals("W") && dest.equals("E")){
                     pThread = new ProcessThread(time, false, qSE, qES, qSW, qWS, qWE, qEW, 5, v);
                 }
                 else{
                     pThread = new ProcessThread(time, false, qSE, qES, qSW, qWS, qWE, qEW, 6, v);
                 }

                 Thread t = new Thread(pThread, "Thread_Add_Vehicle");

                 t.start();
                 try{
                     t.join();
                 }
                 catch(Exception e){System.out.println("Exception has occurred in addVehicle in GUI");}
                 String status = v.getStatus();
                 long remTime = v.getRemTime();

                 ArrayList<Vehicle> vData = new ArrayList<>();
                 vData = getVehicleData();
                 vData.add(v);
                 setVehicleData(vData);
                 updateVehicleTable(1);
             }
         });
        jFrame.add(addButton);
        panel.add(addButton);
        addButton.setVisible(true);
    }

    // Updates the Vehicle Table every second, and on adding a new vehicle to any queue.
    synchronized public void updateVehicleTable(int r){
        ArrayList<Vehicle> vData = getVehicleData();
        DefaultTableModel model = (DefaultTableModel) vehicleTable.getModel();
        model.setRowCount(0);
        ArrayList<Vehicle> newData = new ArrayList<>();
        Vehicle car;
        boolean b = true;
        int i;

        for (Vehicle v: vData){
            b = true;
            car = v;
            String dir = v.vSrc + v.vDest;
            switch (dir){
                case "SE":
                    i = -1;
                    for (Vehicle v1: qSE.vehicles)
                        if (v1.vNo == v.vNo){
                            car = v1;
                            i = 1;
                            model.addRow(new Object[]{car.vNo, car.vSrc, car.vDest, car.vStatus, car.remTime});
                        }
                    if (i == -1){
                        if (qSE.lastCarNo == car.vNo) {
                            model.addRow(new Object[]{car.vNo, car.vSrc, car.vDest, car.vStatus, -1});
                        }
                        else
                            b = false;
                    }
                    break;
                case "ES":
                    i = -1;
                    for (Vehicle v1: qES.vehicles)
                        if (v1.vNo == v.vNo){
                            car = v1;
                            i = 1;
                            model.addRow(new Object[]{car.vNo, car.vSrc, car.vDest, car.vStatus, car.remTime});
                        }
                    if (i == -1){
                        if (qES.lastCarNo == car.vNo) {
                            model.addRow(new Object[]{car.vNo, car.vSrc, car.vDest, car.vStatus, -1});
                        }
                        else
                            b = false;
                    }
                    break;
                case "SW":
                    i = -1;
                    for (Vehicle v1: qSW.vehicles)
                        if (v1.vNo == v.vNo){
                            car = v1;
                            i = 1;
                            model.addRow(new Object[]{car.vNo, car.vSrc, car.vDest, car.vStatus, car.remTime});
                        }
                    if (i == -1){
                        if (qSW.lastCarNo == car.vNo) {
                            model.addRow(new Object[]{car.vNo, car.vSrc, car.vDest, car.vStatus, -1});
                        }
                        else
                            b = false;
                    }
                    break;
                case "WS":
                    i = -1;
                    for (Vehicle v1: qWS.vehicles)
                        if (v1.vNo == v.vNo){
                            car = v1;
                            i = 1;
                            model.addRow(new Object[]{car.vNo, car.vSrc, car.vDest, car.vStatus, car.remTime});
                        }
                    if (i == -1){
                        if (qWS.lastCarNo == car.vNo) {
                            model.addRow(new Object[]{car.vNo, car.vSrc, car.vDest, car.vStatus, -1});
                        }
                        else
                            b = false;
                    }
                    break;
                case "WE":
                    i = -1;
                    for (Vehicle v1: qWE.vehicles)
                        if (v1.vNo == v.vNo){
                            car = v1;
                            i = 1;
                            model.addRow(new Object[]{car.vNo, car.vSrc, car.vDest, car.vStatus, car.remTime});
                        }
                    if (i == -1){
                        if (qWE.lastCarNo == car.vNo) {
                            model.addRow(new Object[]{car.vNo, car.vSrc, car.vDest, car.vStatus, -1});
                        }
                        else
                            b = false;
                    }
                    break;
                case "EW":
                    i = -1;
                    for (Vehicle v1: qEW.vehicles)
                        if (v1.vNo == v.vNo){
                            car = v1;
                            i = 1;
                            model.addRow(new Object[]{car.vNo, car.vSrc, car.vDest, car.vStatus, car.remTime});
                        }
                    if (i == -1){
                        if (qEW.lastCarNo == car.vNo) {
                            model.addRow(new Object[]{car.vNo, car.vSrc, car.vDest, car.vStatus, -1});
                        }
                        else
                            b = false;
                    }
                    break;
                default:
                    b = false;
            }
            if (b)
                newData.add(car);
        }
        setVehicleData(newData);
    }

    // Add label component to frame and set its value that might include timer
    public void addLabel(JLabel jLabel, String s, boolean b, long time) {
        if (b){
            String l;
            l = String.valueOf(time+1);
            l = "Time: " + l;
            jLabel.setText(l);
        }
        jFrame.add(jLabel);
        panel.add(jLabel);
        jFrame.add(panel);
        jLabel.setVisible(true);
    }

    // sets data and columnin table and adds the table component to frame
    public void addTable (JTable jTable, Object data[][], Object column[], String name) {
        jTable.setModel(new javax.swing.table.DefaultTableModel(data, column));
        jTable.setName(name);
        jTable.setVisible(true);

        panel.add(jTable);
        jFrame.add(jTable);
    }

    // Updates the light table according to the timer such that the timer for the traffic light is on which is Green, and other two Red lights have respective timers switched off.
    public void updateLightTable(Time time, long t){
        lightTable.setValueAt(0, 0, 2);
        lightTable.setValueAt(0, 1, 2);
        lightTable.setValueAt(0, 2, 2);
        lightTable.setValueAt("Red", 0, 1);
        lightTable.setValueAt("Red", 1, 1);
        lightTable.setValueAt("Red", 2, 1);
        if (time.cur_time < t) {
            lightTable.setValueAt(time.cur_time+1, 0, 2);
            lightTable.setValueAt("Green", 0, 1);
        }
        else if (time.cur_time < 2*t){
            lightTable.setValueAt(time.cur_time-t+1, 1, 2);
            lightTable.setValueAt("Green", 1, 1);
        }
        else if (time.cur_time < 3*t){
            lightTable.setValueAt(time.cur_time-(2*t)+1, 2, 2);
            lightTable.setValueAt("Green", 2, 1);
        }
    }

    // Constructor for class which dynamically allocates space to its variables.
    public gui(Que queSE, Que queES, Que queSW, Que queWS, Que queWE, Que queEW) {
        vCounter=0;
        qSE = queSE;
        qES = queES;
        qSW = queSW;
        qWS = queWS;
        qWE = queWE;
        qEW = queEW;
        panel = new JPanel();
        lightScrollPane = new JScrollPane(lightTable);
        vehicleScrollPane = new JScrollPane(vehicleTable);
        jFrame = new JFrame("Traffic Light System");

        vehicleData = new ArrayList<Vehicle>();
    }
}
