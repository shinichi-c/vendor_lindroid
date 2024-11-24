package org.lindroid.ui;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import vendor.lindroid.perspective.IPerspective;

public class ContainerManager {
    private static final String TAG = "ContainerManager";
    private static IPerspective mPerspective;

    private ContainerManager() {} // no init

    public static boolean isPerspectiveAvailable() {
        if (mPerspective != null) {
            //if (mPerspective.isBinderAlive()) TODO
                return true;
            //mPerspective = null;
        }
        // Fetch the Perspective service
        final IBinder binder = ServiceManager.getService(Constants.PERSPECTIVE_SERVICE_NAME);
        if (binder == null) {
            Log.e(TAG, "Failed to get binder from ServiceManager");
            return false;
        }
        mPerspective = IPerspective.Stub.asInterface(binder);
        return true;
    }

    public static boolean isRunning(String containerName) {
        if (!isPerspectiveAvailable())
            return false;
        try {
            return mPerspective.isRunning(containerName);
        } catch (RemoteException e) {
            mPerspective = null;
            throw new RuntimeException(e);
        }
    }

    public static String isAtLeastOneRunning() {
        for (String id : listContainers()) {
            if (isRunning(id))
                return id;
        }
        return null;
    }

    public static boolean start(String containerName) {
        if (!isPerspectiveAvailable())
            return false;
        try {
            if (mPerspective.start(containerName)) {
                Log.d(TAG, "Container " + containerName + " started successfully.");
                return true;
            } else {
                Log.e(TAG, "Container " + containerName + " failed to start.");
                return false;
            }
        } catch (RemoteException e) {
            mPerspective = null;
            throw new RuntimeException(e);
        }
    }

    public static boolean stop(String containerName) {
        if (!isPerspectiveAvailable())
            return false;
        try {
            if (mPerspective.stop(containerName)) {
                Log.d(TAG, "Container " + containerName + " stopped successfully.");
                return true;
            } else {
                Log.e(TAG, "Container " + containerName + " failed to stop.");
                return false;
            }
        } catch (RemoteException e) {
            mPerspective = null;
            throw new RuntimeException(e);
        }
    }

    public static boolean addContainer(String containerName, ContentResolver cr, Uri rootfs) {
        if (!isPerspectiveAvailable())
            return false;
        try (ParcelFileDescriptor pfd = cr.openFileDescriptor(rootfs, "r")) {
            if (mPerspective.addContainer(containerName, pfd)) {
                Log.d(TAG, "Container " + containerName + " added successfully.");
                return true;
            } else {
                Log.e(TAG, "Container " + containerName + " failed to be added.");
                return false;
            }
        } catch (RemoteException e) {
            mPerspective = null;
            throw new RuntimeException(e);
        } catch (IOException e) {
            Log.e(TAG, "IOException in addContainer", e);
            return false;
        }
    }

    public static boolean deleteContainer(String containerName) {
        if (!isPerspectiveAvailable())
            return false;
        try {
            if (mPerspective.deleteContainer(containerName)) {
                Log.d(TAG, "Container " + containerName + " deleted successfully.");
                return true;
            } else {
                Log.e(TAG, "Container " + containerName + " failed to be deleted.");
                return false;
            }
        } catch (RemoteException e) {
            mPerspective = null;
            throw new RuntimeException(e);
        }
    }

    public static List<String> listContainers() {
        if (!isPerspectiveAvailable())
            return new ArrayList<>();
        try {
            return mPerspective.listContainers();
        } catch (RemoteException e) {
            mPerspective = null;
            throw new RuntimeException(e);
        }
    }
}
