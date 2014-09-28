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
package sturesy.services.deserialization;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import sturesy.core.backend.services.XMLEscapeService;
import sturesy.items.LectureID;
/**
 * 
 * @author b.brunsen
 *
 */
public class LectureIDLoader {
	
	public List<LectureID> loadFromFile(String fileName) throws XmlPullParserException, IOException
	{
		List <LectureID> ids = new ArrayList<LectureID>();
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();
        
        FileReader reader = new FileReader(new File(fileName));
        parser.setInput(reader);

        processXML(parser, ids);
        reader.close();
		return ids;
	}

	private void processXML(XmlPullParser xpp, List<LectureID> ids) throws XmlPullParserException, IOException {
		String id = "";
		String password = "";
		String url = "";
		int eventType;
		do
        {
        	String lastStartTag;
        	 eventType = xpp.getEventType(); 
            if (eventType == XmlPullParser.START_TAG)
            {
                // process start
                lastStartTag = xpp.getName();

                if (lastStartTag.equals("id"))
                {
                    id = XMLEscapeService.unescapeString(xpp.nextText());
                }
                
                if(lastStartTag.equals("pw"))
                {
                	password = xpp.nextText();
                }
                
                if(lastStartTag.equals("host"))
                {
                	url = xpp.nextText();
                }
            }
            else if (eventType == XmlPullParser.END_TAG)
            {
                // process end

                if (xpp.getName().equals("lectureid"))
                {
                	ids.add(new LectureID(id, password, url, true));
                	id = "";
                	password = "";
                	url = "";
                }
            }
            eventType = xpp.next();
        }
        while (eventType != XmlPullParser.END_DOCUMENT);
	}
}