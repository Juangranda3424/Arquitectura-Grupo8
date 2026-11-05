package ec.edu.monster.service;

import java.io.IOException;

import ec.edu.monster.util.SoapHelper;

public class LoginService {

    /**
     * Validates the credentials against WSLogin.auth.
     *
     * @return true when the server answers {@code <return>true</return>}, false when it rejects them
     * @throws IOException when the server cannot be reached, so the UI does not report
     *                     a connection problem as wrong credentials
     */
    public boolean auth(String username, String password) throws IOException {
        String soapBody = "<username>" + SoapHelper.escapeXml(username) + "</username>" +
                "<password>" + SoapHelper.escapeXml(password) + "</password>";

        String response = SoapHelper.callWebService("WSLogin", "auth", soapBody);

        return response.contains("<return>true</return>") ||
                response.contains("<ns2:return>true</ns2:return>");
    }
}
