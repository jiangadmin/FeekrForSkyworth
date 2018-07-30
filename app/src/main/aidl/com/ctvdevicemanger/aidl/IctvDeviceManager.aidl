package com.ctvdevicemanger.aidl;


interface IctvDeviceManager {

    String getSTBData(String dataName, String extData);

    int setSTBData(String dataName, String value, String extData);

    int executeComand(String comand,String arg0,String arg1,String extData);

}
