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

public class Message
{
	private String title;
	private String content;
	
	public Message()
	{
		title = "";
		content = "";
	}
	
	public Message(String title, String content)
	{
		this.title = title;
		this.content = content;
	}

	public String getTitle()							{ return title;				}
	public String getContent()							{ return content;			}
	
	public void setTitle(String title)					{ this.title = title; 		}
	public void setContent(String content)				{ this.content = content;	}
	
	public void setInfo(String title, String content)	{ this.title = title;
														  this.content = content;	}
}
