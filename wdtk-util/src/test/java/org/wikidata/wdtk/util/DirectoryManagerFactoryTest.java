/*
 * #%L
 * Wikidata Toolkit Utilities
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

package org.wikidata.wdtk.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DirectoryManagerFactoryTest {

	public static class TestDirectoryManager implements DirectoryManager {

		@Override
		public DirectoryManager getSubdirectoryManager(String subdirectoryName) {
			return null;
		}

		@Override
		public boolean hasSubdirectory(String subdirectoryName) {
			return false;
		}

		@Override
		public boolean hasFile(String fileName) {
			return false;
		}

		@Override
		public long createFile(String fileName, InputStream inputStream) {
			return 0;
		}

		@Override
		public long createFileAtomic(String fileName, InputStream inputStream) {
			return 0;
		}

		@Override
		public void createFile(String fileName, String fileContents) {

		}

		@Override
		public OutputStream getOutputStreamForFile(String fileName) {
			return null;
		}

		@Override
		public InputStream getInputStreamForFile(String fileName, CompressionType compressionType) {
			return null;
		}

		@Override
		public List<String> getSubdirectories(String glob) {
			return null;
		}
	}

	@BeforeEach
	public void setup() throws IOException {
		DirectoryManagerFactory
				.setDirectoryManagerClass(DirectoryManagerImpl.class);
	}

	@Test
	public void createDirectoryManagerString() throws IOException {
		Path path = Paths.get(System.getProperty("user.dir"));
		DirectoryManager dm = DirectoryManagerFactory.createDirectoryManager(
				System.getProperty("user.dir"), true);
		assertTrue(dm instanceof DirectoryManagerImpl);
		DirectoryManagerImpl dmi = (DirectoryManagerImpl) dm;
		assertTrue(dmi.readOnly);
		assertEquals(path, dmi.directory);
	}

	@Test
	public void createDefaultDirectoryManagerPath() throws IOException {
		Path path = Paths.get(System.getProperty("user.dir"));
		DirectoryManager dm = DirectoryManagerFactory.createDirectoryManager(
				path, true);
		assertTrue(dm instanceof DirectoryManagerImpl);
		DirectoryManagerImpl dmi = (DirectoryManagerImpl) dm;
		assertTrue(dmi.readOnly);
		assertEquals(path, dmi.directory);
	}

	@Test
	public void createDirectoryManagerNoConstructor() {
		DirectoryManagerFactory
				.setDirectoryManagerClass(TestDirectoryManager.class);
		assertThrows(RuntimeException.class, () -> DirectoryManagerFactory.createDirectoryManager("/", true));
	}

	@Test
	public void createDirectoryManagerIoException() {
		assertThrows(IOException.class, () -> DirectoryManagerFactory.createDirectoryManager(
				"/nonexisting-directory/123456789/hopefully", true));
	}

}
