import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbBLOB;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import java.net.URL;
//import java.net.URLConnection;
import java.net.URLEncoder;
//import java.net.URLConnection;
import javax.net.ssl.HttpsURLConnection;
import java.security.*;
import java.io.*;
import java.util.*;
//import com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnectionOldImpl;

public class IC0097_GetInvoiceList_BuildQueryString extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		//MbOutputTerminal alt = getOutputTerminal("alternate");

		//String  strURL = "https://ehpnaqa.documentportal.com/servlet/api";
		String responseMessage = null;
		
		MbMessage inMessage = inAssembly.getMessage();
		
		// create new empty message
		MbMessage outMessage = new MbMessage();
		MbMessageAssembly outAssembly = new MbMessageAssembly(inAssembly, outMessage);
				
		copyMessageHeaders(inMessage, outMessage);
		
		try {
			// create new message as a copy of the input
			//MbMessage outMessage = new MbMessage(inMessage);
			//outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below
			
		   Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
	       System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
	        
			//URL dhServlet = new URL(strURL);
			java.net.URL dhServlet = new URL(null, (String)getUserDefinedAttribute("DocuLynx_URL"),new sun.net.www.protocol.https.Handler());
			

			 HttpsURLConnection conn = (HttpsURLConnection) dhServlet.openConnection();
			 conn.setRequestMethod("POST");
			 
			 conn.setDoOutput(true);
	         conn.setDoInput(true);

	         // Turn off caching
	         conn.setUseCaches(false);

	         // Work around a Netscape bug
	         conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	         
	         //String inputMsg = inMessage.toString();
	         byte [] msgAsBytes = inMessage.getRootElement().getLastChild().toBitstream(null,null,null,0,1208,0); 
	         String  inputMsg  = new String(msgAsBytes);
	         
	         String encoded = URLEncoder.encode(inputMsg, "UTF-8");
	         
	         OutputStream connOutputStream = conn.getOutputStream();
	         OutputStreamWriter connOutputStreamWriter = new OutputStreamWriter (connOutputStream);
	         
	         connOutputStreamWriter.write("UserName=");
             connOutputStreamWriter.write((String)getUserDefinedAttribute("DocuLynx_UserName"));
            connOutputStreamWriter.write("&");
            connOutputStreamWriter.write("Password=");
            connOutputStreamWriter.write((String)getUserDefinedAttribute("DocuLynx_Password"));
            connOutputStreamWriter.write("&");
            
            // prepend parameter name. This is required by webapi
            connOutputStreamWriter.write("XMLRequest=");
            connOutputStreamWriter.write(encoded,0,encoded.length());
            
         // All done writing character data so the writer needs to be flushed.
            connOutputStreamWriter.flush();
            connOutputStream.flush();
            
            conn.getResponseCode(); // force a wait until the response is ready
            
            if (conn.getResponseCode() == 200) { // success
            	InputStream inStream = conn.getInputStream();
            	
            	responseMessage = convertStreamToString (inStream);
            	
            	byte buffer[] = new byte[1024];
                int nRead = 0;
                String hitlistResponse = null;
                while ((nRead = inStream.read(buffer, 0, 1024-1)) != -1) {
                	hitlistResponse = hitlistResponse + ' ' + buffer;
                }
            }

			// End of user code
			// ----------------------------------------------------------
		} catch (MbException e) {
			// Re-throw to allow Broker handling of MbException
			throw e;
		} catch (RuntimeException e) {
			// Re-throw to allow Broker handling of RuntimeException
			throw e;
		} catch (Exception e) {
			// Consider replacing Exception with type(s) thrown by user code
			// Example handling ensures all exceptions are re-thrown to be handled in the flow
			throw new MbUserException(this, "evaluate()", "", "", e.toString(),
					null);
		}
		// The following should only be changed
		// if not propagating message to the 'out' terminal
		
		//Create new parsers folders in the output message.
		/*
		  MbMessage outputMessage = new MbMessage(inMessage);
		  outAssembly = new MbMessageAssembly(inAssembly, outputMessage);
		
		  MbMessage clonedInMessage = new MbMessage(inAssembly.getMessage());
		  MbElement inputRootElement = clonedInMessage.getRootElement();
		  MbElement inputPropertiesElement = inputRootElement.getFirstElementByPath("Properties");
		 // MbElement inputMQMDElement = inputRootElement.getFirstElementByPath("MQMD");
		  //MbElement inputXMLNSCElement = inputRootElement.getFirstElementByPath("XMLNSC");

		  //copyMessageHeaders(inMessage, outputMessage);
		  
		  MbElement outputRootElement = outputMessage.getRootElement();
		  MbElement outputPropertiesElement = outputRootElement.createElementAsLastChild("Properties");
		//  MbElement outputMQMDElement = outputRootElement.createElementAsLastChild("MQMD");
		// MbElement outputBody = outputRootElement.createElementAsLastChild(MbBLOB.PARSER_NAME);       
		  //Create the root tag in the output XMLNSC folder that will be used for this output record.
		 // MbElement outputXMLNSCRootTag = outputXMLNSCElement.createElementAsLastChild(MbElement.TYPE_NAME, "TestCase", null);
		  //Create the record tag for this output message instance.
		 // MbElement currentOutputRecord = outputXMLNSCRootTag.createElementAsLastChild(MbElement.TYPE_NAME, "Record", null);

		  //Copy the Properties Folder, MQMD header, and the current record.
		  outputPropertiesElement.copyElementTree(inputPropertiesElement);
		  //outputMQMDElement.copyElementTree(inputMQMDElement);
		  //currentOutputRecord.copyElementTree(currentInputRecord);   
		  */

		  //MbElement outBody = outputBody.createElementAsLastChild(responseMessage);
		  
          String messageType = "";
          String messageSet = "";
          String messageFormat = "";
          int encoding = 546;
          int ccsid = 1208;
          int options = 0;
          
          MbElement outputRootElement = outMessage.getRootElement();
          
          outputRootElement.createElementAsLastChildFromBitstream (responseMessage.getBytes(),MbBLOB.PARSER_NAME, messageType, messageSet, messageFormat, encoding, ccsid, options);
          //outputBody.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "BLOB", responseMessage.getBytes());            
        		          

		  
		out.propagate(outAssembly);

	}
	
	public void copyMessageHeaders(MbMessage inMessage, MbMessage outMessage)
			throws MbException {
		MbElement outRoot = outMessage.getRootElement();

		// iterate though the headers starting with the first child of the root
		// element
		MbElement header = inMessage.getRootElement().getFirstChild();
		while (header != null && header.getNextSibling() != null) // stop before
																	// the last
																	// child
																	// (body)
		{
			// copy the header and add it to the out message
			outRoot.addAsLastChild(header.copy());
			// move along to next header
			header = header.getNextSibling();
		}
	}
	static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
}

