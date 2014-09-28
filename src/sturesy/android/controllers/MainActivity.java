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

import sturesy.android.controllers.qgen.QuestionGeneratorActivity;
import sturesy.android.controllers.settings.SettingsActivity;
import sturesy.android.controllers.voting.VotingActivity;
import sturesy.android.controllers.votinganalysis.VotingAnalysisActivity;
import sturesy.util.Startup;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import de.uhh.sturesy_android.R;
/**
 * 
 * @author b.brunsen
 *
 */
public class MainActivity extends Activity implements OnClickListener {
	private TextView _openVoting;
	private TextView _openVotingAnalysis;
	private TextView _openQuestionGenerator;
	private TextView _openSettings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Startup.initiate();
		// Required to use Webcommands for android versions above 9
		if (android.os.Build.VERSION.SDK_INT > 9)
		{
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		initComponents();
		registerListeners();

	}

	/**
	 * Registers listeners on gui components. Listeners will be called once a
	 * user interacts with them.
	 */
	private void registerListeners() {
		_openVoting.setOnClickListener(this);
		_openVotingAnalysis.setOnClickListener(this);
		_openQuestionGenerator.setOnClickListener(this);
		_openSettings.setOnClickListener(this);
	}

	/**
	 * Initializes components.
	 */
	private void initComponents() {
		_openVoting = (TextView) findViewById(R.id.presentation_textView);
		_openVotingAnalysis = (TextView) findViewById(R.id.evaluation_textView);
		_openQuestionGenerator = (TextView) findViewById(R.id.question_gen_textView);
		_openSettings = (TextView) findViewById(R.id.setting_textView);
	}

	@Override
	public void onClick(View v) {
		Intent i = new Intent();
		switch (v.getId()) {
		case R.id.presentation_textView:
			i.setClass(this, VotingActivity.class);
			break;
		case R.id.evaluation_textView:
			i.setClass(this, VotingAnalysisActivity.class);
			break;
		case R.id.question_gen_textView:
			i.setClass(this, QuestionGeneratorActivity.class);
			break;
		case R.id.setting_textView:
			i.setClass(this, SettingsActivity.class);
			break;
		}
		if (i.getClass() != null)
		{
			startActivity(i);
		}
	}

}
