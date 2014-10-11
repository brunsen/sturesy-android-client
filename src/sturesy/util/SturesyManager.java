package sturesy.util;

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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.xmlpull.v1.XmlPullParserException;

import sturesy.core.Log;
import sturesy.core.plugin.IPlugin;
import sturesy.core.plugin.IPollPlugin;
import sturesy.items.LectureID;
import sturesy.services.deserialization.LectureIDLoader;
import sturesy.services.serialization.LectureIDPersister;
import sturesy.util.web.WebVotingHandler;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SturesyManager {

	private static final String PLUGINS_FOLDER = "/plugins";
	private static final String LECTURES_FOLDER = "/lectures";

	private static File MAINDIRECTORY;

	private static Settings _settings;
	private static Set<IPlugin> _loadedPlugins = new HashSet<IPlugin>();

	private static Collection<LectureID> _lectureIDs = null;

	public static Settings getSettings() {
		if (_settings == null)
		{
			_settings = Settings.getInstance();
		}

		return _settings;
	}

	public static void setLoadedPlugins(Collection<IPlugin> plugins) {
		_loadedPlugins.addAll(plugins);
	}

	/**
	 * Returns an unmodifiable Set of all the loaded Plugins
	 */
	public static Set<IPlugin> getLoadedPlugins() {
		return Collections.unmodifiableSet(_loadedPlugins);
	}

	/**
	 * Returns a new Set of loaded {@link IPollPlugin}
	 */
	public static Set<IPollPlugin> getLoadedPollPlugins() {
		LinkedHashSet<IPollPlugin> result = new LinkedHashSet<IPollPlugin>();

		if (getSettings().getBoolean(Settings.WEB_PLUGIN_ENABLED))
		{
			result.add((IPollPlugin) new WebVotingHandler());
		}

		for (IPlugin plug : getLoadedPlugins())
		{
			IPollPlugin pollPlugin = plug.getPollPlugin();
			if (pollPlugin != null)
			{
				result.add(pollPlugin);
			}
		}

		return result;
	}

	/**
	 * Returns stored lecture ids.
	 */
	public static Collection<LectureID> getLectureIDs(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"lecture_ids", Context.MODE_PRIVATE);
		if (sharedPreferences.contains("ids"))
		{
			LectureIDLoader loader = new LectureIDLoader();
			String xml = sharedPreferences.getString("ids", "");
			try
			{
				_lectureIDs = loader.loadFromString(xml);
			} catch (XmlPullParserException e)
			{
				Log.error("Error parsing xml content while parsing lids: "
						+ e.getMessage());
			} catch (IOException e)
			{
				Log.error("Error opening lid.xml: " + e.getMessage());
				return new ArrayList<LectureID>();
			}
		} else
		{
			_lectureIDs = new ArrayList<LectureID>();
		}

		return _lectureIDs;
	}

	/**
	 * Stores lecture ids within androids shared preferences.
	 * @param ids
	 * @param context
	 */
	public static void storeLectureIDs(List<LectureID> ids, Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"lecture_ids", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		LectureIDPersister persistor = new LectureIDPersister();
		String xmlIDs = persistor.toXML(ids);
		editor.putString("ids", xmlIDs);
		editor.commit();
	}

	/**
	 * Returns the Main StuReSy Directory
	 * 
	 * @return directory
	 */
	public static File getMainDirectory() {
		return new File(MAINDIRECTORY + "");
	}

	/**
	 * Returns the Directory where all Lectures are saved <br>
	 * <code>/Users/XYZ/maindir/lectures</code>
	 * 
	 * @return directory
	 */
	public static File getLecturesDirectory() {
		File lecturedir = new File(MAINDIRECTORY + LECTURES_FOLDER);
		if (!lecturedir.exists())
		{
			lecturedir.mkdirs();
		}

		return lecturedir;
	}

	/**
	 * The Pluginsdirectory located inside the Maindirectory
	 */
	public static File getPluginsDirectory() {
		File plugdir = new File(MAINDIRECTORY + PLUGINS_FOLDER);
		if (!plugdir.exists())
		{
			plugdir.mkdirs();
		}
		return plugdir;
	}

	/**
	 * The Pluginsdirectory located next to the sturesy.jar
	 */
	public static File getInternalPluginsDirectory() {
		File plugdir = new File(Folder.getBaseFolder(), PLUGINS_FOLDER);
		if (!plugdir.exists())
		{
			plugdir.mkdir();
		}
		return plugdir;
	}

	public synchronized static void setMainDirectory(String path) {
		MAINDIRECTORY = new File(path);
	}

}
