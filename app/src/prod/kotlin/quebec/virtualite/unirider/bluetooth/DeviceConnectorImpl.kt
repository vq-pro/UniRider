package quebec.virtualite.unirider.bluetooth

import android.app.Activity
import android.bluetooth.*
import android.bluetooth.BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
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

    private val AUTOCONNECT = true

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

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)

            var isGotway = false
            var isInmotion = false
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val theServices = bluetoothGatt.services
                val theServicesIds: List<String> = theServices.map { service -> service.uuid.toString() }

                if (GOTWAY_SERVICES.equals(theServicesIds)) {
                    isGotway = true

                    val bluetoothGattService = bluetoothGatt.getService(GOTWAY_SERVICE)
                    val notifyCharacteristic = bluetoothGattService.getCharacteristic(GOTWAY_READ_CHARACTER)
                    bluetoothGatt.setCharacteristicNotification(notifyCharacteristic, true)

                    var b = true
                    b = false

                } else if (INMOTION_SERVICES.equals(theServicesIds)) {
                    isInmotion = true

                    val bluetoothGattService = bluetoothGatt.getService(INMOTION_SERVICE)
                    val notifyCharacteristic = bluetoothGattService.getCharacteristic(INMOTION_READ_CHARACTER)
                    bluetoothGatt.setCharacteristicNotification(notifyCharacteristic, true)

                    val descriptor = notifyCharacteristic.getDescriptor(INMOTION_DESCRIPTOR)
                    descriptor.value = ENABLE_NOTIFICATION_VALUE
                    bluetoothGatt.writeDescriptor(descriptor)

                    var b = true
                    b = false
                }
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            super.onCharacteristicChanged(gatt, characteristic)

            val data: ByteArray = characteristic.value

            if (characteristic.uuid.equals(GOTWAY_READ_CHARACTER)) {

                val wheelData = DecodeGotway.decodeGotway(data)
                if (wheelData != null) {
                    callback(wheelData)
                }

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
    }

    override fun connect(device: Device, callback: (WheelData) -> Unit) {

        this.callback = callback

        val bluetoothManager = activity.getSystemService(AppCompatActivity.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        val bluetoothDevice = bluetoothAdapter.getRemoteDevice(device.address)
        if (bluetoothDevice == null) {
            throw AssertionError("Impossible to connect to device")
        }

        bluetoothGatt = bluetoothDevice.connectGatt(activity.baseContext, AUTOCONNECT, mGattCallback)
    }

    override fun init(activity: Activity) {
        this.activity = activity
    }
}
