from machine import I2C

class MPU6050:
    def __init__(self, i2c, addr=0x68):
         self.i2c = i2c
         self.addr = addr

    def who_am_i(self):
        return self.i2c.readfrom_mem(self.addr, 0x75, 1)[0]

    def read_temperature(self):
        temp_bytes = self.i2c.readfrom_mem(self.addr, 0x41, 2)
        temp_raw = (temp_bytes[0] << 8) | temp_bytes[1]
        if temp_raw > 32767:                                     
            temp_raw -= 65536
        temp_c = (temp_raw / 340.0) + 36.53
        return temp_c

    def read_accel_data(self):
        i2c.writeto_mem(0x68, 0x6B, b'\x00')
        data = self.i2c.readfrom_mem(self.addr, 0x3B, 6)
        accelx_raw = (data[0] << 8) | data[1]
        accely_raw = (data[2] << 8) | data[3]
        accelz_raw = (data[4] << 8) | data[5]

        def convert_ms2 (accel_raw):
            a_g = accel_raw / 16384 # accel raw / lsb_per_g -> 9.80665 m/s^2
            a_ms2 = a_g * 9.80665
            return a_ms2

        AcX = convert_ms2(accelx_raw)
        AcY = convert_ms2(accely_raw)
        AcZ = convert_ms2(accelz_raw)
        accel = Accel(AcX, AcY, AcZ)
        return accel

    def read_gyro_data(self):
        data = self.i2c.readfrom_mem(self.addr, 0x43, 6)
        gyrox_raw = (data[0] << 8) | data[1]
        gyroy_raw = (data[2] << 8) | data[3]
        gyroz_raw = (data[4] << 8) | data[5]

        if gyrox_raw > 32767: gyrox_raw -= 65536

        def convert_dps (gyro_raw):
            return gyro_raw / 131 # gyro_raw / lsb_per_dps -> angular velocity

        GyX = convert_dps(gyrox_raw)
        GyY = convert_dps(gyroy_raw)
        GyZ = convert_dps(gyroz_raw)
        gyro  = Gyro(GyX, GyY, GyZ)
        return gyro

       

class Accel:
    def __init__(self, x=0.0, y=0.0, z=0.0):
        self.x,self.y,self.z = x,y,z

    def __getitem__(self, key):
        if key == "x": return self.x
        if key == "y": return self.y
        if key == "z": return self.z
    
class Gyro:
    def __init__(self, x=0.0, y=0.0, z=0.0):
        self.x,self.y,self.z = x,y,z

    def __getitem__(self, key):
        if key == "x": return self.x
        if key == "y": return self.y
        if key == "z": return self.z
    
