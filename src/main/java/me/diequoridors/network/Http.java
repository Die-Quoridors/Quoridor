package me.diequoridors.network;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Http {

    public static String getRequest(String serverAddress, String path) throws IOException, URISyntaxException {
        URL url = new URI("http://" + serverAddress + path).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        connection.setUseCaches(false);
        connection.setDoOutput(false);

        return getHttpResponse(connection);
    }

    public static String postRequest(String serverAddress, String path, String dataToSend) throws IOException, URISyntaxException {
        URL url = new URI("http://" + serverAddress + path).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Content-Length", String.valueOf(dataToSend.length()));

        connection.setUseCaches(false);
        connection.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream (
                connection.getOutputStream());
        wr.writeBytes(dataToSend);
        wr.close();

        return getHttpResponse(connection);
    }

    private static String getHttpResponse(HttpURLConnection connection) throws IOException {
        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        rd.close();
        return response.toString();
    }
}
