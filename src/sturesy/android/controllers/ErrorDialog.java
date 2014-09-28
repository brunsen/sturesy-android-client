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
import android.widget.Button;
import android.widget.TextView;
import de.uhh.sturesy_android.R;
/**
 * Simple dialog to display a title and message
 * @author b.brunsen
 *
 */
public class ErrorDialog extends Dialog implements
android.view.View.OnClickListener{
	public ErrorDialog(Context context, String title, String message) {
		super(context);
		setContentView(R.layout.dialog_error);
		setTitle(title);
		TextView tv = (TextView)findViewById(R.id.dialogue_error_message);
		tv.setText(message);
		Button button = (Button)findViewById(R.id.dialogue_error_button);
		button.setText(R.string.OK);
		button.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		dismiss();
	}

}
