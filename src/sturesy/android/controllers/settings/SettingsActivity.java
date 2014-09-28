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

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import de.uhh.sturesy_android.R;
/**
 * 
 * @author b.brunsen
 *
 */
public class SettingsActivity extends Activity {

	private FragmentManager _manager;
	private Fragment _fragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		_fragment = new InternetSettingsFragment();
		_manager = getFragmentManager();
		FragmentTransaction transaction = _manager.beginTransaction();
		transaction.add(R.id.settings_content, _fragment);
		transaction.commit();
	}

	/**
	 * Switches the Fragment in the detail view to show the copyright.
	 * 
	 * @param view
	 */
	public void showCopryright(View v) {
		if (!(_fragment instanceof AboutFragment)) {
			_fragment = new AboutFragment();
			FragmentTransaction transaction = _manager.beginTransaction();
			transaction.replace(R.id.settings_content, _fragment);
			transaction.commit();
		}
	}

	/**
	 * Switches the Fragment in the detail view to show the internet settings.
	 * 
	 * @param view
	 */
	public void showInternetSettings(View v) {
		if (!(_fragment instanceof InternetSettingsFragment)) {
			_fragment = new InternetSettingsFragment();
			FragmentTransaction transaction = _manager.beginTransaction();
			transaction.replace(R.id.settings_content, _fragment);
			transaction.commit();
		}
	}

	public void saveSettings(View v) {
		if (_fragment instanceof InternetSettingsFragment) {
			((InternetSettingsFragment) _fragment).saveSettings(v);
		}
	}

	public void checkServer(View v) {
		if(_fragment instanceof InternetSettingsFragment)
		{
			((InternetSettingsFragment) _fragment).checkHost(v);
		}
	}
	
	public void redeemToken(View v)
	{
		if(_fragment instanceof InternetSettingsFragment)
		{
			((InternetSettingsFragment) _fragment).redeemToken(v);
		}
	}

}
