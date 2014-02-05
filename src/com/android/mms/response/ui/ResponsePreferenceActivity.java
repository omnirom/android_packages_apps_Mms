/*
 *  Copyright (C) 2013 The OmniROM Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.android.mms.response.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.android.mms.R;
import com.android.mms.response.util.Message;

@SuppressLint("UseValueOf")
@SuppressWarnings("deprecation")
public class ResponsePreferenceActivity extends PreferenceActivity implements
        OnPreferenceChangeListener {

    private static final String TAG = "AutoResponse";
    private static final String messagesFile = "AutoMessages.txt";

    final String SERVICE_PREF = "serviceCheckBox";
    final String SILENT_PREF = "silentCheckBox";
    final String CALLTEXT_PREF = "callTextListPref";

    final String MESSAGE_PREF = "messageListPref";
    final String FILTER_PREF = "filteringListPref";
    final String INFORM_PREF = "informCheckBox";
    final String DELAY_PREF = "delayListPref";
    final String REPEAT_PREF = "repeatCheckBox";

    CheckBoxPreference serviceCheckBox;
    CheckBoxPreference silentCheckBox;
    ListPreference callTextListPref;

    ListPreference messageListPref;
    ListPreference filteringListPref;
    CheckBoxPreference informCheckBox;
    ListPreference delayListPref;
    CheckBoxPreference repeatCheckBox;

    Dialog dialog;

    Resources r;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    static final int FILTERING_DIALOG_ID = 1;

    static final int FILTER_MENU_OFF = 0;
    static final int FILTER_MENU_CONTACTS = 1;
    static final int FILTER_MENU_BLACKLIST = 2;
    static final int FILTER_MENU_WHITELIST = 3;

    static final int MENU_ITEM_MESSAGES = 0;
    static final int MENU_ITEM_FILTER = 1;
    static final int MENU_ITEM_SCHEDULE = 2;
    static final int MENU_ITEM_THEME = 3;

    static final int CALLTEXT_BOTH = 0;
    static final int CALLTEXT_CALL = 1;
    static final int CALLTEXT_TEXT = 2;

    private ArrayList<Message> messages = new ArrayList<Message>();

    public void onCreate(Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();

        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences_response);

        // Create XML to Android Object Binds and register Change/Click
        // Listeners
        serviceCheckBox = (CheckBoxPreference) findPreference(SERVICE_PREF);
        serviceCheckBox.setOnPreferenceChangeListener(this);
        silentCheckBox = (CheckBoxPreference) findPreference(SILENT_PREF);
        callTextListPref = (ListPreference) findPreference(CALLTEXT_PREF);

        messageListPref = (ListPreference) findPreference(MESSAGE_PREF);
        filteringListPref = (ListPreference) findPreference(FILTER_PREF);
        informCheckBox = (CheckBoxPreference) findPreference(INFORM_PREF);
        delayListPref = (ListPreference) findPreference(DELAY_PREF);
        repeatCheckBox = (CheckBoxPreference) findPreference(REPEAT_PREF);

        // Load Custom Messages and Generate Drop Down Lists
        messagesExist(messagesFile);
        createListPreferences(false);
    }

    public void onResume() {
        super.onResume();

        editor.commit();

        if (getServiceStatus())
            setPreferenceStatus(false); // Disable Preference Changing if
                                        // Service is Running

        messagesExist(messagesFile);
        createListPreferences(true);
    }

    public boolean onPreferenceChange(Preference p, Object o) {
        if (p.getKey().equals(SERVICE_PREF)) {
            final Intent awayService = new Intent(this, ResponseService.class);

            if (prefs.getBoolean(SERVICE_PREF, false)) {
                setServiceStatus(false);
                setPreferenceStatus(true);
                stopService(awayService);

                return true;
            }

            else {
                setServiceStatus(true);
                setPreferenceStatus(false);

                // Set Intent Extras
                awayService.putExtra("extraSilentStatus", getSilentStatus());
                awayService.putExtra("extraCallText", getCallText());
                awayService.putExtra("extraMessage", getMessage());
                awayService.putExtra("extraInformStatus", getInformStatus());
                awayService.putExtra("extraDelay", getDelay());
                awayService.putExtra("extraRepeatStatus", getRepeatStatus());
                awayService.putExtra("extraFilterStatus", getFilterStatus());

                // Start service and terminate activity
                startService(awayService);

                return true;
            }
        }

        return false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        if (getServiceStatus()) {
            menu.getItem(MENU_ITEM_MESSAGES).setEnabled(false);
            menu.getItem(MENU_ITEM_FILTER).setEnabled(false);
            // menu.getItem(MENU_ITEM_SCHEDULE).setEnabled(false);
        } else if (!getServiceStatus()) {
            menu.getItem(MENU_ITEM_MESSAGES).setEnabled(true);

            if (getFilterStatus() == FILTER_MENU_CONTACTS
                    || getFilterStatus() == FILTER_MENU_OFF)
                menu.getItem(MENU_ITEM_FILTER).setEnabled(false);
            else
                menu.getItem(MENU_ITEM_FILTER).setEnabled(true);

            // menu.getItem(MENU_ITEM_SCHEDULE).setEnabled(true);
        }

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_edit_messages:
            Intent message = new Intent(this, MessageActivity.class);
            startActivity(message);
            break;

        case R.id.menu_filtering:
            switch (getFilterStatus()) {
            case FILTER_MENU_OFF:
                showDialog(FILTERING_DIALOG_ID);
                break;

            case FILTER_MENU_CONTACTS:
                break;

            default:
                Intent filtering = new Intent(this, FilterActivity.class);
                filtering.putExtra("extraFilterStatus", getFilterStatus());

                startActivity(filtering);
                break;
            }
            break;
        }

        return true;
    }

    public boolean messagesExist(String file) {
        r = getResources();
        messages.removeAll(messages);

        Message defaultMessage = new Message(
                r.getString(R.string.default_message_title),
                r.getString(R.string.default_message_content));
        messages.add(defaultMessage);

        try {
            File inFile = getBaseContext().getFileStreamPath(file);

            if (inFile.exists()) {
                InputStream iStream = openFileInput(file);
                InputStreamReader iReader = new InputStreamReader(iStream);
                BufferedReader bReader = new BufferedReader(iReader);

                String line;

                // Should ALWAYS be in groups of two
                while ((line = bReader.readLine()) != null) {
                    Message messageFromFile = new Message(line,
                            bReader.readLine());
                    messages.add(messageFromFile);
                }

                iStream.close();
            }
        } catch (java.io.FileNotFoundException exception) {
            Log.e(TAG,
                    "FileNotFoundException caused by openFileInput(fileName)",
                    exception);
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by buffreader.readLine()", exception);
        }

        if (messages.isEmpty())
            return false;

        return true;
    }

    public void createListPreferences(boolean justMessages) {
        if (!justMessages) {
            CharSequence[] callTextNames = {
                    r.getString(R.string.pref_call_text_type_1),
                    r.getString(R.string.pref_call_text_type_2),
                    r.getString(R.string.pref_call_text_type_3) };
            CharSequence[] callTextValues = { "0", "1", "2" };

            callTextListPref.setEntries(callTextNames);
            callTextListPref.setEntryValues(callTextValues);

            CharSequence[] delayNames = {
                    r.getString(R.string.pref_delay_type_1),
                    r.getString(R.string.pref_delay_type_2),
                    r.getString(R.string.pref_delay_type_3),
                    r.getString(R.string.pref_delay_type_4),
                    r.getString(R.string.pref_delay_type_5),
                    r.getString(R.string.pref_delay_type_6) };

            CharSequence[] delayValues = { "0", "15", "30", "60", "120", "300" };

            delayListPref.setEntries(delayNames);
            delayListPref.setEntryValues(delayValues);

            CharSequence[] filteringNames = {
                    r.getString(R.string.pref_filter_type_1),
                    r.getString(R.string.pref_filter_type_2),
                    r.getString(R.string.pref_filter_type_3),
                    r.getString(R.string.pref_filter_type_4) };

            CharSequence[] filteringValues = {
                    Integer.toString(FILTER_MENU_OFF),
                    Integer.toString(FILTER_MENU_CONTACTS),
                    Integer.toString(FILTER_MENU_BLACKLIST),
                    Integer.toString(FILTER_MENU_WHITELIST) };
            filteringListPref.setEntries(filteringNames);
            filteringListPref.setEntryValues(filteringValues);
        }

        CharSequence[] messageTitle = new CharSequence[messages.size()];
        CharSequence[] messageContent = new CharSequence[messages.size()];

        for (int i = 0; i < messages.size(); i++) {
            messageTitle[i] = messages.get(i).getTitle();
            messageContent[i] = messages.get(i).getContent();
        }

        messageListPref.setEntries(messageTitle);
        messageListPref.setEntryValues(messageContent);
    }

    private void setPreferenceStatus(boolean status) {
        silentCheckBox.setEnabled(status);
        callTextListPref.setEnabled(status);

        messageListPref.setEnabled(status);
        filteringListPref.setEnabled(status);
        informCheckBox.setEnabled(status);
        delayListPref.setEnabled(status);
        repeatCheckBox.setEnabled(status);

    }

    protected Dialog onCreateDialog(int id) {
        AlertDialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (id) {
        case FILTERING_DIALOG_ID:
            builder.setTitle(r.getString(R.string.prompt_filtering_title))
                    .setNegativeButton(r.getString(R.string.menu_close),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int id) {
                                    dialog.cancel();
                                }
                            });
            dialog = builder.create();
            break;
        }

        return dialog;
    }

    public boolean getServiceStatus() {
        return prefs.getBoolean(SERVICE_PREF, false);
    }

    public void setServiceStatus(boolean serviceRunning) {
        editor.putBoolean(SERVICE_PREF, serviceRunning);
        editor.commit();
    }

    public boolean getSilentStatus() {
        return prefs.getBoolean(SILENT_PREF, true);
    }

    public void setSilentStatus(boolean isSilent) {
        editor.putBoolean(SILENT_PREF, isSilent);
        editor.commit();
    }

    public int getCallText() {
        return Integer.parseInt(prefs.getString(CALLTEXT_PREF, "0"));
    }

    public void setCallText(String callText) {
        editor.putString(CALLTEXT_PREF, callText);
        editor.commit();
    }

    public String getMessage() {
        return prefs.getString(MESSAGE_PREF,
                r.getString(R.string.default_message_content));
    }

    public int getFilterStatus() {
        return Integer.parseInt(prefs.getString(FILTER_PREF, "0"));
    }

    public void setFilterStatus(int id) {
        editor.putString(FILTER_PREF, Integer.toString(id));
        editor.commit();
    }

    public int getDelay() {
        return Integer.parseInt(prefs.getString(DELAY_PREF, "0"));
    }

    public boolean getInformStatus() {
        return prefs.getBoolean(INFORM_PREF, true);
    }

    public void setInformStatus(boolean informStatus) {
        editor.putBoolean(INFORM_PREF, informStatus);
        editor.commit();
    }

    public boolean getRepeatStatus() {
        return prefs.getBoolean(REPEAT_PREF, false);
    }

    public void setRepeatStatus(boolean repeatStatus) {
        editor.putBoolean(REPEAT_PREF, repeatStatus);
        editor.commit();
    }
}