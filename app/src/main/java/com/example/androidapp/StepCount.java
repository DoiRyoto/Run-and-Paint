package com.example.androidapp;

public class StepCount {
    float[] filter_data = new float[3];
    float[] axis_result = new float[3];
    float[] axis_new = new float[3];
    float[] axis_old = new float[3];

    float sensitivity = 2.0f;

    int THRESHOLD = 50;
    int sampleCount = 0;
    int filterCount = 0;

    float acXmin, acYmin, acZmin = 100f;
    float acXmax, acYmax, acZmax = -100f;
    float dcX, dcY, dcZ;

    float thresholdLevel = 0;

    public boolean check_step(float[] ac) {
        if (filterCount < 3) {
            filterCount++;
            filter_data[0] += ac[0];
            filter_data[1] += ac[1];
            filter_data[2] += ac[2];
            return false;
        } else {
            filterCount = 0;
            axis_result[0] = filter_data[0] / 4f;
            axis_result[1] = filter_data[1] / 4f;
            axis_result[2] = filter_data[2] / 4f;

            filter_data[0] = 0f;
            filter_data[1] = 0f;
            filter_data[2] = 0f;
        }

        acXmin = Math.min(axis_result[0], acXmin);
        acYmin = Math.min(axis_result[1], acYmin);
        acZmin = Math.min(axis_result[2], acZmin);
        acXmax = Math.max(axis_result[0], acXmax);
        acYmax = Math.max(axis_result[1], acYmax);
        acZmax = Math.max(axis_result[2], acZmax);

        sampleCount++;

        if (sampleCount > THRESHOLD) {
            sampleCount = 0;
            dcX = (acXmax - acXmin) / 2f;
            dcY = (acYmax - acYmin) / 2f;
            dcZ = (acZmax - acZmin) / 2f;

            acXmax = acYmax = acZmax = -100f;
            acXmin = acYmin = acZmin = 100f;
        }

        float resultVector = (float) Math.sqrt(axis_result[0] * axis_result[0] + axis_result[1] * axis_result[1] + axis_result[2] * axis_result[2]);

        if (resultVector > sensitivity) {
            axis_old[0] = axis_new[0];
            axis_old[1] = axis_new[1];
            axis_old[2] = axis_new[2];
            axis_new[0] = axis_result[0];
            axis_new[1] = axis_result[1];
            axis_new[2] = axis_result[2];
        } else {
            axis_old[0] = axis_new[0];
            axis_old[1] = axis_new[1];
            axis_old[2] = axis_new[2];
            return false;
        }

        float abs_x_change = Math.abs(axis_result[0]);
        float abs_y_change = Math.abs(axis_result[1]);
        float abs_z_change = Math.abs(axis_result[2]);

        if (abs_x_change > abs_y_change && abs_x_change > abs_z_change) {
            thresholdLevel = dcX;
            return axis_old[0] > thresholdLevel && thresholdLevel > axis_new[0];
        } else if (abs_y_change > abs_x_change && abs_y_change > abs_z_change) {
            thresholdLevel = dcY;
            return axis_old[1] > thresholdLevel && thresholdLevel > axis_new[1];
        } else if (abs_z_change > abs_x_change && abs_z_change > abs_y_change) {
            thresholdLevel = dcZ;
            return axis_old[2] > thresholdLevel && thresholdLevel > axis_new[2];
        }

        return false;
    }
}