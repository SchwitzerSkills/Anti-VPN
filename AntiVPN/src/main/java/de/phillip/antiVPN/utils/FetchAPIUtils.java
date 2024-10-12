package de.phillip.antiVPN.utils;


import de.phillip.antiVPN.AntiVPN;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


import static org.bukkit.Bukkit.getLogger;

public class FetchAPIUtils {

    public void makeAPIRequest(String ip, String playerName) {
        boolean activeAntiVPN = AntiVPN.getInstance().getConfig().getBoolean("active");
        boolean logger = AntiVPN.getInstance().getConfig().getBoolean("logger");

        if(activeAntiVPN) {

            List<String> allowVPNIPS = AntiVPN.getInstance().getConfig().getStringList("allowVPNIPS");

            for (String allowVPNIP : allowVPNIPS) {
                if (ip.equalsIgnoreCase(allowVPNIP)) {
                    if(logger){
                        getLogger().info("Allowed IP in Config detected!");
                    }
                    return;
                }
            }

            try {
                URL url = new URL("https://proxycheck.io/v2/" + ip + "?vpn=1&asn=1");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder response = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    String apiResponse = response.toString();
                    if(logger){
                        getLogger().info("API Response: " + apiResponse);
                    }

                    String isProxy = extractValue(apiResponse, "proxy");
                    String connectionType = extractValue(apiResponse, "type");

                    if(logger){
                        getLogger().info("Is Proxy: " + isProxy);
                        getLogger().info("Connection Type: " + connectionType);
                    }

                    if ("yes".equalsIgnoreCase(isProxy) && "VPN".equalsIgnoreCase(connectionType)) {
                        if(logger){
                            getLogger().warning("This connection is using a VPN proxy!");
                        }

                        String kickMessage = AntiVPN.getInstance().getConfig().getString("KickMessage");
                        kickMessage = kickMessage.replace("&", "§");
                        kickMessage = kickMessage.replace("%player%", playerName);

                        String kickCommand = AntiVPN.getInstance().getConfig().getString("KickCommand");
                        kickCommand = kickCommand.replace("/", "");
                        kickCommand = kickCommand.replace("%player%", playerName);
                        kickCommand = kickCommand.replace("%KickMessage%", kickMessage);

                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), kickCommand);
                    }
                } else {
                    if(logger){
                        getLogger().warning("API request failed. Response Code: " + responseCode);
                    }
                }

                connection.disconnect();
            } catch (Exception e) {
                if(logger){
                    getLogger().severe("Error making API request: " + e.getMessage());
                }
                e.printStackTrace();
            }
        }
    }

    private String extractValue(String json, String key) {
        int keyIndex = json.indexOf("\"" + key + "\"");
        if (keyIndex != -1) {
            int valueStart = json.indexOf(":", keyIndex) + 1;
            int valueEnd = json.indexOf(",", valueStart);
            if (valueEnd == -1) {
                valueEnd = json.indexOf("}", valueStart);
            }
            if (valueEnd != -1) {
                return json.substring(valueStart, valueEnd).trim().replace("\"", "");
            }
        }
        return "";
    }
}
