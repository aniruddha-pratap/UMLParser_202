package UMLJavaParser;

import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectyUML {

	public static void connectToYUml(String passedCode) {

        try {
            String link = "http://yuml.me/diagram/plain/class/" + passedCode
                    + ".png";
            URL connectUrl = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) connectUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            if (connection.getResponseCode() != 200) {
                throw new RuntimeException(
                        "Failed : HTTP error code : " + connection.getResponseCode());
            }
            connection.disconnect();
        } catch (Exception e) {
            System.out.println(e);
        } 
    }
	
}
