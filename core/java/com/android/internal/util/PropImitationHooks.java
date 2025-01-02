/*
 * Copyright (C) 2022 Paranoid Android
 *           (C) 2023 ArrowOS
 *           (C) 2023 The LibreMobileOS Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.internal.util;

import android.app.ActivityTaskManager;
import android.app.Application;
import android.app.TaskStackListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Binder;
import android.os.Process;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;

import com.android.internal.R;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @hide
 */
public class PropImitationHooks {

    private static final String TAG = "PropImitationHooks";
    private static final String PROP_HOOKS = "persist.sys.pihooks_";
    private static final boolean DEBUG = SystemProperties.getBoolean(PROP_HOOKS + "DEBUG", false);

    private static final String SPOOF_PIXEL_GMS = "persist.sys.pixelprops.gms";

    private static final String PACKAGE_AIWALLPAPERS = "com.google.android.apps.aiwallpapers";
    private static final String PACKAGE_ARCORE = "com.google.ar.core";
    private static final String PACKAGE_ASI = "com.google.android.as";
    private static final String PACKAGE_ASSISTANT = "com.google.android.apps.googleassistant";
    private static final String PACKAGE_EMOJIWALLPAPER = "com.google.android.apps.emojiwallpaper";

    private static final String PACKAGE_FINSKY = "com.android.vending";
    private static final String PACKAGE_GMS = "com.google.android.gms";
    private static final String PACKAGE_GPHOTOS = "com.google.android.apps.photos";
    private static final String PACKAGE_NETFLIX = "com.netflix.mediaclient";

    private static final String PACKAGE_NEXUSLAUNCHER = "com.google.android.apps.nexuslauncher";
    private static final String PACKAGE_PIXELTHEMES = "com.google.android.apps.customization.pixel";
    private static final String PACKAGE_PIXELWALLPAPER = "com.google.android.apps.wallpaper.pixel";
    private static final String PACKAGE_LIVEWALLPAPER = "com.google.pixel.livewallpaper";
    private static final String PACKAGE_SUBSCRIPTION_RED = "com.google.android.apps.subscriptions.red";
    private static final String PACKAGE_VELVET = "com.google.android.googlequicksearchbox";
    private static final String PACKAGE_WALLPAPER = "com.google.android.apps.wallpaper";
    private static final String PACKAGE_WALLPAPEREFFECTS = "com.google.android.wallpaper.effects";

    private static final String PROCESS_GMS_GAPPS = PACKAGE_GMS + ".gapps";
    private static final String PROCESS_GMS_GSERVICE = PACKAGE_GMS + ".gservice";
    private static final String PROCESS_GMS_LEARNING = PACKAGE_GMS + ".learning";
    private static final String PROCESS_GMS_PERSISTENT = PACKAGE_GMS + ".persistent";
    private static final String PROCESS_GMS_SEARCH = PACKAGE_GMS + ".search";
    private static final String PROCESS_GMS_UNSTABLE = PACKAGE_GMS + ".unstable";
    private static final String PROCESS_GMS_UPDATE = PACKAGE_GMS + ".update";

    private static final ComponentName GMS_ADD_ACCOUNT_ACTIVITY = ComponentName.unflattenFromString(
            "com.google.android.gms/.auth.uiflows.minutemaid.MinuteMaidActivity");

    private static final Map<String, String> sPixelEightProps = Map.of(
            "PRODUCT", "husky",
            "DEVICE", "husky",
            "HARDWARE", "husky",
            "MANUFACTURER", "Google",
            "BRAND", "google",
            "MODEL", "Pixel 8 Pro",
            "ID", "AP2A.240705.005",
            "FINGERPRINT", "google/husky/husky:14/AP2A.240705.005/11942872:user/release-keys"
    );

    private static final Map<String, String> sPixelFiveProps = Map.of(
            "PRODUCT", "barbet",
            "DEVICE", "barbet",
            "HARDWARE", "barbet",
            "MANUFACTURER", "Google",
            "BRAND", "google",
            "MODEL", "Pixel 5a",
            "ID", "AP2A.240705.004",
            "FINGERPRINT", "google/barbet/barbet:14/AP2A.240705.004/11875680:user/release-keys"
    );

    private static final Map<String, String> sPixelTabletProps = Map.of(
            "PRODUCT", "tangorpro",
            "DEVICE", "tangorpro",
            "HARDWARE", "tangorpro",
            "MANUFACTURER", "Google",
            "BRAND", "google",
            "MODEL", "Pixel Tablet",
            "ID", "AP2A.240705.005",
            "FINGERPRINT", "google/tangorpro/tangorpro:14/AP2A.240705.005/11942872:user/release-keys"
    );

    private static final Map<String, String> sPixelXLProps = Map.of(
            "PRODUCT", "marlin",
            "DEVICE", "marlin",
            "HARDWARE", "marlin",
            "MANUFACTURER", "Google",
            "BRAND", "google",
            "MODEL", "Pixel XL",
            "ID", "QP1A.191005.007.A3",
            "FINGERPRINT", "google/marlin/marlin:10/QP1A.191005.007.A3/5972272:user/release-keys"
    );

    private static final Set<String> sNexusFeatures = Set.of(
            "NEXUS_PRELOAD",
            "nexus_preload",
            "GOOGLE_BUILD",
            "GOOGLE_EXPERIENCE",
            "PIXEL_EXPERIENCE"
    );

    private static final Set<String> sPixelFeatures = Set.of(
            "PIXEL_2017_EXPERIENCE",
            "PIXEL_2017_PRELOAD",
            "PIXEL_2018_EXPERIENCE",
            "PIXEL_2018_PRELOAD",
            "PIXEL_2019_EXPERIENCE",
            "PIXEL_2019_MIDYEAR_EXPERIENCE",
            "PIXEL_2019_MIDYEAR_PRELOAD",
            "PIXEL_2019_PRELOAD",
            "PIXEL_2020_EXPERIENCE",
            "PIXEL_2020_MIDYEAR_EXPERIENCE",
            "PIXEL_2021_MIDYEAR_EXPERIENCE"
    );

    private static final Set<String> sTensorFeatures = Set.of(
            "PIXEL_2021_EXPERIENCE",
            "PIXEL_2022_EXPERIENCE",
            "PIXEL_2022_MIDYEAR_EXPERIENCE",
            "PIXEL_2023_EXPERIENCE",
            "PIXEL_2023_MIDYEAR_EXPERIENCE",
            "PIXEL_2024_EXPERIENCE",
            "PIXEL_2024_MIDYEAR_EXPERIENCE"
    );

    private static final Map<String, String> DEFAULT_VALUES = Map.of(
        "BRAND", "google",
        "MANUFACTURER", "Google",
        "DEVICE", "husky",
        "FINGERPRINT", "google/husky_beta/husky:15/AP31.240517.022/11948202:user/release-keys",
        "MODEL", "Pixel 8 Pro",
        "PRODUCT", "husky_beta",
        "DEVICE_INITIAL_SDK_INT", "21",
        "SECURITY_PATCH", "2024-07-05",
        "ID", "AP31.240617.009"
    );

    private static volatile String sStockFp, sNetflixModel;

    private static volatile String sProcessName;
    private static volatile boolean sIsGms, sIsFinsky, sIsPhotos, sIsTablet;

    public static void setProps(Context context) {
        final String packageName = context.getPackageName();
        final String processName = Application.getProcessName();

        if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(processName)) {
            Log.e(TAG, "Null package or process name");
            return;
        }

        final Resources res = context.getResources();
        if (res == null) {
            Log.e(TAG, "Null resources");
            return;
        }

        sStockFp = res.getString(R.string.config_stockFingerprint);
        sNetflixModel = res.getString(R.string.config_netflixSpoofModel);
        sIsTablet = res.getBoolean(R.bool.config_spoofasTablet);

        sProcessName = processName;
        sIsGms = packageName.equals(PACKAGE_GMS) && processName.equals(PROCESS_GMS_UNSTABLE);
        sIsFinsky = packageName.equals(PACKAGE_FINSKY);
        sIsPhotos = packageName.equals(PACKAGE_GPHOTOS);

        /* Set certified properties for GMSCore
         * Set stock fingerprint for ARCore
         * Set Pixel 8 Pro for Google, ASI and GMS device configurator
         * Set Pixel XL for Google Photos
         * Set custom model for Netflix
         */

        switch (processName) {
            case PROCESS_GMS_UNSTABLE:
                dlog("Setting certified props for: " + packageName + " process: " + processName);
                setCertifiedPropsForGms();
                return;
            case PROCESS_GMS_PERSISTENT:
            case PROCESS_GMS_GAPPS:
            case PROCESS_GMS_GSERVICE:
            case PROCESS_GMS_LEARNING:
            case PROCESS_GMS_SEARCH:
            case PROCESS_GMS_UPDATE:
                dlog("Spoofing Pixel 5a for: " + packageName + " process: " + processName);
                setProps(sPixelFiveProps);
                return;
        }

        if (!sStockFp.isEmpty() && packageName.equals(PACKAGE_ARCORE)) {
            dlog("Setting stock fingerprint for: " + packageName);
            setPropValue("FINGERPRINT", sStockFp);
            return;
        }

        switch (packageName) {
            case PACKAGE_AIWALLPAPERS:
            case PACKAGE_ASSISTANT:
            case PACKAGE_ASI:
            case PACKAGE_EMOJIWALLPAPER:
            case PACKAGE_GMS:
            case PACKAGE_LIVEWALLPAPER:
            case PACKAGE_NEXUSLAUNCHER:
            case PACKAGE_PIXELTHEMES:
            case PACKAGE_PIXELWALLPAPER:
            case PACKAGE_SUBSCRIPTION_RED:
            case PACKAGE_VELVET:
            case PACKAGE_WALLPAPER:
            case PACKAGE_WALLPAPEREFFECTS:
                if (sIsTablet) {
                    dlog("Spoofing Pixel Tablet for: " + packageName + " process: " + processName);
                    setProps(sPixelTabletProps);
                } else {
                    dlog("Spoofing Pixel 8 Pro for: " + packageName + " process: " + processName);
                    setProps(sPixelEightProps);
                }
                return;
            case PACKAGE_GPHOTOS:
                dlog("Spoofing Pixel XL for Google Photos");
                setProps(sPixelXLProps);
                return;
            case PACKAGE_NETFLIX:
                if (!sNetflixModel.isEmpty()) {
                    dlog("Setting model to " + sNetflixModel + " for Netflix");
                    setPropValue("MODEL", sNetflixModel);
                }
                return;
        }
    }

    private static void setProps(Map<String, String> props) {
        props.forEach(PropImitationHooks::setPropValue);
    }

    private static void setPropValue(String key, String value) {
        try {
            dlog("Setting prop " + key + " to " + value.toString());
            Class clazz = Build.class;
            if (key.startsWith("VERSION.")) {
                clazz = Build.VERSION.class;
                key = key.substring(8);
            }
            Field field = clazz.getDeclaredField(key);
            field.setAccessible(true);
            // Cast the value to int if it's an integer field, otherwise string.
            field.set(null, field.getType().equals(Integer.TYPE) ? Integer.parseInt(value) : value);
            field.setAccessible(false);
        } catch (Exception e) {
            Log.e(TAG, "Failed to set prop " + key, e);
        }
    }

    private static void setPIFPropValue(String key, Object value) {
        try {
            Field field = getBuildClassField(key);
            if (field != null) {
                field.setAccessible(true);
                if (field.getType() == int.class) {
                    if (value instanceof String) {
                        field.set(null, Integer.parseInt((String) value));
                    } else if (value instanceof Integer) {
                        field.set(null, (Integer) value);
                    }
                } else if (field.getType() == long.class) {
                    if (value instanceof String) {
                        field.set(null, Long.parseLong((String) value));
                    } else if (value instanceof Long) {
                        field.set(null, (Long) value);
                    }
                } else {
                    field.set(null, value.toString());
                }
                field.setAccessible(false);
                dlog("Set prop " + key + " to " + value);
            } else {
                Log.e(TAG, "Field " + key + " not found in Build or Build.VERSION classes");
            }
        } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
            Log.e(TAG, "Failed to set prop " + key, e);
        }
    }

    private static Field getBuildClassField(String key) throws NoSuchFieldException {
        try {
            Field field = Build.class.getDeclaredField(key);
            dlog("Field " + key + " found in Build.class");
            return field;
        } catch (NoSuchFieldException e) {
            Field field = Build.VERSION.class.getDeclaredField(key);
            dlog("Field " + key + " found in Build.VERSION.class");
            return field;
        }
    }

    private static void setCertifiedPropsForGms() {
        if (!SystemProperties.getBoolean(SPOOF_PIXEL_GMS, true)) {
            dlog("GMS Spoof is disabled by user");
            return;
        }
        final boolean was = isGmsAddAccountActivityOnTop();
        final TaskStackListener taskStackListener = new TaskStackListener() {
            @Override
            public void onTaskStackChanged() {
                final boolean is = isGmsAddAccountActivityOnTop();
                if (is ^ was) {
                    dlog("GmsAddAccountActivityOnTop is:" + is + " was:" + was +
                            ", killing myself!"); // process will restart automatically later
                    Process.killProcess(Process.myPid());
                }
            }
        };
        if (!was) {
            dlog("Spoofing build for GMS");
            setCertifiedProps();
        } else {
            dlog("Skip spoofing build for GMS, because GmsAddAccountActivityOnTop");
        }
        try {
            ActivityTaskManager.getService().registerTaskStackListener(taskStackListener);
        } catch (Exception e) {
            Log.e(TAG, "Failed to register task stack listener!", e);
        }
    }

    private static void setCertifiedProps() {
        for (Map.Entry<String, String> entry : DEFAULT_VALUES.entrySet()) {
            String propKey = PROP_HOOKS + entry.getKey();
            String value = SystemProperties.get(propKey);
            setPIFPropValue(entry.getKey(), value != null && !value.isEmpty() ? value : entry.getValue());
        }
    }

    private static void setSystemProperty(String name, String value) {
        try {
            SystemProperties.set(name, value);
            dlog("Set system prop " + name + "=" + value);
        } catch (Exception e) {
            Log.e(TAG, "Failed to set system prop " + name + "=" + value, e);
        }
    }

    private static boolean isGmsAddAccountActivityOnTop() {
        try {
            final ActivityTaskManager.RootTaskInfo focusedTask =
                    ActivityTaskManager.getService().getFocusedRootTaskInfo();
            return focusedTask != null && focusedTask.topActivity != null
                    && focusedTask.topActivity.equals(GMS_ADD_ACCOUNT_ACTIVITY);
        } catch (Exception e) {
            Log.e(TAG, "Unable to get top activity!", e);
        }
        return false;
    }

    public static boolean shouldBypassTaskPermission(Context context) {
        // GMS doesn't have MANAGE_ACTIVITY_TASKS permission
        final int callingUid = Binder.getCallingUid();
        final int gmsUid;
        try {
            gmsUid = context.getPackageManager().getApplicationInfo(PACKAGE_GMS, 0).uid;
            dlog("shouldBypassTaskPermission: gmsUid:" + gmsUid + " callingUid:" + callingUid);
        } catch (Exception e) {
            Log.e(TAG, "shouldBypassTaskPermission: unable to get gms uid", e);
            return false;
        }
        return gmsUid == callingUid;
    }

    private static boolean isCallerSafetyNet() {
        return sIsGms && Arrays.stream(Thread.currentThread().getStackTrace())
                .anyMatch(elem -> elem.getClassName().contains("DroidGuard"));
    }

    public static void onEngineGetCertificateChain() {
        if (!SystemProperties.getBoolean(SPOOF_PIXEL_GMS, true))
            return;
        // Check stack for SafetyNet or Play Integrity
        if (isCallerSafetyNet() || sIsFinsky) {
            dlog("Blocked key attestation sIsGms=" + sIsGms + " sIsFinsky=" + sIsFinsky);
            throw new UnsupportedOperationException();
        }
    }

    public static boolean hasSystemFeature(String name, boolean has) {
        if (sIsPhotos) {
            if (has && (sPixelFeatures.stream().anyMatch(name::contains)
                    || sTensorFeatures.stream().anyMatch(name::contains))) {
                dlog("Blocked system feature " + name + " for Google Photos");
                has = false;
            } else if (!has && sNexusFeatures.stream().anyMatch(name::contains)) {
                dlog("Enabled system feature " + name + " for Google Photos");
                has = true;
            }
        }
        return has;
    }

    public static void dlog(String msg) {
        if (DEBUG) Log.d(TAG, "[" + sProcessName + "] " + msg);
    }
}
