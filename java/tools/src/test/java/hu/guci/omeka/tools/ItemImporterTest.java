/**
 * 
 */
package hu.guci.omeka.tools;

import hu.areus.terminus.testsupport.BaseTestClass;

import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Test;

/**
 * @author adam.katona
 *
 */
public class ItemImporterTest extends BaseTestClass 
{
	/**
	 * Test the importer.
	 * @throws EncryptedDocumentException
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	@Test
	public void testImportItems() throws EncryptedDocumentException, InvalidFormatException, IOException
	{
		ItemImporter importer = new ItemImporter();
		importer.importItems();
	}
}
