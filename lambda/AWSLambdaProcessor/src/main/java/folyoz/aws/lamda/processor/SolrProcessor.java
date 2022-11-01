package folyoz.aws.lamda.processor;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class SolrProcessor {

	SolrClient solrClient;

	LambdaLogger logger;

	public void updateRequest(String message, String solrURL, LambdaLogger pLogger)
			throws SolrServerException, IOException {
		this.logger = pLogger;
		logger.log("[PROCESSING] " + message);
		logger.log("Solr URL: " + solrURL);

		this.solrClient = new HttpSolrClient.Builder(solrURL).build();

		JSONObject jsonObject = new JSONObject(message);
		String action = (String) jsonObject.get("action");
		JSONObject jsonProduct = (JSONObject) jsonObject.get("product");

		if (action.equals("ADD")) {
			actiondAdd(jsonProduct);
		} else if (action.equals("DELETE")) {
			actionDelete(jsonProduct);
		} else if (action.equals("DELETE_ALL")) {
			actionDeleteAll();
		} else if (action.equals("UPDATE")) {
			actionDeleteAndAdd(jsonProduct);
		}

		logger.log("[PROCESSED] " + message);

	}

	private void actionDelete(JSONObject product) throws SolrServerException, IOException {
		logger.log("Solr: DELETE TRY: Product Id: " + product.get("productId"));
		if (solrClient.getById((String) product.get("productId")) != null) {
			logger.log("Solr: document found, will delete now: Product Id: " + product.get("productId"));
			solrClient.deleteById((String) product.get("productId"));
			solrClient.commit();
			logger.log("Solr: DELETED: Product Id: " + product.get("productId"));
		} else {
			logger.log("Solr: DELETE: could not find product: Product Id: " + product.get("productId"));
		}

	}

	private void actionDeleteAll() throws SolrServerException, IOException {
		logger.log("Solr: DELETING ALL PRODUCTS");
		solrClient.deleteByQuery("*");
		solrClient.commit();
		logger.log("Solr: DELETED ALL PRODUCTS");
	}

	private void actiondAdd(JSONObject product) throws SolrServerException, IOException {
		logger.log("Solr: Adding: Product Id: " + product.get("productId"));
		SolrInputDocument doc = new SolrInputDocument();

		String artistCity = (product.get("artistCity") == JSONObject.NULL
				|| ((String) product.get("artistCity")).isEmpty()) ? "Not Available"
						: (String) product.get("artistCity");
		String artistState = (product.get("artistState") == JSONObject.NULL
				|| ((String) product.get("artistState")).isEmpty()) ? "Not Available"
						: (String) product.get("artistState");
		String artistCountry = (product.get("artistCountry") == JSONObject.NULL
				|| ((String) product.get("artistCountry")).isEmpty()) ? "Not Available"
						: (String) product.get("artistCountry");
		String artistGender = (product.get("artistGender") == JSONObject.NULL
				|| ((String) product.get("artistGender")).isEmpty()) ? "Not Available"
						: (String) product.get("artistGender");
		String artistEthnicity = (product.get("artistEthnicity") == JSONObject.NULL
				|| ((String) product.get("artistEthnicity")).isEmpty()) ? "Not Available"
						: (String) product.get("artistEthnicity");

		// System.out.println("Product Id: " + product.getString("productId") + "
		// artistEthnicity: " + artistEthnicity + " product.get(\"artistEthnicity\"): "
		// + product.get("artistEthnicity"));

		doc.addField("productId", product.get("productId"));
		doc.addField("name", product.get("name"));
		doc.addField("image", product.get("image"));
		doc.addField("imageModal", product.get("imageModal"));
		doc.addField("imageModalHeight", product.get("imageModalHeight"));
		doc.addField("imageModalWidth", product.get("imageModalWidth"));
		doc.addField("categoryId", product.get("categoryId"));
		doc.addField("categoryName", product.get("categoryName"));
		doc.addField("attributeStyle", product.get("style"));
		doc.addField("attributeCategory", product.get("categories"));
		doc.addField("artistID", product.get("artistId"));
		doc.addField("artistName", product.get("artistName"));
		doc.addField("artistCity", artistCity);
		doc.addField("artistState", artistState);
		doc.addField("artistCountry", artistCountry);
		doc.addField("artistGender", artistGender);
		doc.addField("artistEthnicity", artistEthnicity);
		doc.addField("productAvailabilty", product.get("availability"));
		doc.addField("productClient", product.get("client"));
		doc.addField("artistImage", product.get("artistImage"));
		doc.addField("artistEducation", product.get("artistEducation"));
		doc.addField("artistPortfolioLink", product.get("artistPortfolioLink"));
		doc.addField("keywords", product.get("productKeywords"));

		solrClient.add(doc);
		solrClient.commit();
		logger.log("Solr: Added: Product Id: " + product.get("productId"));

	}

	private void actionDeleteAndAdd(JSONObject product) throws SolrServerException, IOException {
		logger.log("Solr: Updating: Product Id: " + product.get("productId"));
		actionDelete(product);
		actiondAdd(product);
		logger.log("Solr: Updated: Product Id: " + product.get("productId"));
	}

}
