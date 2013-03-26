
package kq.xtoolkit;

import java.util.ArrayList;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class SettingsActivity extends PreferenceActivity implements
        OnSharedPreferenceChangeListener, OnCheckedChangeListener {
    public static final String PREFS_NAME = "x_settings";
    /**
     * Default is true.
     */
    public static final String ENABLE_SWITCH = "enable_switch";
    public static final String BOUNCER_ANIMATION = "bouncer_animation_preference";
    public static final String LOCK_SHORTCUT = "lock_screen_shortcut_preference";
    public static final String SCREENSHOT_SHORTCUT = "screenshot_shortcut_preference";
    public static final String FLASHLIGHT_SHORTCUT = "flashlight_shortcut_preference";
    // switch and checkbox
    private Switch mEnabledSwitch;
    private CheckBoxPreference mBouncerAnimation;
    private CheckBoxPreference mLockShortcut;
    private CheckBoxPreference mScreenShotShortcut;
    private CheckBoxPreference mFlashLightShortcut;

    private final ArrayList<Preference> mAllPrefs = new ArrayList<Preference>();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName(PREFS_NAME);
        addPreferencesFromResource(R.xml.setting_preferences);

        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        // switch button
        initEnableSwitch();
        // checkbox
        mBouncerAnimation = findAndInitCheckboxPref(BOUNCER_ANIMATION);
        mLockShortcut = findAndInitCheckboxPref(LOCK_SHORTCUT);
        mScreenShotShortcut = findAndInitCheckboxPref(SCREENSHOT_SHORTCUT);
        mFlashLightShortcut = findAndInitCheckboxPref(FLASHLIGHT_SHORTCUT);
        // start service
        boolean enable = getBooleanPreference(ENABLE_SWITCH);
        if (enable) {
            startService(new Intent(this, XToolKitService.class));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar()
                .setCustomView(
                        mEnabledSwitch,
                        new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL
                                        | Gravity.END));
    }

    @Override
    protected void onStop() {
        getActionBar().setDisplayOptions(0, ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(null);
        super.onStop();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        final boolean value= sharedPreferences.getBoolean(key, true);
        if (key.equals(ENABLE_SWITCH)) {
            if (value) {
                startService(new Intent(this, XToolKitService.class));
            } else {
                stopService(new Intent(this, XToolKitService.class));
            }
        } else if (key.equals(BOUNCER_ANIMATION)) {
            XUtils.gBouncerFlag = value;
        } else if (key.equals(LOCK_SHORTCUT)) {
            XUtils.gLockFlag = value;
        } else if (key.equals(SCREENSHOT_SHORTCUT)) {
            XUtils.gScreenShotFlag = value;
        } else if (key.equals(FLASHLIGHT_SHORTCUT)) {
            XUtils.gFlashLightFlag = value;
        } else {
            
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == mEnabledSwitch) {
            setBooleanPreference(ENABLE_SWITCH, isChecked);
            setPrefsEnabledState(isChecked);
        }

    }

    private void setPrefsEnabledState(boolean enabled) {
        for (int i = 0; i < mAllPrefs.size(); i++) {
            Preference pref = mAllPrefs.get(i);
            pref.setEnabled(enabled);
        }
    }

    private void initEnableSwitch() {
        mEnabledSwitch = new Switch(this);
        final int padding = getResources().getDimensionPixelSize(
                R.dimen.action_bar_switch_padding);
        mEnabledSwitch.setPadding(0, 0, padding, 0);
        mEnabledSwitch.setChecked(getBooleanPreference(ENABLE_SWITCH));
        mEnabledSwitch.setOnCheckedChangeListener(this);
    }

    private CheckBoxPreference findAndInitCheckboxPref(String key) {
        CheckBoxPreference pref = (CheckBoxPreference) findPreference(key);
        if (pref == null) {
            throw new IllegalArgumentException("Cannot find preference with key = " + key);
        }
        mAllPrefs.add(pref);
        return pref;
    }

    private void setBooleanPreference(String key, boolean value){
        SharedPreferences settings = getSharedPreferences(SettingsActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    private boolean getBooleanPreference(String key) {
        SharedPreferences settings = getSharedPreferences(SettingsActivity.PREFS_NAME, 0);
        boolean value = settings.getBoolean(key, false);
        return value;
    }


}
