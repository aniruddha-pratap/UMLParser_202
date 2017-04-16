package UMLJavaParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
public class ConnectyUML {

	public static void connectToYUml(String passedCode, String imagePath) {

		HttpURLConnection connection = null;
		OutputStream outputStream = null;
		
        try {
            String link = "http://yuml.me/diagram/plain/class/" + passedCode
                    + ".png";
            URL connectUrl = new URL(link);
            connection = (HttpURLConnection) connectUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            if (connection.getResponseCode() != 200) {
                throw new RuntimeException(
                        "Failed : HTTP error code : " + connection.getResponseCode());
            }
            outputStream = new FileOutputStream(new File(imagePath));
            byte[] readBytes = new byte[1024];
			while(connection.getInputStream().read(readBytes) != -1){
				outputStream.write(readBytes);
            }
			outputStream.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        finally{
        	connection.disconnect();
        	
        }
    }
	
}
