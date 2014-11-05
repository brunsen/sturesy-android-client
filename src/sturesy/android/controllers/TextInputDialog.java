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

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import de.uhh.sturesy_android.R;
/**
 * Dialog to handle text input from user.
 * @author b.brunsen
 *
 */
public class TextInputDialog extends Dialog implements OnClickListener{

	private EditText _inputText;
	private Button _okButton;
	private String text;
	private boolean _validText;
	
	public TextInputDialog(Context context, String title) {
		super(context);
		setContentView(R.layout.dialog_text_input);
		setTitle(title);
		_inputText = (EditText) findViewById(R.id.dialog_text_input);
		_okButton = (Button) findViewById(R.id.dialog_ok_button);
		_okButton.setOnClickListener(this);
		set_validText(false);
	}

	@Override
	public void onClick(View v) {
		setText(_inputText.getText().toString());
		set_validText(true);
		dismiss();
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setInputText(String text)
	{
		_inputText.setText(text);
	}
	
	public boolean is_validText() {
		return _validText;
	}

	private void set_validText(boolean _validText) {
		this._validText = _validText;
	}

}
