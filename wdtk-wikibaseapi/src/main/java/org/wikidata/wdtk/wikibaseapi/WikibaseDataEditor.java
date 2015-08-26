package org.wikidata.wdtk.wikibaseapi;

/*
 * #%L
 * Wikidata Toolkit Wikibase API
 * %%
 * Copyright (C) 2014 - 2015 Wikidata Toolkit Developers
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.json.jackson.JsonSerializer;

/**
 * Class that provides high-level editing functionality for Wikibase data.
 *
 * @author Markus Kroetzsch
 *
 */
public class WikibaseDataEditor {

	static final Logger logger = LoggerFactory
			.getLogger(WikibaseDataEditor.class);

	/**
	 * API Action to fetch data.
	 */
	final WbEditEntityAction wbEditEntityAction;

	/**
	 * The IRI that identifies the site that the data is from.
	 */
	final String siteIri;

	/**
	 * Creates an object to edit data via the Web API of the given
	 * {@link ApiConnection} object. The site URI is necessary to create data
	 * objects from API responses, since it is not contained in the data
	 * retrieved from the URI.
	 *
	 * @param connection
	 *            ApiConnection
	 * @param siteUri
	 *            the URI identifying the site that is accessed (usually the
	 *            prefix of entity URIs), e.g.,
	 *            "http://www.wikidata.org/entity/"
	 */
	public WikibaseDataEditor(ApiConnection connection, String siteUri) {
		this.wbEditEntityAction = new WbEditEntityAction(connection, siteUri);
		this.siteIri = siteUri;
	}

	/**
	 * Creates a new item document with the summary message as provided.
	 * <p>
	 * The item document that is given as a parameter must use a local item id,
	 * such as {@link ItemIdValue#NULL}. The newly created document is returned.
	 * It will contain the new id. Note that the site IRI used in this ID is not
	 * part of the API response; the site IRI given when constructing this
	 * object is used in this place.
	 * <p>
	 * Statements in the given data must have empty statement IDs.
	 *
	 * @param itemDocument
	 *            the document that contains the data to be written
	 * @param summary
	 *            additional summary message for the edit, or null to omit this
	 * @return newly created item document, or null if there was an error
	 * @throws NoLoginException
	 *             if our {@link ApiConnection} is not logged in
	 * @throws IOException
	 *             if there was an IO problem, such as missing network
	 *             connection
	 */
	public ItemDocument createItemDocument(ItemDocument itemDocument,
			String summary) throws NoLoginException, IOException {
		String data = JsonSerializer.getJsonString(itemDocument);
		return (ItemDocument) this.wbEditEntityAction.wbEditEntity(null, null,
				null, "item", data, false, summary);
	}

	/**
	 * Creates a new property document with the summary message as provided.
	 * <p>
	 * The property document that is given as a parameter must use a local
	 * property id, such as {@link PropertyIdValue#NULL}. The newly created
	 * document is returned. It will contain the new id. Note that the site IRI
	 * used in this ID is not part of the API response; the site IRI given when
	 * constructing this object is used in this place.
	 * <p>
	 * Statements in the given data must have empty statement IDs.
	 *
	 * @param propertyDocument
	 *            the document that contains the data to be written
	 * @param summary
	 *            additional summary message for the edit, or null to omit this
	 * @return newly created property document, or null if there was an error
	 * @throws NoLoginException
	 *             if our {@link ApiConnection} is not logged in
	 * @throws IOException
	 *             if there was an IO problem, such as missing network
	 *             connection
	 */
	public PropertyDocument createPropertyDocument(
			PropertyDocument propertyDocument, String summary)
			throws NoLoginException, IOException {
		String data = JsonSerializer.getJsonString(propertyDocument);
		return (PropertyDocument) this.wbEditEntityAction.wbEditEntity(null,
				null, null, "property", data, false, summary);
	}

	/**
	 * Writes the data for the given item document with the summary message as
	 * given. Optionally, the existing data is cleared (deleted).
	 * <p>
	 * The id of the given item document is used to specify which item document
	 * should be changed. The site IRI will be ignored for this.
	 * <p>
	 * If the data is not cleared, then the existing data will largely be
	 * preserved. Statements with empty ids will be added without checking if
	 * they exist already; statements with (valid) ids will replace any existing
	 * statements with these ids or just be added if there are none. Labels,
	 * descriptions, and aliases will be preserved for all languages for which
	 * no data is given at all. For aliases this means that writing one alias in
	 * a language will overwrite all aliases in this language, so some care is
	 * needed.
	 *
	 * @param itemDocument
	 *            the document that contains the data to be written
	 * @param clear
	 *            if true, the existing data will be replaced by the given data;
	 *            if false, the given data will be added to the existing data,
	 *            overwriting only parts that are set to new values
	 * @param summary
	 *            additional summary message for the edit, or null to omit this
	 * @return the modified item document, or null if there was an error
	 * @throws NoLoginException
	 *             if our {@link ApiConnection} is not logged in
	 * @throws IOException
	 *             if there was an IO problem, such as missing network
	 *             connection
	 */
	public ItemDocument editItemDocument(ItemDocument itemDocument,
			boolean clear, String summary) throws NoLoginException, IOException {
		String data = JsonSerializer.getJsonString(itemDocument);
		return (ItemDocument) this.wbEditEntityAction.wbEditEntity(itemDocument
				.getItemId().getId(), null, null, null, data, clear, summary);
	}

	/**
	 * Writes the data for the given property document with the summary message
	 * as given. Optionally, the existing data is cleared (deleted).
	 * <p>
	 * The id of the given property document is used to specify which property
	 * document should be changed. The site IRI will be ignored for this.
	 * <p>
	 * If the data is not cleared, then the existing data will largely be
	 * preserved. Statements with empty ids will be added without checking if
	 * they exist already; statements with (valid) ids will replace any existing
	 * statements with these ids or just be added if there are none. Labels,
	 * descriptions, and aliases will be preserved for all languages for which
	 * no data is given at all. For aliases this means that writing one alias in
	 * a language will overwrite all aliases in this language, so some care is
	 * needed.
	 *
	 * @param propertyDocument
	 *            the document that contains the data to be written
	 * @param clear
	 *            if true, the existing data will be replaced by the given data;
	 *            if false, the given data will be added to the existing data,
	 *            overwriting only parts that are set to new values
	 * @param summary
	 *            additional summary message for the edit, or null to omit this
	 * @return the modified property document, or null if there was an error
	 * @throws NoLoginException
	 *             if our {@link ApiConnection} is not logged in
	 * @throws IOException
	 *             if there was an IO problem, such as missing network
	 *             connection
	 */
	public PropertyDocument editPropertyDocument(
			PropertyDocument propertyDocument, boolean clear, String summary)
			throws NoLoginException, IOException {
		String data = JsonSerializer.getJsonString(propertyDocument);
		return (PropertyDocument) this.wbEditEntityAction.wbEditEntity(
				propertyDocument.getPropertyId().getId(), null, null, null,
				data, clear, summary);
	}

}
