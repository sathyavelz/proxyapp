package com.request.proxy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@RequestMapping("/proxy")//NO I18N
public class RequestRedirect {

	@RequestMapping(method = RequestMethod.GET)
	public void rediredtRequest(HttpServletResponse response , @RequestParam("header") String headers, @RequestParam("url") String url, @RequestParam("requestType") String requestType,@RequestParam("body") String reqBody,@RequestParam("clientId") String clientId) {
		
		try {
			URL req = new URL(url);
			HttpURLConnection con = (HttpURLConnection) req.openConnection();
			con.setRequestMethod(requestType);
			JSONObject header = new JSONObject(headers.toString());
			Iterator<String> keys = header.keys();
			while(keys.hasNext()){
				String key = keys.next();
				con.addRequestProperty(key, header.get(key).toString());
			}
			con.setDoOutput(true);
			try(OutputStream os = con.getOutputStream()){
				byte[] input = reqBody.getBytes("utf-8");
				os.write(input, 0, input.length);	
			}
			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);
			int code = con.getResponseCode();
			response.setStatus(code);
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			response.getWriter().write(content.toString());
			response.setContentType(con.getContentType());

		}
		catch(Exception e){
			
		}
		
	}
}
