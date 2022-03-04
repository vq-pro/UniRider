package quebec.virtualite.unirider.bluetooth.impl;

// FIXME-1 Cleanup
public class Tester {

    static int intFromBytes(byte[] bytes, int starting) {
        if (bytes.length >= starting + 4) {
            return (((((((bytes[starting + 3] & 255)) << 8) | (bytes[starting + 2] & 255)) << 8) | (bytes[starting + 1] & 255)) << 8) | (bytes[starting] & 255);
        }
        return 0;
    }
}
