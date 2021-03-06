package org.wikidata.wdtk.dumpfiles;

/*
 * #%L
 * Wikidata Toolkit Dump File Handling
 * %%
 * Copyright (C) 2014 Wikidata Toolkit Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.helpers.JsonDeserializer;
import org.wikidata.wdtk.datamodel.interfaces.*;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * A revision processor that processes Wikibase entity content from a dump file.
 * Revisions are parsed to obtain EntityDocument objects.
 *
 * @author Markus Kroetzsch
 *
 */
public class WikibaseRevisionProcessor implements MwRevisionProcessor {

	static final Logger logger = LoggerFactory
			.getLogger(WikibaseRevisionProcessor.class);

	/**
	 * The IRI of the site that this data comes from. This cannot be extracted
	 * from individual revisions.
	 */
	private final EntityDocumentProcessor entityDocumentProcessor;
	private final JsonDeserializer jsonDeserializer;


	/**
	 * Constructor.
	 *
	 * @param entityDocumentProcessor
	 *            the object that entity documents will be forwarded to
	 * @param siteIri
	 *            the IRI of the site that the data comes from, as used in
	 *            {@link ItemIdValue#getSiteIri()}
	 */
	public WikibaseRevisionProcessor(
			EntityDocumentProcessor entityDocumentProcessor, String siteIri) {
		this.entityDocumentProcessor = entityDocumentProcessor;
		this.jsonDeserializer = new JsonDeserializer(siteIri);
	}

	@Override
	public void startRevisionProcessing(String siteName, String baseUrl,
			Map<Integer, String> namespaces) {
		// FIXME the baseUrl from the dump is not the baseIri we need here
		// Compute this properly.
		// this.jsonConverter = new JsonConverter(
		// "http://www.wikidata.org/entity/", this.dataObjectFactory);
	}

	@Override
	public void processRevision(MwRevision mwRevision) {
		if (MwRevision.MODEL_WIKIBASE_ITEM.equals(mwRevision.getModel())) {
			processItemRevision(mwRevision);
		} else if (MwRevision.MODEL_WIKIBASE_PROPERTY.equals(mwRevision
				.getModel())) {
			processPropertyRevision(mwRevision);
		} else if (MwRevision.MODEL_WIKIBASE_LEXEME.equals(mwRevision
				.getModel())) {
			processLexemeRevision(mwRevision);
		} // else: ignore this revision
	}

	public void processItemRevision(MwRevision mwRevision) {
		if(isWikibaseRedirection(mwRevision)) {
			processEntityRedirectRevision(mwRevision);
			return;
		}

		try {
			ItemDocument document = jsonDeserializer.deserializeItemDocument(mwRevision.getText());
			entityDocumentProcessor.processItemDocument(document);
		} catch (JsonParseException e1) {
			logger.error("Failed to parse JSON for item "
					+ mwRevision.getPrefixedTitle() + ": " + e1.getMessage());
		} catch (JsonMappingException e1) {
			logger.error("Failed to map JSON for item "
					+ mwRevision.getPrefixedTitle() + ": " + e1.getMessage());
			e1.printStackTrace();
			System.out.print(mwRevision.getText());
		} catch (IOException e1) {
			logger.error("Failed to read revision: " + e1.getMessage());
		}
	}

	public void processPropertyRevision(MwRevision mwRevision) {
		if(isWikibaseRedirection(mwRevision)) {
			processEntityRedirectRevision(mwRevision);
			return;
		}

		try {
			PropertyDocument document = jsonDeserializer.deserializePropertyDocument(mwRevision.getText());
			entityDocumentProcessor.processPropertyDocument(document);
		} catch (JsonParseException e1) {
			logger.error("Failed to parse JSON for property "
					+ mwRevision.getPrefixedTitle() + ": " + e1.getMessage());
		} catch (JsonMappingException e1) {
			logger.error("Failed to map JSON for property "
					+ mwRevision.getPrefixedTitle() + ": " + e1.getMessage());
			e1.printStackTrace();
			System.out.print(mwRevision.getText());
		} catch (IOException e1) {
			logger.error("Failed to read revision: " + e1.getMessage());
		}
	}

	private void processLexemeRevision(MwRevision mwRevision) {
		if(isWikibaseRedirection(mwRevision)) {
			processEntityRedirectRevision(mwRevision);
			return;
		}

		try {
			LexemeDocument document = jsonDeserializer.deserializeLexemeDocument(mwRevision.getText());
			entityDocumentProcessor.processLexemeDocument(document);
		} catch (JsonParseException e1) {
			logger.error("Failed to parse JSON for lexeme "
					+ mwRevision.getPrefixedTitle() + ": " + e1.getMessage());
		} catch (JsonMappingException e1) {
			logger.error("Failed to map JSON for lexeme "
					+ mwRevision.getPrefixedTitle() + ": " + e1.getMessage());
			e1.printStackTrace();
			System.out.print(mwRevision.getText());
		} catch (IOException e1) {
			logger.error("Failed to read revision: " + e1.getMessage());
		}
	}

	private void processEntityRedirectRevision(MwRevision mwRevision) {
		try {
			EntityRedirectDocument document = jsonDeserializer.deserializeEntityRedirectDocument(mwRevision.getText());
			entityDocumentProcessor.processEntityRedirectDocument(document);
		} catch (JsonParseException e1) {
			logger.error("Failed to parse JSON for redirect "
					+ mwRevision.getPrefixedTitle() + ": " + e1.getMessage());
		} catch (JsonMappingException e1) {
			logger.error("Failed to map JSON for redirect "
					+ mwRevision.getPrefixedTitle() + ": " + e1.getMessage());
			e1.printStackTrace();
			System.out.print(mwRevision.getText());
		} catch (IOException e1) {
			logger.error("Failed to read revision: " + e1.getMessage());
		}
	}

	private boolean isWikibaseRedirection(MwRevision mwRevision) {
		return mwRevision.getText().contains("\"redirect\":"); //Hacky but fast
	}

	@Override
	public void finishRevisionProcessing() {
		// nothing to do
	}

}
