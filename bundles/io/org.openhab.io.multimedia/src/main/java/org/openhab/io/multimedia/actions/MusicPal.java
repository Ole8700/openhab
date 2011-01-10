/**
 * openHAB, the open Home Automation Bus.
 * Copyright (C) 2010, openHAB.org <admin@openhab.org>
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or
 * combining it with Eclipse (or a modified version of that library),
 * containing parts covered by the terms of the Eclipse Public License
 * (EPL), the licensors of this Program grant you additional permission
 * to convey the resulting work.
 */

package org.openhab.io.multimedia.actions;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Dictionary;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.eclipse.persistence.oxm.annotations.XmlPath;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Action which controls a MusicPal Streaming Radio (http://www.freecom.de). The
 * development of this Action is heavily inspired by the php-development of Andre
 * (a.k.a "jonofe") at KNX-User-Forum. Many thanks for that ...
 * 
 * @author Thomas.Eichstaedt-Engelen
 * @see http://knx-user-forum.de/knx-eib-forum/5086-musicpal-als-kleine-leute-visu-2.html#post84623
 */
public class MusicPal implements ManagedService {

	private static final Logger logger = LoggerFactory.getLogger(MusicPal.class);
	
	private static boolean initialized = false;
	
	private static String hostname;
	private static String username;
	private static String password;

    private static String urlGetState = "/admin/cgi-bin/state.cgi?fav=0";
    private static String urlPowerUp = "/admin/cgi-bin/ipc_send?power_up";
    private static String urlPowerDown = "/admin/cgi-bin/ipc_send?power_down";
    private static String urlChangeVolume = "/admin/cgi-bin/admin.cgi?f=volume_set&v=";
    private static String urlPlayPause = "/admin/cgi-bin/admin.cgi?f=play_pause";
    private static String urlSetFavorite = "/admin/cgi-bin/admin.cgi?f=favorites&n=../favorites.html&a=p&i=";
    
    private static String MP_ON = "1";
    private static String MP_PLAYING = "1";
    
    private static String STATE_ON = "ON";
    
	
    /**
	 * switches the musicpal on from standby 
     */
    public static void power(String powerState) {
    	
    	if (STATE_ON.equals(powerState)) {
    		powerUp();
    	}
    	else {
    		powerDown();
    	}
    }
    
    /**
	 * switches the musicpal on from standby 
     */
    public static void powerUp() {
        execute("http://" + hostname + urlPowerUp, username, password);
    }
    
    /**
	 * switches the musicpal down to standby 
     */
    public static void powerDown() {
        execute("http://" + hostname + urlPowerDown, username, password);
    }
    
    /**
     * increase volume by one step (which means 5%)
     */
    public static void volumnUp() {
        int currentVolume = getVolume(hostname, username, password);
        int newVolume = currentVolume + 1;
        execute("http://" + hostname + urlChangeVolume + newVolume, username, password);
    }
    
    /**
     * decrease volume by one step (which means 5%)
     */
    public static void volumnDown() {
        int currentVolume = getVolume(hostname, username, password);
        int newVolume = currentVolume - 1;
        execute("http://" + hostname + urlChangeVolume + newVolume, username, password);
    }

    /**
     * pause the current playback
     */
    public static void pause() {
        execute("http://" + hostname + urlPlayPause, username, password);
    }
    
    private static boolean isOn(String mpDomain, String username, String password) {
    	MusicPalState state = getState(mpDomain, username, password);
    	if (state != null) {
    		return MP_ON.equals(state.powerState);
    	}
    	else {
    		return false;
    	}
    }
    
    private static boolean isPlaying(String mpDomain, String username, String password) {
    	MusicPalState state = getState(mpDomain, username, password);
    	if (state != null) {
    		return MP_PLAYING.equals(state.playerState);
    	}
    	else {
    		return false;
    	}
    }
    
    private static int getVolume(String mpDomain, String username, String password) {
    	MusicPalState state = getState(mpDomain, username, password);
    	if (state != null) {
    		return Integer.parseInt(state.volume);
    	}
    	return 5;
    }
    	    

    /**
     * Excutes the given <code>url</code> as GET-Request using the 
     * <code>user</code> and <code>password</code> for HTTP authentication.
     * 
     * @param url the url to execute
     * @param username
     * @param password
     */
    private static void execute(String url, String username, String password) {
    	
    	if (initialized) {
    		
	        HttpClient client = new HttpClient();
	        GetMethod method = new GetMethod(url);
	        
	        // Provide custom retry handler is necessary
	        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
	        		new DefaultHttpMethodRetryHandler(3, false));
	
	        Credentials credentials = 
	        	new UsernamePasswordCredentials(username, password);
	        client.getState().setCredentials(AuthScope.ANY, credentials);
	        
	        logger.debug("execute:" + method);
	        
	        try {
	        	
	          int statusCode = client.executeMethod(method);
	
	          if (statusCode != HttpStatus.SC_OK) {
	        	  logger.warn("Method failed: " + method.getStatusLine());
	          }
	
	          byte[] responseBody = method.getResponseBody();
	          
	          if (responseBody.length > 0) {
	        	  logger.debug(new String(responseBody));
	          }
	
	        } 
	        catch (HttpException he) {
	        	logger.error("Fatal protocol violation: ", he);
	        }
	        catch (IOException e) {
	        	logger.error("Fatal transport error: ", e);
	        }
	        finally {
	          method.releaseConnection();
	        }  
    	}    	
    }
    
    /**
     * Reads the current state of the MusicPal. The state is represented as a
     * XML-document.
     * 
     * @param hostname
     * @param username
     * @param password
     * 
     * @return the current state object
     */
    private static MusicPalState getState(String hostname, String username, String password) {

    	MusicPalState state = null;
    	
		try {

		    JAXBContext jc = JAXBContext.newInstance(MusicPalState.class);
		    Unmarshaller unmarshaller = jc.createUnmarshaller();
		    
		    URL xmlURL;
				xmlURL = new URL("http://" + hostname + urlGetState);
		    InputStream xml = xmlURL.openStream();
		    
		    state = (MusicPalState) unmarshaller.unmarshal(xml);
		    
		    logger.debug("current state is: " + state);
		    
		    xml.close();
		}   
		catch (Exception e) {
			logger.error("getState throws exception", e);
		}
        
        return state;
	}
    
    /**
     * The Java-Binding of the corresponding XML state
     * 
     * @author Thomas.Eichstaedt-Engelen
     */
	@XmlRootElement(name="state")
	class MusicPalState {
		
	    @XmlPath("now_playing/text()")
		String nowPlaying;
	    @XmlPath("position/text()")
		String position;
	    @XmlPath("length/text()")
		String length;
	    @XmlPath("volume/text()")
		String volume;
	    @XmlPath("player_state/text()")
		String playerState;
	    @XmlPath("power_state/text()")
		String powerState;
	    @XmlPath("url/text()")
		String url;
	    @XmlPath("stream_url/text()")
		String streamUrl;
	    
		@Override
		public String toString() {
			return "MusicPalState [nowPlaying=" + nowPlaying + ", position="
					+ position + ", length=" + length + ", volume=" + volume
					+ ", playerState=" + playerState + ", powerState="
					+ powerState + ", url=" + url + ", streamUrl=" + streamUrl
					+ "]";
		}
	    
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void updated(Dictionary config) throws ConfigurationException {
		
		if (config!=null) {

			MusicPal.hostname = (String) config.get("hostname");
			MusicPal.username = (String) config.get("username");
			MusicPal.password = (String) config.get("password");
			
			String urlGetStateString = (String) config.get("urlgetstate");
			if (urlGetStateString != null) {
				MusicPal.urlGetState = urlGetStateString;
			}
			
			String urlPowerUpString = (String) config.get("urlpowerup");
			if (urlPowerUpString != null) {
				MusicPal.urlPowerUp = urlPowerUpString;
			}

			String urlPowerDownString = (String) config.get("urlpowerdown");
			if (urlPowerDownString != null) {
				MusicPal.urlPowerDown = urlPowerDownString;
			}

			String urlChangeVolumeString = (String) config.get("urlchangevolume");
			if (urlChangeVolumeString != null) {
				MusicPal.urlChangeVolume = urlChangeVolumeString;
			}

			String urlPlayPauseString = (String) config.get("urlplaypause");
			if (urlPlayPauseString != null) {
				MusicPal.urlPlayPause = urlPlayPauseString;
			}

			String urlSetFavoriteString = (String) config.get("urlsetfavorite");
			if (urlSetFavoriteString != null) {
				MusicPal.urlSetFavorite = urlSetFavoriteString;
			}
			
			// check mandatory settings
			if(hostname == null || hostname.isEmpty()) return;
			if(username == null || username.isEmpty()) return;
			if(password == null || password.isEmpty()) return;
			
			initialized = true;
		}
	}
	
	
}
