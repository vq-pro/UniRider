package quebec.virtualite.unirider.bluetooth.impl

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice.TRANSPORT_LE
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class DeviceConnectorImpl : DeviceConnector {

    private val GOTWAY_SERVICES: List<String> = listOf(
        "00001800-0000-1000-8000-00805f9b34fb",
        "00001801-0000-1000-8000-00805f9b34fb",
        "0000180a-0000-1000-8000-00805f9b34fb",
        "0000ffe0-0000-1000-8000-00805f9b34fb"
    )
    private val GOTWAY_READ_CHARACTER: UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb")
    private val GOTWAY_SERVICE: UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")

    private val INMOTION_DESCRIPTOR: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    private val INMOTION_READ_CHARACTER: UUID = UUID.fromString("0000ffe4-0000-1000-8000-00805f9b34fb")
    private val INMOTION_SERVICE: UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")
    private val INMOTION_SERVICES: List<String> = listOf(
        "0000180a-0000-1000-8000-00805f9b34fb",
        "0000180f-0000-1000-8000-00805f9b34fb",
        "0000ffe0-0000-1000-8000-00805f9b34fb",
        "0000ffe5-0000-1000-8000-00805f9b34fb",
        "0000fff0-0000-1000-8000-00805f9b34fb",
        "0000ffd0-0000-1000-8000-00805f9b34fb",
        "0000ffc0-0000-1000-8000-00805f9b34fb",
        "0000ffb0-0000-1000-8000-00805f9b34fb",
        "0000ffa0-0000-1000-8000-00805f9b34fb",
        "0000ff90-0000-1000-8000-00805f9b34fb",
        "0000fc60-0000-1000-8000-00805f9b34fb",
        "0000fe00-0000-1000-8000-00805f9b34fb"
    )
    private val KINGSONG_SERVICES: List<String> = listOf(
        "00001800-0000-1000-8000-00805f9b34fb",
        "00001801-0000-1000-8000-00805f9b34fb",
        "0000180a-0000-1000-8000-00805f9b34fb",
        "0000fff0-0000-1000-8000-00805f9b34fb",
        "0000ffe0-0000-1000-8000-00805f9b34fb"
    )
    private val KINGSONG_DESCRIPTOR_UUID1: UUID = UUID.fromString("00002901-0000-1000-8000-00805f9b34fb")
    private val KINGSONG_DESCRIPTOR_UUID2: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    private val KINGSONG_READ_CHARACTER: UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb")
    private val KINGSONG_SERVICE_UUID: UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")

    private val AUTOCONNECT = false

    private var mVoltage: Int = 0
    private var mTotalDistance: Long = 0

    private lateinit var activity: Activity
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothGatt: BluetoothGatt
    private lateinit var callback: (WheelData) -> Unit

    private val mGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {

                    bluetoothGatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    val b = true
                }
            }
        }

        private fun detectWheel(gatt: BluetoothGatt): Boolean {
            val services = gatt.services
            if (services.size != KINGSONG_SERVICES.size)
                return false

            services.forEach { service_entry ->
                val service = gatt.getService(service_entry.uuid)
                if (service == null) {
                    return false
                }

                val characteristicsFromService = service.characteristics

                val characteristics: List<String> = getCharacteristics("${service_entry.uuid}")
                characteristics.forEach { char_entry ->
                    val characteristic: BluetoothGattCharacteristic =
                        service.getCharacteristic(UUID.fromString(char_entry))

                    val descriptors = characteristic.descriptors

                    if (characteristic == null) {
                        return false
                    }
                }
            }

            return true
        }

        private fun getCharacteristics(serviceEntry: String): List<String> {
            return when (serviceEntry) {
                "00001800-0000-1000-8000-00805f9b34fb" -> listOf(
                    "00002a00-0000-1000-8000-00805f9b34fb",
                    "00002a01-0000-1000-8000-00805f9b34fb",
                    "00002a02-0000-1000-8000-00805f9b34fb",
                    "00002a03-0000-1000-8000-00805f9b34fb",
                    "00002a04-0000-1000-8000-00805f9b34fb"
                )
                "00001801-0000-1000-8000-00805f9b34fb" -> listOf(
                    "00002a05-0000-1000-8000-00805f9b34fb"
                )
                "0000180a-0000-1000-8000-00805f9b34fb" -> listOf(
                    "00002a23-0000-1000-8000-00805f9b34fb",
                    "00002a24-0000-1000-8000-00805f9b34fb",
                    "00002a25-0000-1000-8000-00805f9b34fb",
                    "00002a26-0000-1000-8000-00805f9b34fb",
                    "00002a27-0000-1000-8000-00805f9b34fb",
                    "00002a28-0000-1000-8000-00805f9b34fb",
                    "00002a29-0000-1000-8000-00805f9b34fb",
                    "00002a2a-0000-1000-8000-00805f9b34fb",
                    "00002a50-0000-1000-8000-00805f9b34fb"
                )
                "0000fff0-0000-1000-8000-00805f9b34fb" -> listOf(
                    "0000fff1-0000-1000-8000-00805f9b34fb",
                    "0000fff2-0000-1000-8000-00805f9b34fb",
                    "0000fff3-0000-1000-8000-00805f9b34fb",
                    "0000fff4-0000-1000-8000-00805f9b34fb",
                    "0000fff5-0000-1000-8000-00805f9b34fb"
                )
                "0000ffe0-0000-1000-8000-00805f9b34fb" -> listOf(
                    "0000ffe1-0000-1000-8000-00805f9b34fb"
                )
                else -> listOf()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)

            var isGotway = false
            var isInmotion = false
            if (status == BluetoothGatt.GATT_SUCCESS) {

                val detected = detectWheel(gatt)


//                val service1 = bluetoothGatt.getService(UUID.fromString("00001801-0000-1000-8000-00805f9b34fb"))
//                val notifyCharacteristic1 = service1.getCharacteristic(UUID.fromString("00002a05-0000-1000-8000-00805f9b34fb"))
//                val succes1 = bluetoothGatt.setCharacteristicNotification(notifyCharacteristic1, true)

//                val descriptor1 =
//                    notifyCharacteristic1.getDescriptor(KINGSONG_DESCRIPTOR_UUID2)
//                descriptor1.value = ENABLE_NOTIFICATION_VALUE
//                val succesb1 = gatt.writeDescriptor(descriptor1)

//                val service2 = bluetoothGatt.getService(UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb"))
//                val notifyCharacteristic2 = service2.getCharacteristic(UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb"))
//                val succes2 = bluetoothGatt.setCharacteristicNotification(notifyCharacteristic2, true)

//                val descriptor2 =
//                    notifyCharacteristic2.getDescriptor(KINGSONG_DESCRIPTOR_UUID2)
//                descriptor2.value = ENABLE_NOTIFICATION_VALUE
//                val succesb2 = gatt.writeDescriptor(descriptor2)

                val service = gatt.getService(KINGSONG_SERVICE_UUID)
                val notifyCharacteristic = service.getCharacteristic(KINGSONG_READ_CHARACTER)

                val succes = gatt.setCharacteristicNotification(notifyCharacteristic, true)

                val descriptor =
                    notifyCharacteristic.getDescriptor(KINGSONG_DESCRIPTOR_UUID2)
                descriptor.setValue(ENABLE_NOTIFICATION_VALUE)
                val succesb = gatt.writeDescriptor(descriptor)

//                requestKingSongNameData()


//                gatt.readCharacteristic(notifyCharacteristic)


//                val descriptor1 =
//                    notifyCharacteristic.getDescriptor(KINGSONG_DESCRIPTOR_UUID1)
//                descriptor1.value = ENABLE_INDICATION_VALUE
//                val succes1 = bluetoothGatt.writeDescriptor(descriptor1)

//                requestKingSongSerialData()
//                requestKingSongHorn()

                var b = true

//                val theServices = bluetoothGatt.services
//                val theServicesIds: List<String> = theServices.map { service -> "${service.uuid}" }
//
//                if (KINGSONG_SERVICES.equals(theServicesIds)) {
//                    val bluetoothGattService = bluetoothGatt.getService(KINGSONG_SERVICE)
//                    val notifyCharacteristic0 =
//                        bluetoothGattService.getCharacteristic(UUID.fromString(KINGSONG_SERVICES[0]))
//                    val notifyCharacteristic1 =
//                        bluetoothGattService.getCharacteristic(UUID.fromString(KINGSONG_SERVICES[1]))
//                    val notifyCharacteristic2 =
//                        bluetoothGattService.getCharacteristic(UUID.fromString(KINGSONG_SERVICES[2]))
//                    val notifyCharacteristic3 =
//                        bluetoothGattService.getCharacteristic(UUID.fromString(KINGSONG_SERVICES[3]))
//                    val notifyCharacteristic4 =
//                        bluetoothGattService.getCharacteristic(UUID.fromString(KINGSONG_SERVICES[4]))
//
//                    val notifyCharacteristic =
//                        bluetoothGattService.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"))
//                    bluetoothGatt.setCharacteristicNotification(notifyCharacteristic, true)
//
////                    val targetService: BluetoothGattService = mBluetoothLeService.getGattService(
////                        UUID.fromString(Constants.KINGSONG_SERVICE_UUID)
////                    )
////                    val notifyCharacteristic =
////                        targetService.getCharacteristic(UUID.fromString(Constants.KINGSONG_READ_CHARACTER_UUID))
////                    mBluetoothLeService.setCharacteristicNotification(notifyCharacteristic, true)
//                    val descriptor =
//                        notifyCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
//                    descriptor.value = ENABLE_NOTIFICATION_VALUE
//                    bluetoothGatt.writeDescriptor(descriptor)
//
//                    var b = true
//
//                } else if (GOTWAY_SERVICES.equals(theServicesIds)) {
//                    isGotway = true
//
//                    val bluetoothGattService = bluetoothGatt.getService(GOTWAY_SERVICE)
//                    val notifyCharacteristic = bluetoothGattService.getCharacteristic(GOTWAY_READ_CHARACTER)
//                    bluetoothGatt.setCharacteristicNotification(notifyCharacteristic, true)
//
//                    var b = true
//
//                } else if (INMOTION_SERVICES.equals(theServicesIds)) {
//                    isInmotion = true
//
//                    val bluetoothGattService = bluetoothGatt.getService(INMOTION_SERVICE)
//                    val notifyCharacteristic = bluetoothGattService.getCharacteristic(INMOTION_READ_CHARACTER)
//                    bluetoothGatt.setCharacteristicNotification(notifyCharacteristic, true)
//
//                    val descriptor = notifyCharacteristic.getDescriptor(INMOTION_DESCRIPTOR)
//                    descriptor.value = ENABLE_NOTIFICATION_VALUE
//                    bluetoothGatt.writeDescriptor(descriptor)
//
//                    var b = true
//                    b = false
//                }
            }
        }

//
//        private fun requestKingSongSerialData() {
//            val data = ByteArray(20)
//            data[0] = (-86).toByte()
//            data[1] = 85.toByte()
//            data[16] = 99.toByte()
//            data[17] = 20.toByte()
//            data[18] = 90.toByte()
//            data[19] = 90.toByte()
//            writeBluetoothGattCharacteristic(data)
//        }
//
//        private fun requestKingSongHorn() {
//            val data = ByteArray(20)
//            data[0] = (-86).toByte()
//            data[1] = 85.toByte()
//            data[16] = (-120).toByte()
//            data[17] = 20.toByte()
//            data[18] = 90.toByte()
//            data[19] = 90.toByte()
//            writeBluetoothGattCharacteristic(data)
//        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)

            val data: ByteArray = characteristic.value

            if (characteristic.uuid.equals(KINGSONG_READ_CHARACTER)) {

                val wheelData = decodeKingSong(gatt, data)
            }
            val b = true
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            super.onCharacteristicChanged(gatt, characteristic)

            val data: ByteArray = characteristic.value

            if (characteristic.uuid.equals(KINGSONG_READ_CHARACTER)) {

                val wheelData = decodeKingSong(gatt, data)
//                if (wheelData != null) {
//                    callback(wheelData)
//                }

                var b = true
                b = false
            } else if (characteristic.uuid.equals(INMOTION_READ_CHARACTER)) {
                val statuses: List<InMotionAdapter.Status> = InMotionAdapter.getInstance().charUpdated(data)

                var b = true
                b = false
            }
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            super.onDescriptorWrite(gatt, descriptor, status)
        }

        override fun onDescriptorRead(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
            super.onDescriptorRead(gatt, descriptor, status)
        }
    }

    override fun connect(deviceAddress: String, callback: (WheelData) -> Unit) {

        this.callback = callback

        val bluetoothManager = activity.getSystemService(AppCompatActivity.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        val bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress)
        if (bluetoothDevice == null) {
            throw AssertionError("Impossible to connect to device")
        }

        bluetoothGatt = bluetoothDevice.connectGatt(
            activity.baseContext,
            AUTOCONNECT,
            mGattCallback,
            TRANSPORT_LE
//            PHY_LE_1M,
//            null
        )

        bluetoothGatt.requestMtu(517)

//            .connectGatt(
//                Context context,
//                boolean autoConnect,
//                BluetoothGattCallback callback,
//                int transport,
//                boolean opportunistic,
//                int phy,
//                Handler handler
//            )
    }

    override fun init(activity: Activity) {
        this.activity = activity
    }

    fun byteArrayInt2(low: Byte, high: Byte): Int {

        return ((high.toUByte() * 0x100u) + low.toUByte()).toInt()
    }

    fun byteArrayInt4(low1: Byte, high1: Byte, low2: Byte, high2: Byte): Long {

        return (byteArrayInt2(low1, high1).toULong() * 0x10000u
                + byteArrayInt2(low2, high2).toULong()).toLong()
    }

    fun byteArrayToString(data: ByteArray): String {
        val string = StringBuilder()
        for (byte in data) {
            if (string.isNotEmpty())
                string.append("-")
            val value = byte.toUByte().toString(16).uppercase()
            string.append(if (value.length == 1) "0$value" else value)
        }

        return string.toString()
    }

    private fun requestKingSongNameData(gatt: BluetoothGatt) {
        val data = ByteArray(20)
//            data[0] = (-86).toByte()
//            data[1] = 85.toByte()
//            data[16] = (-101).toByte()
//            data[17] = 20.toByte()
//            data[18] = 90.toByte()
//            data[19] = 90.toByte()
        data[0] = 0xAA.toByte()
        data[1] = 0x55.toByte()
        data[16] = 0x9B.toByte()
        data[17] = 0x14.toByte()
        data[18] = 0x5A.toByte()
        data[19] = 0x5A.toByte()
        writeBluetoothGattCharacteristic(gatt, data)
    }

    private fun writeBluetoothGattCharacteristic(gatt: BluetoothGatt, cmd: ByteArray) {
        val service = gatt.getService(KINGSONG_SERVICE_UUID)
        val ks_characteristic = service.getCharacteristic(KINGSONG_READ_CHARACTER)
        ks_characteristic.setValue(cmd)
        ks_characteristic.setWriteType(1)
        gatt.writeCharacteristic(ks_characteristic)
    }

    private fun decodeKingSong(gatt: BluetoothGatt, data: ByteArray): Boolean {

        Log.i("*** decodeKingSong - ", byteArrayToString(data))

        if (data.size < 20) {
//            val service = gatt.getService(KINGSONG_SERVICE_UUID)
//            val notifyCharacteristic = service.getCharacteristic(KINGSONG_READ_CHARACTER)
//            gatt.readCharacteristic(notifyCharacteristic)

            requestKingSongNameData(gatt)

            return false
        }

        if (data[0] != 0xAA.toByte() || data[1] != 0x55.toByte())
            return false

        when (data[16]) {
            0xA9.toByte() -> {  // Live data
                mVoltage = byteArrayInt2(data[2], data[3])
                mTotalDistance = byteArrayInt4(data[6], data[7], data[8], data[9])
                var b = true
            }

            0xB9.toByte() -> {  // Distance, time, fan data
                var b = true
            }

            0xBB.toByte() -> {  // Name & type data
                var b = true
            }

            0xB3.toByte() -> {  // Serial Number
                var b = true
            }

            else -> {
                var b = true
            }
        }

        return true
    }
}
