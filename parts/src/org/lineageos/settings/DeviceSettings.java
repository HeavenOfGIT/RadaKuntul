package org.lineageos.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
import androidx.preference.TwoStatePreference;

import org.lineageos.settings.fps.FPSInfoService;
import org.lineageos.settings.preferences.SecureSettingListPreference;
import org.lineageos.settings.preferences.SecureSettingSwitchPreference;
import org.lineageos.settings.preferences.VibrationSeekBarPreference;
import org.lineageos.settings.preferences.CustomSeekBarPreference;

import org.lineageos.settings.R;

public class DeviceSettings extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "DeviceSettings";

    // Vibration override will use bool instead of integer
    public static final String PREF_VIBRATION_OVERRIDE = "vmax_override";
    public static final String PREF_VIBRATION_PATH = "/sys/devices/platform/soc/200f000.qcom,spmi/spmi-0/spmi0-03/200f000.qcom,spmi:qcom,pmi8950@3:qcom,haptics@c000/leds/vibrator/vmax_override";

    public static final String PREF_VIBRATION_SYSTEM_STRENGTH = "vibration_system";
    public static final String PREF_VIBRATION_NOTIFICATION_STRENGTH = "vibration_notification";
    public static final String PREF_VIBRATION_CALL_STRENGTH = "vibration_call";
    public static final String VIBRATION_SYSTEM_PATH = "/sys/devices/platform/soc/200f000.qcom,spmi/spmi-0/spmi0-03/200f000.qcom,spmi:qcom,pmi8950@3:qcom,haptics@c000/leds/vibrator/vmax_mv_user";
    public static final String VIBRATION_NOTIFICATION_PATH = "/sys/devices/platform/soc/200f000.qcom,spmi/spmi-0/spmi0-03/200f000.qcom,spmi:qcom,pmi8950@3:qcom,haptics@c000/leds/vibrator/vmax_mv_strong";
    public static final String VIBRATION_CALL_PATH = "/sys/devices/platform/soc/200f000.qcom,spmi/spmi-0/spmi0-03/200f000.qcom,spmi:qcom,pmi8950@3:qcom,haptics@c000/leds/vibrator/vmax_mv_call";

    // value of vtg_min and vtg_max
    public static final int MIN_VIBRATION = 116;
    public static final int MAX_VIBRATION = 3596;

    public static final String PREF_MSM_TOUCHBOOST = "touchboost";
    public static final String MSM_TOUCHBOOST_PATH = "/sys/module/msm_performance/parameters/touchboost";

    public static final String PREF_KEY_FPS_INFO = "fps_info";

    private SecureSettingSwitchPreference mTouchboost;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.xiaomi_main, rootKey);
        mContext = this.getContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        SecureSettingSwitchPreference vib = (SecureSettingSwitchPreference) findPreference(PREF_VIBRATION_OVERRIDE);
        vib.setEnabled(Vibration.isSupported());
        vib.setChecked(Vibration.isCurrentlyEnabled(this.getContext()));
        vib.setOnPreferenceChangeListener(new Vibration(getContext()));
        Preference ambientDisplay = findPreference(AMBIENT_DISPLAY);
        ambientDisplay.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getContext(), AmbientGesturePreferenceActivity.class);
            startActivity(intent);
            return true;
        });
        VibrationSeekBarPreference vibrationSystemStrength = (VibrationSeekBarPreference) findPreference(PREF_VIBRATION_SYSTEM_STRENGTH);
        vibrationSystemStrength.setEnabled(FileUtils.fileWritable(VIBRATION_SYSTEM_PATH));
        vibrationSystemStrength.setOnPreferenceChangeListener(this);

        VibrationSeekBarPreference vibrationNotificationStrength = (VibrationSeekBarPreference) findPreference(PREF_VIBRATION_NOTIFICATION_STRENGTH);
        vibrationNotificationStrength.setEnabled(FileUtils.fileWritable(VIBRATION_NOTIFICATION_PATH));
        vibrationNotificationStrength.setOnPreferenceChangeListener(this);

        VibrationSeekBarPreference vibrationCallStrength = (VibrationSeekBarPreference) findPreference(PREF_VIBRATION_CALL_STRENGTH);
        vibrationCallStrength.setEnabled(FileUtils.fileWritable(VIBRATION_CALL_PATH));
        vibrationCallStrength.setOnPreferenceChangeListener(this);

        SwitchPreference fpsInfo = (SwitchPreference) findPreference(PREF_KEY_FPS_INFO);
        fpsInfo.setChecked(prefs.getBoolean(PREF_KEY_FPS_INFO, false));
        fpsInfo.setOnPreferenceChangeListener(this);

        if (FileUtils.fileWritable(MSM_TOUCHBOOST_PATH)) {
            mTouchboost = (SecureSettingSwitchPreference) findPreference(PREF_MSM_TOUCHBOOST);
            mTouchboost.setEnabled(Touchboost.isSupported());
            mTouchboost.setChecked(Touchboost.isCurrentlyEnabled(this.getContext()));
            mTouchboost.setOnPreferenceChangeListener(new Touchboost(getContext()));
        } else {
            getPreferenceScreen().removePreference(findPreference(PREF_MSM_TOUCHBOOST));
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        final String key = preference.getKey();
        switch (key) {
            case PREF_VIBRATION_SYSTEM_STRENGTH:
                double VibrationSystemValue = (int) value / 100.0 * (MAX_VIBRATION - MIN_VIBRATION) + MIN_VIBRATION;
                FileUtils.setValue(VIBRATION_SYSTEM_PATH, VibrationSystemValue);
                break;

            case PREF_VIBRATION_NOTIFICATION_STRENGTH:
                double VibrationNotificationValue = (int) value / 100.0 * (MAX_VIBRATION - MIN_VIBRATION) + MIN_VIBRATION;
                FileUtils.setValue(VIBRATION_NOTIFICATION_PATH, VibrationNotificationValue);
                break;

            case PREF_VIBRATION_CALL_STRENGTH:
                double VibrationCallValue = (int) value / 100.0 * (MAX_VIBRATION - MIN_VIBRATION) + MIN_VIBRATION;
                FileUtils.setValue(VIBRATION_CALL_PATH, VibrationCallValue);
                break;

            case PREF_KEY_FPS_INFO:
                boolean enabled = (Boolean) value;
                Intent fpsinfo = new Intent(this.getContext(), FPSInfoService.class);
                if (enabled) {
                    this.getContext().startService(fpsinfo);
                } else {
                    this.getContext().stopService(fpsinfo);
                }
                break;
        }
    }
