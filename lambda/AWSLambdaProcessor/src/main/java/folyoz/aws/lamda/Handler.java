package folyoz.aws.lamda;


import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;

import folyoz.aws.lamda.processor.SolrProcessor;

public class Handler implements RequestHandler<SQSEvent, Void>{
    
	SolrProcessor solrProcessor;
	LambdaLogger logger;
	
	@Override
    public Void handleRequest(SQSEvent event, Context context)
    {
		solrProcessor = new SolrProcessor();
		logger = context.getLogger();
		
		logger.log("[START FOLYOZ LAMBDA]");
		logger.log(event + "");
		
        for(SQSMessage msg : event.getRecords()){
            
        	System.out.println(msg);
        	
        	String messageBody = new String(msg.getBody()); 
        
        	try {
				solrProcessor.updateRequest(messageBody, System.getenv("folyoz_solr_endpoint"), logger);
			} catch (SolrServerException | IOException e) {
				logger.log("[EXCEPTION] " + e.getMessage());
				e.printStackTrace();
			}
        	
        }
        
        logger.log("[END FOLYOZ LAMBDA]");
        
        return null;
    }
}
