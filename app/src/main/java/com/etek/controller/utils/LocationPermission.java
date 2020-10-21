package com.etek.controller.utils;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


public class LocationPermission {

    private LocationPermission() {
        // Utility class
    }

    private static final int REQUEST_PERMISSION_COARSE_LOCATION = 9358;

    public static boolean isLocationPermissionGranted(final Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true; // It is not needed at all as there were no runtime permissions yet
        return ContextCompat.checkSelfPermission(context, permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestLocationPermission(final Activity activity) {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSION_COARSE_LOCATION
        );
    }

    public static boolean isRequestLocationPermissionGranted(final int requestCode, final String[] permissions,
                                                             final int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_COARSE_LOCATION) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(permission.ACCESS_COARSE_LOCATION)
                        && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    return true;
                }
            }
        }
        return false;
    }
}
