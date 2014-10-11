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
package sturesy.android.controllers.settings;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import sturesy.android.controllers.ErrorDialog;
import sturesy.core.Log;
import sturesy.items.LectureID;
import sturesy.util.BackgroundWorker;
import sturesy.util.Settings;
import sturesy.util.SturesyManager;
import sturesy.util.web.WebCommands;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import de.uhh.sturesy_android.R;

/**
 * 
 * @author b.brunsen
 *
 */
public class InternetSettingsFragment extends Fragment {
	private Settings _settings;
	private Activity _activity;
	private EditText _idTextView;
	private EditText _passwordTextview;
	private EditText _hostTextview;
	private EditText _pollFrequency;
	private TextView _tokenfield;
	private ImageButton _hostCheckButton;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = (LinearLayout) inflater.inflate(
				R.layout.internet_settings_fragment, container, false);
		_settings = SturesyManager.getSettings();
		_activity = getActivity();
		Collection<LectureID> lectureIDs = SturesyManager.getLectureIDs(_activity);
		if (lectureIDs == null)
		{
			lectureIDs = new ArrayList<LectureID>();
		}
		initComponents(view);
		if (!lectureIDs.isEmpty())
		{
			int frequency = _settings.getInteger(Settings.POLL_FREQUENCY);

			if (frequency == -1)
			{
				frequency = 5000;
				_settings.setProperty(Settings.POLL_FREQUENCY, frequency);
			}

			LectureID id = lectureIDs.iterator().next();
			_idTextView.setText(id.getLectureID());
			_passwordTextview.setText(id.getPassword());
			_hostTextview.setText(_settings.getString(Settings.SERVERADDRESS));
			_pollFrequency.setText("" + frequency);
		}
		return view;
	}

	private void initComponents(View view) {
		_idTextView = (EditText) view.findViewById(R.id.edit_lecture_id);
		_passwordTextview = (EditText) view.findViewById(R.id.edit_password);
		_hostTextview = (EditText) view.findViewById(R.id.server_adress);
		_pollFrequency = (EditText) view
				.findViewById(R.id.server_pull_intervall);
		_tokenfield = (TextView) view.findViewById(R.id.edit_token);
		_hostCheckButton = (ImageButton) view.findViewById(R.id.check_server);
	}

	/**
	 * Redeems a given Token and enters valid credentials to the Activity, if
	 * the token is valid.
	 * 
	 * @param v
	 */
	public void redeemToken(View v) {
		String host = _hostTextview.getText().toString();
		if (!host.equals(""))
		{
			_settings.setProperty(Settings.SERVERADDRESS, _hostTextview
					.getText().toString());
			BackgroundWorker redeemer = new BackgroundWorker() {
				boolean error = false;
				String name = "";
				String pw = "";

				@Override
				public void postExecute() {
					if (name.length() < 30)
					{
						_idTextView.setText(name);
						_passwordTextview.setText(pw);
					} else
					{
						error = true;
					}

					if (error)
					{
						Dialog alert = new ErrorDialog(_activity,
								getString(R.string.error),
								getString(R.string.error_tokenredemption));
						alert.show();
					}
					else{
						Toast.makeText(_activity,
								getString(R.string.token_redemption_success),
								Toast.LENGTH_LONG).show();
					}

				}

				@Override
				public void inBackground() {
					String result;
					try
					{
						result = WebCommands.redeemToken(
								_settings.getString(Settings.SERVERADDRESS),
								_tokenfield.getText().toString().trim());
						name = result.split(";")[0];
						pw = result.split(";")[1];
					} catch (Exception e)
					{
						Log.error(e.getMessage(), e.getCause());
						error = true;
					}

				}
			};
			redeemer.execute();
		}
	}

	/**
	 * saves the Settings from the Activity to a File.
	 * 
	 * @param v
	 */
	public void saveSettings(View v) {
		if (_pollFrequency.getText().toString().equals(""))
		{
			_pollFrequency.setText("-1");
		}

		// Save server address for client and host.
		String serveraddress = _hostTextview.getText().toString();
		String clientaddress = serveraddress.replace("relay.php", "index.php");
		_settings.setProperty(Settings.SERVERADDRESS, serveraddress);
		_settings.setProperty(Settings.CLIENTADDRESS, clientaddress);
		_settings.setProperty(Settings.SERVERADDRESS, _hostTextview.getText()
				.toString());
		_settings.setProperty(Settings.POLL_FREQUENCY, _pollFrequency.getText()
				.toString());
		_settings.setProperty(Settings.WEB_PLUGIN_ENABLED, true);
		_settings.save();
		saveLectureID();
		if (v.getId() == R.id.save_and_exit)
		{
			_activity.finish();
		}
	}

	/**
	 * Removes any previous lecture IDs and stores the new lecture ID in a File,
	 * if every required field is set.
	 */
	private void saveLectureID() {
		String id = _idTextView.getText().toString();
		String password = _passwordTextview.getText().toString();
		if (id.length() != 0 && password.length() != 0)
		{

			String host = _settings.getString(Settings.SERVERADDRESS);
			if (host == null || !host.matches("http://.*"))
			{
				String errorTitle = getString(R.string.error);
				String errorMessage = getString(R.string.error_no_valid_host_provided);
				ErrorDialog alert = new ErrorDialog(_activity, errorTitle,
						errorMessage);
				alert.show();
				return;
			}
			Collection<LectureID> lectureIDs = SturesyManager.getLectureIDs(_activity);
			lectureIDs.clear();
			lectureIDs.add(new LectureID(id, password, host));
			SturesyManager.storeLectureIDs((List<LectureID>) lectureIDs, _activity);
			Toast.makeText(_activity.getApplicationContext(),
					getString(R.string.saving_lecture_id_success),
					Toast.LENGTH_SHORT).show();

		} else
		{ // Password or ID empty
			String errorTitle = getString(R.string.error);
			if (id.length() == 0)
			{
				String errorMessage = getString(R.string.error_no_lecture_id_provided);
				ErrorDialog alert = new ErrorDialog(_activity, errorTitle,
						errorMessage);
				alert.show();
			} else
			{
				String errorMessage = getString(R.string.error_no_password_provided);
				ErrorDialog alert = new ErrorDialog(_activity, errorTitle,
						errorMessage);
				alert.show();
			}
		}
	}

	/**
	 * Checks if the server address provided in the settings Activity links to a
	 * valid server.
	 * 
	 * @param v
	 */
	public void checkHost(View v) {
		BackgroundWorker checkHost = new BackgroundWorker() {
			boolean validresult = false;

			public void inBackground() {

				try
				{
					URL url = getServerAdress();

					String result = WebCommands.getInfo(url.toString());
					validresult = result.startsWith("sturesy")
							&& result.length() < 80
							&& result.matches("sturesy [0-9\\.]*");
				} catch (IOException e)
				{
					Log.error(e);
				} catch (Exception e)
				{
					validresult = false;
				}

			}

			@Override
			public void postExecute() {
				if (validresult)
				{
					Toast.makeText(_activity.getApplicationContext(),
							getString(R.string.host_connection_success),
							Toast.LENGTH_SHORT).show();
					_hostCheckButton.setImageResource(R.drawable.green);
				}

				else
				{
					Toast.makeText(_activity.getApplicationContext(),
							getString(R.string.error_host_connection_failed),
							Toast.LENGTH_SHORT).show();
					_hostCheckButton.setImageResource(R.drawable.red);
				}
			}

		};
		checkHost.execute();

	}

	private URL getServerAdress() throws MalformedURLException {
		String url = _hostTextview.getText().toString();

		if (url.startsWith("https://"))
		{
			url = url.replace("https://", "http://");
		}
		if (!url.toLowerCase(Locale.US).startsWith("http://"))
		{
			url = "http://" + url;
		}
		if (!url.endsWith("/") && !url.endsWith(".php"))
		{
			url += "/relay.php";
		}
		if (url.endsWith("/"))
		{
			url += "relay.php";
		}

		return new URL(url);
	}
	
}