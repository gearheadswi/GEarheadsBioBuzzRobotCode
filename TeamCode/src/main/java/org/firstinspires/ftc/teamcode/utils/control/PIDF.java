package org.firstinspires.ftc.teamcode.utils.control;

public class PIDF {
    public double p, i, d, f;

    private double prev = 0;
    private double integral = 0;
    private long prev_time = 0;
    private boolean started = false;

    public static final double epsilon = 1e-2;

    public PIDF(double P, double I, double D, double F){
        set_values(P, I, D, F);
    }

    // reset all running values to zero. If you are using the pid after waiting a while, this is basically a must.
    // Keeps pidf values the same
    public void reset(){
        prev = 0;
        integral = 0;
        prev_time = 0;
        started = false;
    }

    public void set_values(double P, double I, double D, double F){
        p = P;
        i = I;
        d = D;
        f = F;
    }
    public double smooth(double target_reading, double current_reading){
        double error = current_reading - target_reading;

        // System.nanoTime() may be more accurate if needed, but is less performant
        double time_step = (double)(System.currentTimeMillis() - prev_time);
        prev_time = System.currentTimeMillis();

        // if pid not yet started, it may have been a while since it was initialized
        if (!started){
            time_step = 0;
        }
        integral += error * time_step;

        // (P)
        double proportianal = error * p;

        // (I)
        double integ = i * integral;

        // can't divide by zero (D)
        double deriv = 0;
        if(time_step != 0){
            deriv = -d * (prev - current_reading) / time_step;
        }
        
        prev = current_reading;
        started = true;
        return proportianal + integ + deriv + f;
    }

    public double smoothAngle(double target_reading, double current_reading){
        double error = angle_difference(target_reading, current_reading);

        // System.nanoTime() may be more accurate if needed, but is less performant
        double time_step = (double)(System.currentTimeMillis() - prev_time);
        prev_time = System.currentTimeMillis();

        // if pid not yet started, it may have been a while since it was initialized
        if (!started){
            time_step = 0;
        }
        integral += error * time_step;

        // Reset integral when we have reached target
        if (Math.abs(error) < epsilon){
            integral = 0;
        }

        // (P)
        double proportianal = error * p;

        // (I)
        double integ = i * integral;

        // can't divide by zero (D)
        double deriv = 0;
        if(time_step != 0){
            deriv = d * angle_difference(prev, current_reading) / time_step;
        }

        prev = current_reading;
        started = true;
        return proportianal + integ + deriv + f;
    }

    private double angle_difference(double a, double b){
        double chord_length =
                Math.sqrt(
                        Math.pow(Math.cos(a) - Math.cos(b),2)
                                + Math.pow(Math.sin(a) -Math.sin(b), 2)
                );
        double angle_between = 2 * Math.asin(chord_length / 2);

        double determinate = Math.sin(b - a);
        if (determinate > 0){
            angle_between *= -1;
        }

        return angle_between;
    }
}
