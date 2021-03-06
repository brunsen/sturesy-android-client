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
package sturesy.util;


/**
 * A JLabel-extension which makes use of the HTML-Functions integrated in the
 * JLabel, by appending "&lt;html&gt;" at the beginning and end of each String
 * 
 * @author w.posdorfer
 * 
 */
public class HTMLLabel
{

    private static final String HTML_CENTER_START = "<center>";
    private static final String HTML_CENTER_END = "</center>";

    private static final String HTML_START = "<html>";
    private static final String HTML_END = "</html>";
    
    private static final String HTML_BODY_START = "<body>";
    private static final String HTML_BODY_END = "</body>";
    /**
     * Creates an empty Label
     */
    public HTMLLabel()
    {
    }

    /**
     * Creates a Label showing text
     * 
     * @param text
     *            Text to be wrapped in &lt;html&gt;-Tags, if not already
     */
    public HTMLLabel(String text)
    {
        generateHTLMString(text);
    }

    public static String generateHTLMString(String text)
    {
        if (text == null || (text.startsWith(HTML_START) && text.endsWith(HTML_END)))
        {
            return text;
        }
        else
        {
            return HTML_START + HTML_BODY_START + HTML_CENTER_START + text + HTML_CENTER_END + HTML_BODY_END + HTML_END;
        }

    }

    public void setText(String text)
    {
        generateHTLMString(text);
    }
}
