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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import sturesy.util.HTMLLabel;
import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import de.uhh.sturesy_android.R;
/**
 * 
 * @author b.brunsen
 *
 */
public class AboutFragment extends Fragment {
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	/**
	 * Initiates the fragment and inflates the layout.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Activity activity = getActivity();
		View view = inflater.inflate(R.layout.about_fragment, container, false);
		WebView contentWebView = (WebView) view.findViewById(R.id.about_content);
		String version = "";
		PackageInfo pInfo;
		try {
			pInfo = activity.getPackageManager().getPackageInfo(
					activity.getPackageName(), 0);
			version = pInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		String info = "StuReSy - Version " + version
				+ "<br> &#169 2012-2014 StuReSy-Team </br> </br>";
		contentWebView.loadData(HTMLLabel.generateHTLMString(info + getContributors() + getLicense()), "text/html", "UTF-8");
		return view;
	}

	public String getContributors() {
		String contributers = "";
		try {
			InputStream input = getActivity().getAssets().open(
					"documents/contributors.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				total.append(line);
			}
			contributers += total.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return contributers;
	}
	
	public String getLicense()
	{
		String agpl = "";
		try {
			InputStream input = getActivity().getAssets().open(
					"documents/agpl.html");
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				total.append(line);
			}
			agpl += total.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return agpl;
	}
}
