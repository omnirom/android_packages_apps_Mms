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
package com.android.mms.response.util;

public class LogEntry {
    public static final int PHONE_CALL = 0;
    public static final int TEXT_MESSAGE = 1;

    private int type;
    private String time;
    private String date;
    private String name;
    private String number;

    public LogEntry() {
        type = PHONE_CALL;
        time = "";
        date = "";
        name = "";
        number = "";
    }

    public LogEntry(int type, String time, String date, String name,
            String number) {
        this.type = type;
        this.time = time;
        this.date = date;
        this.name = name;
        this.number = number;
    }

    public int getType() {
        return type;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
