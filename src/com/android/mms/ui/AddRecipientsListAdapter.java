/*
 * Copyright (C) 2013 The CyanogenMod Project
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

package com.android.mms.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;
import android.util.Log;

import com.android.mms.R;
import com.android.mms.data.Group;
import com.android.mms.data.PhoneNumber;
import com.android.mms.util.HanziToPinyin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Iterator;

public class AddRecipientsListAdapter extends ArrayAdapter<AddRecipientsListItem> implements SectionIndexer {
    private static final String TAG = "AddRecipientsListAdapter";
    private final LayoutInflater mFactory;
    private HashMap<String, Integer> alphaIndexer;
    private String[] sections;
    private java.text.Collator mCollator;

    public AddRecipientsListAdapter(Context context, List<AddRecipientsListItem> items) {
        super(context, R.layout.add_recipients_list_item, items);
        mFactory = LayoutInflater.from(context);

        alphaIndexer = new HashMap<String, Integer>();
        for (int i = 0; i < items.size(); i++) {
            AddRecipientsListItem item = items.get(i);
            if (!item.isGroup()) {
                String name = item.getPhoneNumber().getName();
                String s = name.substring(0, 1).toUpperCase();
                // maxwen: TODO see ContactsProvider:src/com/android/providers/contacts/ContactLocaleUtils.java
                /*String hzPinYin = null;
                ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance().get(name);
                if (tokens.size() > 0){
                    hzPinYin.get(0);
                }

                if (hzPinYin != null && !name.equals(hzPinYin) && !hzPinYin.isEmpty()) {
                    s = hzPinYin;
                }*/

                if (!alphaIndexer.containsKey(s)) {
                    alphaIndexer.put(s, i);
                }
            }
        }

        Set<String> sectionLetters = alphaIndexer.keySet();
        ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
        Collections.sort(sectionList);
        sections = sectionList.toArray(new String[sectionList.size()]);

        mCollator = java.text.Collator.getInstance();
        mCollator.setStrength(java.text.Collator.PRIMARY);
    }

    public View getView(int position, View convertView, ViewGroup viewGroup) {
        AddRecipientsListItem view;

        if (convertView == null) {
            view = (AddRecipientsListItem) mFactory.inflate(
                    R.layout.add_recipients_list_item, viewGroup, false);
        } else {
            if (convertView instanceof AddRecipientsListItem) {
                view = (AddRecipientsListItem) convertView;
            } else {
                return convertView;
            }
        }

        bindView(position, view);
        return view;
    }

    private void bindView(int position, AddRecipientsListItem view) {
        final AddRecipientsListItem item = this.getItem(position);

        PhoneNumber phoneNumber = item.getPhoneNumber();
        Group group = item.getGroup();
        boolean showHeader;

        if (!item.isGroup()) {
            showHeader = alphaIndexer.containsValue(position);

            boolean showFooter = true;
            long cid = phoneNumber.getContactId();
            PhoneNumber nextPhoneNumber = null;
            long nextCid = -1;
            int lastIndex = this.getCount() - 1;

            if (position < lastIndex) {
                int nextPosition = position + 1;
                nextPhoneNumber = this.getItem(nextPosition).getPhoneNumber();
                if (nextPhoneNumber != null) {
                    nextCid = nextPhoneNumber.getContactId();
                }
            }

            if (cid == nextCid) {
                showFooter = false;
                nextPhoneNumber.setFirst(false);
            }
            view.bind(getContext(), phoneNumber, showHeader, showFooter);
        } else {
            showHeader = (position == 0);
            view.bind(getContext(), group, showHeader);
        }
    }

    /**
     * Default implementation compares the first character of word with letter.
     */
    private int compare(String word, String letter) {
        final String firstLetter;
        if (word.length() == 0) {
            firstLetter = " ";
        } else {
            firstLetter = word.substring(0, 1);
        }

        return mCollator.compare(firstLetter, letter);
    }

    @Override
    public int getPositionForSection(int section) {
        return alphaIndexer.get(sections[section]);
    }

    @Override
    public int getSectionForPosition(int position) {
        AddRecipientsListItem item = getItem(position);
        if (item == null || item.isGroup()){
            return 0;
        }
        for (int i = 0; i < sections.length; i++){

            if (compare(item.getPhoneNumber().getName(), sections[i]) == 0) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public Object[] getSections() {
        return sections;
    }
}
