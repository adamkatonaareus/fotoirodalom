/**
 * 
 */
package hu.guci.omeka.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import hu.areus.terminus.base.BaseClass;

/**
 * @author adam.katona
 *
 */
public class ItemImporter extends BaseClass 
{
	private static final String IMPORT_PATH = "F:\\guci\\guci-Omeka\\Munkaanyagok\\";
	private static final String IMPORT_FILENAME = "Import_v0.1.xlsx";
	private static final int FIRST_ITEM_ID = 1;
	private static final int FIRST_ELEMENT_TEXT_ID = 10;
	private static final int FIRST_RECORD_TAG_ID = 1;
	private static final int FIRST_TAG_ID = 1;
	
	private Workbook workbook;
	private Sheet sheet;
	private Map<Integer, String> columnNames = new HashMap<>();
	private Map<String, Integer> elementNames = new HashMap<>();
	private Map<String, Integer> tags = new HashMap<>();
	private List<RecordTag> recordTags = new ArrayList<>();
	private List<Item> items = new ArrayList<>();
	private List<ElementText> elementTexts = new ArrayList<>();
	private int itemId = FIRST_ITEM_ID;
	private int elementTextId = FIRST_ELEMENT_TEXT_ID;
	private int recordTagId = FIRST_RECORD_TAG_ID;
	private int tagId = FIRST_TAG_ID;

	
	/**
	 * Call this.
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 * @throws EncryptedDocumentException 
	 */
	public void importItems() throws EncryptedDocumentException, InvalidFormatException, IOException
	{
		//--- Open Excel file
		File file = new File(IMPORT_PATH + IMPORT_FILENAME);
		getLogger().info("Opening " + file.getAbsolutePath());
		InputStream is = new FileInputStream(file);
		workbook = WorkbookFactory.create(is);
		
		try
		{
			//--- Process tabs
			for (int i=0; i<3; i++)
			{
				processSheet(i);
			}
			
			//--- Write tags
			writeTags();
		}
		finally
		{
			workbook.close();
		}
	}

	/**
	 * Process a sheet
	 * @throws IOException 
	 */
	private void processSheet(int sheetNo) throws IOException 
	{
		sheet = workbook.getSheetAt(sheetNo);
		String collectionName = sheet.getSheetName();
		int collectionId = getCollectionId(collectionName);
		items.clear();
		elementTexts.clear();
		
		getLogger().info("Processing sheet to collection: " + collectionName);
		
		//--- Load column names from the second row
		getColumnNames();
		
		//--- Process rows
		for (int i=2; i<sheet.getLastRowNum(); i++)
		{
			getLogger().debug("Importing row: " + i);
			Row row = sheet.getRow(i);
			
			if (row != null)
			{
				//--- Create an item record
				Item item = new Item();
				item.setId(itemId++);
				item.setCollectionId(collectionId);
				
				//--- Get column values
				Map<String, String> columnValues = new HashMap<>();
				NumberFormat format = new DecimalFormat("#");
				
				for (Integer colNo : columnNames.keySet())
				{
					Cell cell = row.getCell(colNo);
					if (cell != null)
					{
						switch (cell.getCellTypeEnum())
						{
							case STRING:
								columnValues.put(columnNames.get(colNo), cell.getStringCellValue());
								break;
							case NUMERIC:
								//--- FIX KA 20180429: round numbers so it won't add .0 to string.
								columnValues.put(columnNames.get(colNo), 
									format.format(cell.getNumericCellValue()));
								break;
							case BLANK:
								columnValues.put(columnNames.get(colNo), "");
								break;							
							default:
								throw new IllegalArgumentException("Dunno how to handle cell type: " + cell.getCellTypeEnum());
						}
						
					}
				}
				
				if (StringUtils.isBlank(columnValues.get("itemType")))
				{
					getLogger().warn("Skipping row " + i + ", no item type.");
					continue;
				}

				items.add(item);

				//--- Find out item type
				item.setItemTypeId(getItemTypeId(columnValues.get("itemType")));
				
				//--- Collection not used, comes from sheet name
				
				//--- public, featured
//				item.setPublic(
//					columnValues.containsKey("public") && (columnValues.get("public").equals(1)));
//				item.setFeatured(
//						columnValues.containsKey("featured") && (columnValues.get("featured").equals(1)));

				
				//--- Create elementText records
				for(String name : columnValues.keySet())
				{
					ElementText record = new ElementText();
					record.setId(elementTextId++);
					record.setRecordId(item.getId());
					record.setRecordType("Item");
					record.setElementId(getElementId(name));
					record.setText(columnValues.get(name));
					
					if (record.getElementId() > 0)
					{
						elementTexts.add(record);
					}
				}
				
				//--- Process tags
				if (!StringUtils.isBlank(columnValues.get("tag")))
				{
					String[] tags = columnValues.get("tag").split(",");
					
					for (String tag : tags)
					{
						String tag2 = tag.trim().toLowerCase();
						getLogger().debug("Adding tag: " + tag2);
						
						int tagId = getTagId(tag2);
						RecordTag recordTag = new RecordTag();
						recordTag.setId(recordTagId++);
						recordTag.setRecordId(item.getId());
						recordTag.setTagId(tagId);
						recordTags.add(recordTag);
					}
				}
			}
		}
		
		//--- Write SQL
		writeSql(sheetNo, collectionName);
	}

	
	/**
	 * Get record tag id
	 * @param tag2
	 * @return
	 */
	private int getTagId(String tag) 
	{
		if (!tags.containsKey(tag))
		{
			tags.put(tag, tagId++);
		}
		
		return tags.get(tag);
	}

	/**
	 * Write out SQL commands.
	 * @param collectionName
	 * @throws IOException 
	 */
	private void writeSql(int sheetNo, String collectionName) throws IOException 
	{
		File file = new File(IMPORT_PATH + sheetNo + "_" + collectionName + ".sql");
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		
		try
		{
			writer.write("START TRANSACTION;");
			writer.newLine();
			writer.write("INSERT INTO `omeka_items`(`id`, `item_type_id`, `collection_id`, `featured`, `public`, `modified`, `added`, `owner_id`) VALUES ");
			writer.newLine();

			for (int i=0; i<items.size(); i++)
			{
				Item item = items.get(i);
				writer.write("(" + item.getId() + ", " + item.getItemTypeId() + ", " + item.getCollectionId());
				writer.write(", " + (item.isFeatured() ? "1" : "0"));
				writer.write(", " + (item.isPublic() ? "1" : "0"));
				writer.write(", null, CURRENT_TIMESTAMP, 1)");
				
				writer.write((i < items.size() - 1) ? "," : ";");
				writer.newLine();
			}
			
			writer.write("INSERT INTO `omeka_element_texts`(`id`, `record_id`, `record_type`, `element_id`, `html`, `text`) VALUES ");
			writer.newLine();
			
			for (int i=0; i<elementTexts.size(); i++)
			{
				ElementText item = elementTexts.get(i);
				writer.write("(" + item.getId() + ", " + item.getRecordId() + ", '" + item.getRecordType());
				writer.write("', " + item.getElementId() + ", 0");
				writer.write(", '" + item.getText() + "')");
				
				writer.write((i < elementTexts.size() - 1) ? "," : ";");
				writer.newLine();				
			}
			
			writer.write("COMMIT;");
			writer.newLine();
		}
		finally
		{
			writer.close();
		}
	}
	
	/**
	 * Write out SQL commands.
	 * @param collectionName
	 * @throws IOException 
	 */
	private void writeTags() throws IOException 
	{
		File file = new File(IMPORT_PATH + "4_tags.sql");
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		
		try
		{
			writer.write("START TRANSACTION;");
			writer.newLine();

			writer.write("INSERT INTO `omeka_tags`(`id`, `name`) VALUES ");
			writer.newLine();
			
			int tagIndex = 0;
			for (String tag : tags.keySet())
			{
				writer.write("(" + tags.get(tag) + ", '" + tag + "')");
				writer.write((tagIndex < tags.size() - 1) ? "," : ";");
				writer.newLine();
				tagIndex++;
			}
			
			writer.write("INSERT INTO `omeka_records_tags`(`id`, `record_id`, `record_type`, `tag_id`, `time`) VALUES ");
			writer.newLine();
			
			for (int i=0; i<recordTags.size(); i++)
			{
				RecordTag item = recordTags.get(i);
				writer.write("(" + item.getId() + ", " + item.getRecordId() + ", 'Item', " + item.getTagId());
				writer.write(", CURRENT_TIMESTAMP)");
				
				writer.write((i < recordTags.size() - 1) ? "," : ";");
				writer.newLine();
			}
			
			writer.write("COMMIT;");
			writer.newLine();
		}
		finally
		{
			writer.close();
		}
	}	

	/**
	 * Get element id by name.
	 * @param name: full element name, eg. Dublin Core:Title. This breaks down to element set:element name
	 * @return
	 */
	private int getElementId(String name) 
	{
		//--- Special fields.
		if (!name.contains(":"))
		{
			return 0;
		}
		
		String[] parts = name.split(":");
		
		//--- We don't need element set name right now...
		
		//--- But we need to find element name
		if (elementNames.isEmpty())
		{
			elementNames.put("Title", 50);
			elementNames.put("Text",1);
			elementNames.put("Interviewer", 2);
			elementNames.put("Interviewee", 3);
			elementNames.put("Location", 4);
			elementNames.put("Transcription", 5);
			elementNames.put("Local URL", 6);
			elementNames.put("Original Format", 7);
			elementNames.put("Physical Dimensions", 10);
			elementNames.put("Duration", 11);
			elementNames.put("Compression", 12);
			elementNames.put("Producer", 13);
			elementNames.put("Director", 14);
			elementNames.put("Bit Rate/Frequency", 15);
			elementNames.put("Time Summary", 16);
			elementNames.put("Email Body", 17);
			elementNames.put("Subject Line", 18);
			elementNames.put("From", 19);
			elementNames.put("To", 20);
			elementNames.put("CC", 21);
			elementNames.put("BCC", 22);
			elementNames.put("Number of Attachments", 23);
			elementNames.put("Standards", 24);
			elementNames.put("Objectives", 25);
			elementNames.put("Materials", 26);
			elementNames.put("Lesson Plan Text", 27);
			elementNames.put("URL", 28);
			elementNames.put("Event Type", 29);
			elementNames.put("Participants", 30);
			elementNames.put("Birth Date", 31);
			elementNames.put("Birthplace", 32);
			elementNames.put("Death Date", 33);
			elementNames.put("Occupation", 34);
			elementNames.put("Biographical Text", 35);
			elementNames.put("Bibliography", 36);
			elementNames.put("Contributor", 37);
			elementNames.put("Coverage", 38);
			elementNames.put("Creator", 39);
			elementNames.put("Date", 40);
			elementNames.put("Description", 41);
			elementNames.put("Format", 42);
			elementNames.put("Identifier", 43);
			elementNames.put("Language", 44);
			elementNames.put("Publisher", 45);
			elementNames.put("Relation", 46);
			elementNames.put("Rights", 47);
			elementNames.put("Source", 48);
			elementNames.put("Subject", 49);
			elementNames.put("Title", 50);
			elementNames.put("Type", 51);
			elementNames.put("Author", 52);
			elementNames.put("Sorozat cím", 53);
			elementNames.put("Fotózta", 54);
			elementNames.put("Jpg link", 55);
			elementNames.put("SZTE rekord link", 56);
			elementNames.put("PIM rekord link", 57);
			elementNames.put("Fotó link", 58);
			elementNames.put("Fotó jellege", 59);
			elementNames.put("Fotós vagy író kapcsán", 60);
		}
		
		if (elementNames.containsKey(parts[1]))
		{
			return elementNames.get(parts[1]);
		}
		else
		{
			throw new IllegalArgumentException("No such element: " + name);
		}
	}

	/**
	 * Get item type id by name.
	 * @param itemTypeName
	 * @return
	 */
	private int getItemTypeId(String itemTypeName) 
	{
		switch (itemTypeName)
		{
			case "Book": return 18;
			case "Article": return 19;
			case "Journal": return 20;
			case "Website": return 7;
			case "Still image": return 6;
			case "doktori disszertáció": return 21;

			default: throw new IllegalArgumentException("Unknown item type: \"" + itemTypeName + "\"");
		}
	}

	/**
	 * Get collection id by name.
	 * @param collectionName
	 * @return
	 */
	private int getCollectionId(String collectionName) 
	{
		switch (collectionName)
		{
			case "Fotóillusztrált könyvek": return 1;
			case "Fotóirodalom portrék": return 2;
			case "Fotóirodalom elmélet": return 3;
			case "Illusztráció nélkül": return 4;
			default: throw new IllegalArgumentException("Unknown collection: " + collectionName);
		}
	}

	/**
	 * Load column names from the second row.
	 */
	private void getColumnNames() 
	{
		Row row = sheet.getRow(1);
		for (int i=0; i<row.getLastCellNum(); i++)
		{
			Cell cell = row.getCell(i);
			
			if (cell != null && !StringUtils.isBlank(cell.getStringCellValue()))
			{
				columnNames.put(i, cell.getStringCellValue());
				getLogger().debug("Column name: " + cell.getStringCellValue());
			}
		}
	}
}
