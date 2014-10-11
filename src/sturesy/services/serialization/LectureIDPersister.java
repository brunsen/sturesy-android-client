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
package sturesy.services.serialization;

import java.util.List;

import sturesy.core.Log;
import sturesy.items.LectureID;
import sturesy.util.Crypt;
/**
 * 
 * @author b.brunsen
 *
 */
public class LectureIDPersister {
	
	/**
	 * Writes a list of LectureID objects into a xml String.
	 * @param lectureIds list of ids to be saved.
	 * @return xml string containing data of all LectureID objects.
	 */
    public String toXML(List<LectureID> lectureIds)
    {
        StringBuilder builder = new StringBuilder();
        for (LectureID lectureID : lectureIds) {
        	try
        	{
        		builder.append("<lectureid>").append("\n");
        		builder.append("<id>").append(lectureID.getLectureID()).append("</id>").append("\n");
        		builder.append("<pw>").append(Crypt.encrypt(lectureID.getPassword())).append("</pw>").append("\n");
        		builder.append("<host>").append(lectureID.getHost().toString()).append("</host>").append("\n");
        		builder.append("</lectureid>").append("\n");
        	}
        	catch (Exception e)
        	{
        		Log.error("Error generating XML for LectureID", e);
        	}
		}

        return builder.toString();
    }
}
