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
package sturesy.android.controllers.qgen;

import java.util.ArrayList;
import java.util.List;

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
public class SelectQuestionDialog extends Dialog implements OnItemClickListener, OnClickListener{
	
	private int choice;
	
	public SelectQuestionDialog(Context context) {
		super(context);
		setContentView(R.layout.dialog_user_choice);
		choice = -1;
		String title = context.getString(R.string.title_question_selection);
		setTitle(title);
		List<String> options = new ArrayList<String>();
		options.add(context.getString(R.string.single_choice));
		options.add(context.getString(R.string.multiple_choice));
		options.add(context.getString(R.string.text_choice));
		Button okButton = (Button) findViewById(R.id.dialog_ok);
		Button cancelButton = (Button) findViewById(R.id.dialog_cancel);
		ListView lv = (ListView) findViewById(R.id.dialog_choice_list);
		okButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice, options);
		lv.setAdapter(adapter);
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lv.setOnItemClickListener(this);
	}

	public int getChoice()
	{
		return choice;
	}
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.dialog_ok){
			dismiss();
		}
		else{
			choice = -1;
			dismiss();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		choice = position;
	}

}
