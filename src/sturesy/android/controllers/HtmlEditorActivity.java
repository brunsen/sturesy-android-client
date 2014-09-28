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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import de.uhh.sturesy_android.R;
/**
 * 
 * @author b.brunsen
 *
 */
public class HtmlEditorActivity extends Activity {

	private int _position;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_html_editor);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setPosition(getIntent().getExtras().getInt("Position"));
		String text = "";
		if (getPosition() == -1) {
			text = getIntent().getExtras().getString("Question");
		} else {
			text = getIntent().getExtras().getString("Answer");
		}
		EditText edit = (EditText) findViewById(R.id.editorText);
		edit.setText(text);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.html_editor, menu);
		return true;
	}

	public void saveText(View view) {
		view.getParent();
		Intent data = new Intent();
		EditText editor = (EditText) findViewById(R.id.editorText);
		Bundle extras = new Bundle();
		String text = editor.getText().toString();
		if (_position == -1) {
			extras.putString("Question", text);
			extras.putInt("Position", getIntent().getExtras()
					.getInt("Position"));
			data.putExtras(extras);
		} else {
			extras.putString("Answer", text);
			extras.putInt("Position", getIntent().getExtras()
					.getInt("Position"));
			data.putExtras(extras);
		}

		if (getParent() == null) {
			setResult(Activity.RESULT_OK, data);
		} else {
			getParent().setResult(Activity.RESULT_OK, data);
		}
		finish();
	}

	public int getPosition() {
		return _position;
	}

	public void setPosition(int _position) {
		this._position = _position;
	}

}
