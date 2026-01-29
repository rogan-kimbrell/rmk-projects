from machine import Pin, I2C
import bmp280
import MPU6050
from time import sleep

bmpbus = I2C(0, scl=Pin(1), sda=Pin(0), freq = 40000)
mpubus = I2C(0, scl=Pin(1), sda=Pin(0), freq = 40000)
bmp = bmp280.BMP280(bmpbus, addr=0x77)
mpu = MPU6050.MPU6050(mpubus)

cols = ["Temp", "Pres", "Alt", "AcX", "AcY", "AcZ", "GyX", "GyY", "GyZ"]

def altitude(pressure) -> float:
    h = 44330 * (1 - ((pressure)/(101325))**(1/5.255))
    return h

    
with open('data_logs.txt', 'a') as f:
    if f.tell() == 0: f.write(",".join(cols) + "\n")
    while True:
        temp = mpu.read_temperature()
        pres = bmp.pressure
        alt = altitude(pres)
        accel = mpu.read_accel_data()
        gyro  = mpu.read_gyro_data()

        aX = accel["x"]
        aY = accel["y"]
        aZ = accel["z"]

        gX = gyro["x"]
        gY = gyro["y"]
        gZ = gyro["z"]
        

        row = f"{temp},{pres},{alt},{aX},{aY},{aZ},{gX},{gY},{gZ}\n"
        f.write(row)
        f.flush();
        sleep(.100)
