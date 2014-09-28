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

import java.util.List;

import sturesy.items.QuestionModel;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import de.uhh.sturesy_android.R;
/**
 *
 * @author b.brunsen
 *
 */
public class QuestionListAdapter extends ArrayAdapter<QuestionModel> {

	private List<QuestionModel> _items;
	private int _layoutResourceId;
	private Context _context;

	public QuestionListAdapter(Context context, int resource, List<QuestionModel> objects) {
		super(context,resource,objects);
		_layoutResourceId = R.layout.question_list_item;
		_context = context;
		_items = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int currentPosition = position;
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = ((Activity) _context).getLayoutInflater();
			row = inflater.inflate(_layoutResourceId, parent, false);
		}
		String text = _items.get(position).getQuestion();
		TextView answerTextView = (TextView) row.findViewById(R.id.QuestionText);
		if (text != null) {
			if (answerTextView != null) {
				answerTextView.setText((CharSequence) text);
			}
		}

		ImageButton up = (ImageButton) row.findViewById(R.id.questionUp);
		up.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				swapItems(currentPosition, -1, v);
				notifyDataSetChanged();

			}
		});

		ImageButton down = (ImageButton) row.findViewById(R.id.questionDown);
		down.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				swapItems(currentPosition, 1,v);
				notifyDataSetChanged();

			}
		});
		return row;
	}

	/**
	 * Method to swap elements in the list in a specific direction.
	 * 
	 * @param index
	 *            index inside the list
	 * @param direction
	 *            -1 up; 1 down
	 */
	public void swapItems(int index, int direction, View v) {
		if ((index != 0 && direction == -1)
				|| (index != _items.size() - 1 && direction == 1)) {
			_items.add(index + direction, _items.remove(index));
			Activity act = ((Activity)v.getContext());
			if(act instanceof QuestionGeneratorActivity)
			{
//				((QuestionGeneratorActivity) act).getCurrentQuestionSet().swapElements(index, index + direction);
				((QuestionGeneratorActivity) act).swapQuestions(index, index + direction);
			}
		}
	}
	
}