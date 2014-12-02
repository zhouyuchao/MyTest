package com.zsuper.mytest.procotol.remote;

import java.net.SocketException;
import java.net.UnknownHostException;

import android.hardware.Sensor;

import com.coship.easybus.transport.udp.EasybusUdp;
import com.coship.easybus.util.EasyConstants;
import com.coship.easycontrol.inputcontrol.SensorCommand;
import com.coship.easycontrol.inputcontrol.entity.AccelerationSensorEntity;
import com.coship.easycontrol.inputcontrol.entity.GravitySensorEntity;
import com.coship.easycontrol.inputcontrol.entity.GyroscopeSensorEntity;
import com.coship.easycontrol.inputcontrol.entity.LinearAccelerationSensorEntity;
import com.coship.easycontrol.inputcontrol.entity.OrientationSensorEntity;
import com.coship.easycontrol.inputcontrol.entity.RotationVectorSensorEntity;

public class RemoteSensor {
    private static final String TAG = "RemoteSensor";

    /** udp客户端 */
    private EasybusUdp udpClient = null;

    private SensorCommand sensorCommand = null;

    private String mDeviceIP = null;

    public static final int SENSORS_ACCECTRTION = 1;
    public static final int SENSORS_ORIENTAION = 3;
    public static final int SENSORS_MAGNETIC_FIELD = 4;
    public static final int SENSORS_TEMPRATURE = 8;

    private static final int SENSORS_PORT = 12180;

    public RemoteSensor(String devIP) {
        udpClient = new EasybusUdp(devIP, SENSORS_PORT);
        try {
            udpClient.connect();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        mDeviceIP = devIP;
    }

    public void setRemote(String ip) {
        mDeviceIP = ip;
    }

    protected void destroy() {
        try {
            udpClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendSensorEvent(int sensorType, float sendX, float sendY, float sendZ) {
//        Log.i(TAG, "sensor type =" + sensorType);
//        Log.i(TAG, " sendX =" + sendX + " sendY =" + sendY + " sendZ =" + sendZ);
        
        switch (sensorType) {
        case Sensor.TYPE_ACCELEROMETER:
            // 加速度
            AccelerationSensorEntity accelerationEntity = new AccelerationSensorEntity(
                    sendX, sendY, sendZ);
            sensorCommand = new SensorCommand(accelerationEntity);
            break;
        case Sensor.TYPE_ORIENTATION:
            OrientationSensorEntity orientationEntity = new OrientationSensorEntity(
                    sendX, sendY, sendZ);
            sensorCommand = new SensorCommand(orientationEntity);
            // 方向
            break;
        case Sensor.TYPE_GYROSCOPE:
            // 陀螺仪
            GyroscopeSensorEntity gyroscopeEntity = new GyroscopeSensorEntity(
                    sendX, sendY, sendZ);
            sensorCommand = new SensorCommand(gyroscopeEntity);

            break;
        case Sensor.TYPE_GRAVITY:
            // 重力
            GravitySensorEntity gravityEntity = new GravitySensorEntity(sendX,
                    sendY, sendZ);
            sensorCommand = new SensorCommand(gravityEntity);
            break;
        case Sensor.TYPE_LINEAR_ACCELERATION:
            // 线性加速度
            LinearAccelerationSensorEntity linearAccelerationEntity = new LinearAccelerationSensorEntity(
                    sendX, sendY, sendZ);
            sensorCommand = new SensorCommand(linearAccelerationEntity);
            break;
        case Sensor.TYPE_ROTATION_VECTOR:
            // 旋转向量
            // 可选数值
            RotationVectorSensorEntity rotationVectorEntity = new RotationVectorSensorEntity(
                    sendX, sendY, sendZ, 0.0f);
            sensorCommand = new SensorCommand(rotationVectorEntity);
            break;
        default:
            break;
        }

        try {
            if (null == udpClient) {
                udpClient = new EasybusUdp(mDeviceIP, EasyConstants.REMOTE_PORT);
                udpClient.connect();
            }
            udpClient.send(sensorCommand, mDeviceIP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void release(){
        if(udpClient != null){
            try {
                udpClient.disconnect();
                udpClient = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
