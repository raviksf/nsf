package org.example;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.utils.URIBuilder;
import java.net.URI;

import java.io.BufferedReader;
import java.io.FileReader;




public class BitcoinPrice {

    private static String svcHost = "192.168.99.100";
    private static int svcPort = 5000;

    private static String[] getVars() {

        String vars[] = new String[2];

        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader("/var/lib/tomcat7/hello.conf"));
            String line = br.readLine();
            if (line != null)
                vars[0] = line;
            line = br.readLine();
            if (line != null)
                vars[1] = line;
        } catch (Exception e) {
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (Exception e) {
            }
        }
        
        vars[0] = "xx=blockchain.info/tickerm";
        vars[1] = "yy=80";

        for (int ind = 0; ind < 2; ind++) {
            if (vars[ind] != null) {
                int i = vars[ind].lastIndexOf('=');
                if (i > 0)
                    vars[ind]= vars[ind].substring(i + 1);
            }
        }
        return vars;
    }

    public static String requestPrice() throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        String vars[] = getVars();

        String envSvcHost = System.getenv("BITCOIN_SERVICE_HOST");
        String envSvcPort = System.getenv("BITCOIN_SERVICE_PORT");


        String regexstr = "^[0]|[1-9]\\d*$";


        if (envSvcHost != null && !envSvcHost.isEmpty() && (envSvcPort != null && !envSvcPort.isEmpty() && regexstr.matches(envSvcPort))) {
            svcHost = envSvcHost;
            svcPort = Integer.parseInt(envSvcPort);
        }

        String resultstr = "[{'Error': 'no result'}, {'Service Host': " + envSvcHost + ", 'Service Port': " + envSvcPort  + "}]";

        svcHost = "192.168.99.100";
        svcPort = 5000;

        try {
            URI uri = new URIBuilder().setScheme("http").setHost(svcHost).setPort(svcPort).build();

            HttpGet httpGet = new HttpGet(uri);
            CloseableHttpResponse response1 = httpclient.execute(httpGet);
            // The underlying HTTP connection is still held by the response object
            // to allow the response content to be streamed directly from the network socket.
            // In order to ensure correct deallocation of system resources
            // the user MUST call CloseableHttpResponse#close() from a finally clause.
            // Please note that if response content is not fully consumed the underlying
            // connection cannot be safely re-used and will be shut down and discarded
            // by the connection manager.
            try {
                System.out.println(response1.getStatusLine());
                HttpEntity entity1 = response1.getEntity();
                // do something useful with the response body
                // and ensure it is fully consumed
                resultstr = "[ " + resultstr + ", {'Service Host': " + svcHost + ", {'Service Port': " + Integer.toString(svcPort) + "}, " +  EntityUtils.toString(entity1) + "]";
                EntityUtils.consume(entity1);
            } finally {
                response1.close();
            }

        } finally {
            httpclient.close();
            return resultstr;
        }
    }

   public static void main(String args[]) {
       try {
           requestPrice();
       } catch (Exception e) {
       } finally {
       }

   }

}

