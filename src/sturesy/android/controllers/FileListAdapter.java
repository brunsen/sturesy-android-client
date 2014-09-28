/*
 * StuReSy - Student Response System
 * Copyright (C) 2012-2014  StuReSy-Team
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package sturesy.android.controllers;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
/**
 * 
 * @author b.brunsen
 *
 */
public class FileListAdapter extends ArrayAdapter<File> {

	private List<File> _files;
	private int _layoutResourceId;
	private Context _context;

	public FileListAdapter(Context context, int resource, List<File> objects) {
		super(context,resource,objects);
		_layoutResourceId = resource;
		_context = context;
		_files = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = ((Activity) _context).getLayoutInflater();
			row = inflater.inflate(_layoutResourceId, parent, false);
		}
		String filename = _files.get(position).getName();
		String displayText = filename;
		displayText = displayText.replace(".xml", "");
		displayText = displayText.replace(".csv", "");
		displayText = displayText.replace(".zip", "");
		TextView tv = (TextView) row.findViewById(android.R.id.text1);
		tv.setText(displayText);
		return row;
	}
}
