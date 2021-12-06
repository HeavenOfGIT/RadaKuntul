/*
 * Copyright (C) 2015 The CyanogenMod Project
 *               2017-2018 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lineageos.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.provider.Settings;

import org.lineageos.settings.dirac.DiracUtils;
import org.lineageos.settings.doze.DozeUtils;
import org.lineageos.settings.preferences.FileUtils;
import org.lineageos.settings.soundcontrol.SoundControlSettings;
import org.lineageos.settings.fps.FPSInfoService;
import org.lineageos.settings.vibration.VibrationSettings;
import org.lineageos.settings.vibration.VibrationUtils;
import org.lineageos.settings.torch.TorchSettings;

public class BootCompletedReceiver extends BroadcastReceiver implements Controller {

    private static final boolean DEBUG = false;
    private static final String TAG = "XiaomiParts";

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (DozeUtils.isDozeEnabled(context) && DozeUtils.sensorsEnabled(context)) {
            if (DEBUG) Log.d(TAG, "Starting Doze service");
            DozeUtils.startService(context);
        }
        new DiracUtils(context).onBootCompleted();

        FileUtils.setValue(TorchSettings.TORCH_1_BRIGHTNESS_PATH,
                Settings.Secure.getInt(context.getContentResolver(),
                        TorchSettings.KEY_WHITE_TORCH_BRIGHTNESS, 100));
        FileUtils.setValue(TorchSettings.TORCH_2_BRIGHTNESS_PATH,
                Settings.Secure.getInt(context.getContentResolver(),
                        TorchSettings.KEY_YELLOW_TORCH_BRIGHTNESS, 100));
    int gain = Settings.Secure.getInt(context.getContentResolver(),
                SoundControlSettings.PREF_HEADPHONE_GAIN, 4);
        FileUtils.setValue(SoundControlSettings.HEADPHONE_GAIN_PATH, gain + " " + gain);
        FileUtils.setValue(SoundControlSettings.MICROPHONE_GAIN_PATH, Settings.Secure.getInt(context.getContentResolver(),
                SoundControlSettings.PREF_MICROPHONE_GAIN, 0));
        FileUtils.setValue(SoundControlSettings.SPEAKER_GAIN_PATH, Settings.Secure.getInt(context.getContentResolver(),
                SoundControlSettings.PREF_SPEAKER_GAIN, 0));

        if (Settings.Secure.getInt(context.getContentResolver(), PREF_ENABLED, 0) == 1) {
            FileUtils.setValue(KCAL_ENABLE, Settings.Secure.getInt(context.getContentResolver(),
                    PREF_ENABLED, 0));

            String rgbValue = Settings.Secure.getInt(context.getContentResolver(),
                    PREF_RED, RED_DEFAULT) + " " +
                    Settings.Secure.getInt(context.getContentResolver(), PREF_GREEN,
                            GREEN_DEFAULT) + " " +
                    Settings.Secure.getInt(context.getContentResolver(), PREF_BLUE,
                            BLUE_DEFAULT);

            FileUtils.setValue(KCAL_RGB, rgbValue);
            FileUtils.setValue(KCAL_MIN, Settings.Secure.getInt(context.getContentResolver(),
                    PREF_MINIMUM, MINIMUM_DEFAULT));
            FileUtils.setValue(KCAL_SAT, Settings.Secure.getInt(context.getContentResolver(),
                    PREF_GRAYSCALE, 0) == 1 ? 128 :
                    Settings.Secure.getInt(context.getContentResolver(),
                            PREF_SATURATION, SATURATION_DEFAULT) + SATURATION_OFFSET);
            FileUtils.setValue(KCAL_VAL, Settings.Secure.getInt(context.getContentResolver(),
                    PREF_VALUE, VALUE_DEFAULT) + VALUE_OFFSET);
            FileUtils.setValue(KCAL_CONT, Settings.Secure.getInt(context.getContentResolver(),
                    PREF_CONTRAST, CONTRAST_DEFAULT) + CONTRAST_OFFSET);
            FileUtils.setValue(KCAL_HUE, Settings.Secure.getInt(context.getContentResolver(),
                    PREF_HUE, HUE_DEFAULT));
        }

        //Vibration
	VibrationUtils.setValue(VibrationSettings.PREF_VIBRATION_PATH, Settings.Secure.getInt(context.getContentResolver(),
                VibrationSettings.PREF_VIBRATION_OVERRIDE, 0));
	VibrationUtils.setValue(VibrationSettings.VIBRATION_SYSTEM_PATH, Settings.Secure.getInt(
                context.getContentResolver(), VibrationSettings.PREF_VIBRATION_SYSTEM_STRENGTH, 80) / 100.0 * (VibrationSettings.MAX_VIBRATION - VibrationSettings.MIN_VIBRATION) + VibrationSettings.MIN_VIBRATION);
	VibrationUtils.setValue(VibrationSettings.VIBRATION_NOTIFICATION_PATH, Settings.Secure.getInt(
                context.getContentResolver(), VibrationSettings.PREF_VIBRATION_NOTIFICATION_STRENGTH, 80) / 100.0 * (VibrationSettings.MAX_VIBRATION - VibrationSettings.MIN_VIBRATION) + VibrationSettings.MIN_VIBRATION);
        VibrationUtils.setValue(VibrationSettings.VIBRATION_CALL_PATH, Settings.Secure.getInt(
                context.getContentResolver(), VibrationSettings.PREF_VIBRATION_CALL_STRENGTH, 80) / 100.0 * (VibrationSettings.MAX_VIBRATION - VibrationSettings.MIN_VIBRATION) + VibrationSettings.MIN_VIBRATION);

        //TouchBoost
        FileUtils.setValue(DeviceSettings.MSM_TOUCHBOOST_PATH, Settings.Secure.getInt(context.getContentResolver(),
                DeviceSettings.PREF_MSM_TOUCHBOOST, 0));

    }
}
