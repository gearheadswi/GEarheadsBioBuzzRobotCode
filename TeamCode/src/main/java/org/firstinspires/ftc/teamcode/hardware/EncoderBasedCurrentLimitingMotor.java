package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.VoltageSensor;

/**
 * Current Limiting Bare Motor.
 */
public class EncoderBasedCurrentLimitingMotor {
    // 12V / Stall Current
    private static final double MOTOR_RESISTANCE = 12 / 9.2;
    // 12V - no load current * resistance / (5800rpm -> radians/sec)
    private static final double VOLTAGE_PER_RADIANS_PER_SECOND =
            12 - 0.25 * MOTOR_RESISTANCE
                    / (5800 * 2 * Math.PI / 60);
    private static final double TICKS_PER_REV = 28;
    private static final double MAX_VOLTAGE = 13;

    private DcMotorEx motor;
    private VoltageSensor voltageSensor;

    /**
     * @param motor to use
     * @param voltageSensor of control hub
     */
    public EncoderBasedCurrentLimitingMotor(DcMotorEx motor, VoltageSensor voltageSensor) {
        this.motor = motor;
        this.voltageSensor = voltageSensor;
    }

    /**
     * Sets target current of motor. Note stall current is 9.2A, so peak power is at 4.6A.
     * @param targetCurrent in Amps
     */
    public void setCurrent(double targetCurrent){
        double radiansPerSecond = (motor.getVelocity() / TICKS_PER_REV) * 2 * Math.PI;
        double backEmfVoltage = VOLTAGE_PER_RADIANS_PER_SECOND * radiansPerSecond;

        double requiredVoltage = targetCurrent * MOTOR_RESISTANCE + backEmfVoltage;

        double batteryVoltage = voltageSensor.getVoltage();
        double targetVoltage = Math.min( MAX_VOLTAGE, Math.max( -MAX_VOLTAGE, requiredVoltage ));
        motor.setPower(targetVoltage / batteryVoltage);
    }
}
