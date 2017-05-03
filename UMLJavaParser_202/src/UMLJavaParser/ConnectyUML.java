package UMLJavaParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
public class ConnectyUML {

	private HttpURLConnection connection;
	private OutputStream oStream;
	private InputStream iStream;
	private int oCalc = -1;
	
	public void connectToYUml(String passedCode, String outputName) throws Exception{
		try {
			outputName = outputName + ".png";
			connection = null;oStream= null;
		    String bsURL = "https://yuml.me/diagram/plain/class/" + passedCode;
            URL connectUrl = new URL(bsURL);
            connection = (HttpURLConnection) connectUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            if (connection.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
            }
            oStream = new FileOutputStream(new File(outputName));
            iStream = connection.getInputStream();
            byte[] readBytes = new byte[4096];
			while((oCalc=iStream.read(readBytes)) != -1){
				oStream.write(readBytes, 0, oCalc);
            }
			oStream.close();
			iStream.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        finally{
        	connection.disconnect();
        	
        }
    }
	
}
