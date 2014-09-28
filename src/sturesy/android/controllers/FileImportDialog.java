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
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sturesy.util.Folder;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import de.uhh.sturesy_android.R;
/**
 * 
 * @author b.brunsen
 *
 */
public class FileImportDialog extends Dialog implements OnItemClickListener, OnClickListener {
	
	private List<File> _files;
	private File _selectedFile;
	private ArrayAdapter<File> _fileAdapter;
	
	public FileImportDialog(Context context, FileFilter filter, String title) {
		super(context);
		setContentView(R.layout.dialog_user_choice);
		setTitle(title);
		Button okButton = (Button) findViewById(R.id.dialog_ok);
		Button cancelButton = (Button) findViewById(R.id.dialog_cancel);
		ListView lv = (ListView) findViewById(R.id.dialog_choice_list);
		
		okButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		setCancelable(false);
		
		_files = new ArrayList<File>();
		File lectureFolder = new File(Folder.getBaseFolder().getAbsolutePath() + "/lectures/");
		if(!lectureFolder.exists())
		{
			lectureFolder.mkdir();
		}
		File[] listFiles = lectureFolder.listFiles(filter);
		if(listFiles == null)
		{
			listFiles = new File[]{};
		}
		Collections.addAll(_files, listFiles);
		_fileAdapter = new FileListAdapter(context, android.R.layout.simple_list_item_single_choice, _files);
		lv.setAdapter(_fileAdapter);
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lv.setOnItemClickListener(this);
		
	}
	/**
	 * Returns the selected file for handling.
	 * @return the selected file.
	 */
	public File getSelectedFile()
	{
		return _selectedFile;
	}

	/**
	 * Specifies how a click inside a list should be handled. Changes selected file.
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		_selectedFile  = _files.get(position);
	}

	/**
	 * Specifies how clicks on OK and cancel buttons should be handled. Closes dialog.
	 */
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.dialog_cancel)
		{
			_selectedFile = null;
			dismiss();
		}
		if(_selectedFile != null)
		{
			dismiss();
		}
	}
}
