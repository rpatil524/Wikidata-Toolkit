package org.wikidata.wdtk.wikibaseapi.apierrors;

import java.util.List;

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

/**
 * Exception to indicate a MediaWiki API error caused by an edit conflict.
 *
 * @author Markus Kroetzsch
 *
 */
public class EditConflictErrorException extends MediaWikiApiErrorException {

	private static final long serialVersionUID = 3603929976083601076L;

	/**
	 * Creates a new exception.
	 *
	 * @param errorMessage
	 *            the error message reported by MediaWiki, or any other
	 *            meaningful message for the user
	 */
	@Deprecated(since = "0.16.0")
	public EditConflictErrorException(String errorMessage) {
		super(MediaWikiApiErrorHandler.ERROR_EDIT_CONFLICT, errorMessage);
	}
	
	/**
     * Creates a new exception.
     *
     * @param errorMessage
     *            the root error message
     * @param detailedMessages
     *            additional details provided by the MediaWiki API
     */
    public EditConflictErrorException(String errorMessage, List<MediaWikiErrorMessage> detailedMessages) {
        super(MediaWikiApiErrorHandler.ERROR_EDIT_CONFLICT, errorMessage, detailedMessages);
    }


}
