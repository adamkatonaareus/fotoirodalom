START TRANSACTION;
DELETE FROM omeka_records_tags;
DELETE FROM omeka_tags;
DELETE FROM omeka_element_texts WHERE id >= 10;
DELETE FROM omeka_items;
COMMIT;
