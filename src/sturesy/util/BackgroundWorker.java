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

import android.os.AsyncTask;

/**
 * An extension of the {@link AsyncTask} to perform lengthy GUI-interacting
 * tasks in a dedicated background thread.<br>
 * 
 * <br>
 * Usage:<br>
 * <code>BackgroundWorker b = new BackgroundWorker(){ public void inBackground() { ... }}<br>
        b.execute();</code>
 * 
 * @author w.posdorfer, b.brunsen
 */
//public abstract class BackgroundWorker extends AsyncTask<Object, Object,Object>
public abstract  class BackgroundWorker extends AsyncTask<Object, Object, Object>
{

	@Override
	protected Object doInBackground(Object... params) {
		inBackground();
		return null;
	}

    /**
     * This method will be executed in a background thread. It will only be
     * called once
     */
    public abstract void inBackground();

    /**
     * Checks if the state of this BackgroundWorker is PENDING
     * 
     * @return true if pending, otherwise false
     */
    public boolean isPending()
    {
        return getStatus() == Status.PENDING;
    }

    @Override
    protected void onPostExecute (Object result)
    {
    	postExecute();
    }
    
    /**
     * This method will be called whenever the Backgroundworker needs to change UI elements on android.
     * Needs to be called for UI changes, otherwise application crashes.
     */
    public abstract void postExecute();
}
