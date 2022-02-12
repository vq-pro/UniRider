package quebec.virtualite.unirider.bluetooth;

import java.util.Calendar;

public class DecodeGotway {

    private static final double RATIO_GW = 0.875;
    private static final int MAX_BATTERY_AVERAGE_COUNT = 150;
    private static final int TIME_BUFFER = 10;

    private static int mSpeed;
    private static boolean mUseRatio = false;
    private static int mTopSpeed;
    private static int mTemperature;
    private static int mTemperature2;
    private static long mStartTotalDistance;
    private static long mTotalDistance;
    private static long mDistance;
    private static int mVoltage;
    private static float mActualVoltage;
    private static int mCurrent;
    private static int mBattery;
    private static double mAverageBattery;
    private static double mAverageBatteryCount;
    private static int mGotwayVoltageScaler = 0;
    private static long rideStartTime;
    private static int mLastRideTime;
    private static int mRideTime;
    private static int mRidingTime;

    private static void setCurrentTime(int currentTime) {
        if (mRideTime > (currentTime + TIME_BUFFER))
            mLastRideTime += mRideTime;
        mRideTime = currentTime;
    }

    private static void setTopSpeed(int topSpeed) {
        if (topSpeed > mTopSpeed)
            mTopSpeed = topSpeed;
    }

    private static void setDistance(long distance) {
        if (mStartTotalDistance == 0 && mTotalDistance != 0)
            mStartTotalDistance = mTotalDistance;

        mDistance = distance;
    }

    private static void setBatteryPercent(int battery) {
        mBattery = battery;

        mAverageBatteryCount = mAverageBatteryCount < MAX_BATTERY_AVERAGE_COUNT ?
                mAverageBatteryCount + 1 : MAX_BATTERY_AVERAGE_COUNT;

        mAverageBattery += (battery - mAverageBattery) / mAverageBatteryCount;
    }

    private static int byteArrayInt2(byte low, byte high) {
        return (low & 255) + ((high & 255) * 256);
    }

    public static WheelData decodeGotway(byte[] data) {
        if (rideStartTime == 0) {
            rideStartTime = Calendar.getInstance().getTimeInMillis();
            mRidingTime = 0;
        }
        if (data.length >= 20) {
            int a1 = data[0] & 255;
            int a2 = data[1] & 255;
            int a19 = data[18] & 255;
            if (a1 != 85 || a2 != 170 || a19 != 0) {
                return null;
            }

            if (data[5] >= 0)
                mSpeed = (int) Math.abs(((data[4] * 256.0) + data[5]) * 3.6);
            else
                mSpeed = (int) Math.abs((((data[4] * 256.0) + 256.0) + data[5]) * 3.6);
            if (mUseRatio) mSpeed = (int)Math.round(mSpeed * RATIO_GW);
            setTopSpeed(mSpeed);

            mTemperature = (int) Math.round(((((data[12] * 256) + data[13]) / 340.0) + 35) * 100);
            mTemperature2 = mTemperature;

            long distance = byteArrayInt2(data[9], data[8]);
            if (mUseRatio) distance = Math.round(distance * RATIO_GW);
            setDistance(distance);

            mVoltage = (data[2] * 256) + (data[3] & 255);
            mActualVoltage = mVoltage / 66.80f;

            mCurrent = Math.abs((data[10] * 256) + data[11]);

            int battery;

            float battery2 = (mVoltage - 5290f) * 100f / (6733f-5290f);

//            if (mVoltage > 6680) {
//                battery = 100;
//            } else if (mVoltage > 5440) {
//                battery = (mVoltage - 5380) / 13;
//            } else if (mVoltage > 5290){
//                battery = (int)Math.round((mVoltage - 5290) / 32.5);
//            } else {
//                battery = 0;
//            }
            if (mVoltage <= 5290) {
                battery = 0;
            } else if (mVoltage >= 6580) {
                battery = 100;
            } else {
                battery = (mVoltage - 5290) / 13;
            }
            setBatteryPercent(battery);
//			if (mGotway84V) {
//				mVoltage = (int)Math.round(mVoltage / 0.8);
//			}
            mVoltage = mVoltage + (int)Math.round(mVoltage*0.25*mGotwayVoltageScaler);
            int currentTime = (int) (Calendar.getInstance().getTimeInMillis() - rideStartTime) / 1000;
            setCurrentTime(currentTime);

            return new WheelData(mActualVoltage, battery2);

        } else if (data.length >= 10) {
            int a1 = data[0];
            int a5 = data[4] & 255;
            int a6 = data[5] & 255;
            if (a1 != 90 || a5 != 85 || a6 != 170) {
                return null;
            }
            mTotalDistance = ((data[6]&0xFF) <<24) + ((data[7]&0xFF) << 16) + ((data[8] & 0xFF) <<8) + (data[9] & 0xFF);
            if (mUseRatio) mTotalDistance = Math.round(mTotalDistance * RATIO_GW);
        }
        return null;
    }
}
