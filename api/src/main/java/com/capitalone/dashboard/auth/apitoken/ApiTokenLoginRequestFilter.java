package com.capitalone.dashboard.auth.apitoken;

import com.capitalone.dashboard.auth.AuthenticationResultHandler;
import com.capitalone.dashboard.model.AuthType;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ApiTokenLoginRequestFilter extends AbstractAuthenticationProcessingFilter {

    public ApiTokenLoginRequestFilter() {
        super(new AntPathRequestMatcher("/login/apitoken", "POST"));
    }

    public ApiTokenLoginRequestFilter(String path, AuthenticationManager authManager, AuthenticationResultHandler authenticationResultHandler) {
        this();
        setAuthenticationManager(authManager);
        setAuthenticationSuccessHandler(authenticationResultHandler);
        setFilterProcessesUrl(path);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        String apiUser = request.getHeader("apiUser");
        String apikey = "";

        String authHeader = request.getHeader("Authorization");
        String encodedAuthStr = authHeader.substring(authHeader.indexOf(" "), authHeader.length());
        byte[] encodedAuthbytes = encodedAuthStr.getBytes();
        String decodedAuthStr = new String(Base64.decodeBase64(encodedAuthbytes));
        String decodedAuthJson = decodedAuthStr.substring(decodedAuthStr.indexOf(":") + 1, decodedAuthStr.length());

        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(decodedAuthJson);
            apikey = (String) jsonObject.get("apiKey");
        } catch (ParseException e) {
            throw new AuthenticationServiceException("Unable to parse apikey token.");
        }

        ApiTokenAuthenticationToken authRequest = new ApiTokenAuthenticationToken(apiUser, apikey);

        authRequest.setDetails(AuthType.APIKEY);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

}

