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
package sturesy.core.backend;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import sturesy.core.Log;
import android.content.Context;

/**
 * Standard Classloader to use
 * 
 * @author w.posdorfer
 * 
 */
public class Loader
{

    public static final String CONTRIBUTORS_TEXT = "documents/contributors.txt";
    public static final String LICENSE_HTML = "documents/agpl.html";

    public static final ClassLoader cl = Loader.class.getClassLoader();

    

    /**
     * Retrieves a File
     * 
     * @param name
     * @return
     */
    public static File getFile(String name, Context c)
    {
        try
        {
            final URL url = cl.getResource(name);
            return new File(url.toURI());
        }
        catch (Exception ex)
        {
            Log.error("error getting File:" + name, ex);
        }
        return null;
    }

    /**
     * Returns a Resource as InputStream
     * 
     * @param name
     *            Name of resource
     * @return InputStream containing the resource
     */
    public static InputStream getResourceAsStream(String name, Context c)
    {
        return cl.getResourceAsStream(name);
    }
}