<?php
$pageTitle = metadata($item, array('Dublin Core', 'Title'));
echo head(array(
    'title' => $pageTitle,
    'bodyclass' => 'items show',
));
?>
<div id="primary">
    <div class="row form-group">
        <div class="col-xs-12">
            <nav class="pager">
                <ul>
                    <li id="previous-item" class="previous"><?php echo link_to_previous_item_show(); ?></li>
                </ul>
                <ul>
                    <li id="next-item" class="next"><?php echo link_to_next_item_show(); ?></li>
                </ul>
            </nav>
        </div>
    </div>
    <div class="row">
        <div class="col-md-9">

    <div class="row page-header">
        <div class="col-xs-3">
            <h1><?php echo moly_cover($item, true); ?></h1>
        </div>
        <div class="col-xs-9">
            <h1><?php echo $pageTitle; ?></h1>
        </div>
    </div>
	</div>
	</div>

<?php if ($selectedMetadata = get_theme_option('Display Preselected Metadata')):
    echo common('show-selected-metadata', array('item' => $item));
else:
    // TODO Limit fields to display.
    // $fieldsToDisplay = get_theme_option('Display Dublin Core Fields');
?>
    <div class="row">
        <div class="col-md-9">
            <div class="row">
                <div class="col-xs-12">
					<!-- FIX KA 20180502: only display selected fields
                    php echo all_element_texts($item); 
					-->
                    <div class="element-set">
					
		<?php if ($itemCreator = metadata('item', array('Dublin Core', 'Creator'))): ?>
		<div id="dublin-core-creator" class="element">
			<div class="row">
				<div class="col-xs-3">Szerző:</div>
				<div class="col-xs-9"><?php echo $itemCreator ?></div>
			</div>
        </div>
		<?php endif ?>

		<?php if ($itemTitle = metadata('item', array('Dublin Core', 'Title'))): ?>
        <div id="dublin-core-title" class="element">
			<div class="row">
				<div class="col-xs-3">Cím:</div>
				<div class="col-xs-9"><?php echo $itemTitle ?></div>
			</div>
        </div>
		<?php endif ?>

		<?php if ($itemPublisher = metadata('item', array('Dublin Core', 'Publisher'))): ?>
        <div id="dublin-core-publisher" class="element">
			<div class="row">
				<div class="col-xs-3">Kiadó:</div>
				<div class="col-xs-9"><?php echo ($itemPublisher) ?></div>
			</div>
        </div>
		<?php endif ?>

		<?php if ($itemLocation = metadata('item', array('Item Type Metadata', 'Location'))): ?>
		<div id="dublin-core-location" class="element">
			<div class="row">
				<div class="col-xs-3">Kiadás helye:</div>
				<div class="col-xs-9"><?php echo $itemLocation ?></div>
			</div>
        </div>
		<?php endif ?>

		<?php if ($itemDate = metadata('item', array('Dublin Core', 'Date'))): ?>
        <div id="dublin-core-date" class="element">
			<div class="row">
				<div class="col-xs-3">Megjelenés ideje:</div>
				<div class="col-xs-9"><?php echo $itemDate ?></div>
			</div>
        </div>
		<?php endif ?>

		<?php if ($itemSorozatCim = metadata('item', array('Item Type Metadata', 'Sorozat cím'))): ?>
        <div id="book-item-type-metadata-sorozat-cm" class="element">
			<div class="row">
				<div class="col-xs-3">Sorozatcím:</div>
				<div class="col-xs-9"><?php echo $itemSorozatCim ?></div>
			</div>
        </div>
		<?php endif ?>

		<?php if ($itemContributor = metadata('item', array('Dublin Core', 'Contributor'))): ?>
        <div id="dublin-core-contributor" class="element">
			<div class="row">
				<div class="col-xs-3">Fotográfus:</div>
				<div class="col-xs-9"><?php echo $itemContributor ?></div>
			</div>
        </div>
		<?php endif ?>

		<?php if ($itemFotoJellege = metadata('item', array('Item Type Metadata', 'Fotó jellege'))): ?>
        <div id="book-item-type-metadata-fot-jellege" class="element">
			<div class="row">
				<div class="col-xs-3">Fotó jellege:</div>
				<div class="col-xs-9"><?php echo $itemFotoJellege ?></div>
			</div>
        </div>
		<?php endif ?>
		
		<?php if ($itemKapcsan = metadata('item', array('Item Type Metadata', 'Fotós vagy író kapcsán'))): ?>
        <div id="book-item-type-metadata-fot-jellege" class="element">
			<div class="row">
				<div class="col-xs-3">Fotós vagy író kapcsán:</div>
				<div class="col-xs-9"><?php echo $itemKapcsan ?></div>
			</div>
        </div>		
		<?php endif ?>
		
		<?php if ($itemDescription = metadata('item', array('Dublin Core', 'Description'))): ?>
        <div id="dublin-core-description" class="element">
			<div class="row">
				<div class="col-xs-3">Leírás:</div>
				<div class="col-xs-9"><?php echo $itemDescription ?></div>
			</div>
		</div>
		<?php endif ?>
		


		<?php if ($itemSzteLink = metadata('item', array('Item Type Metadata', 'SZTE rekord link'))): ?>
        <div id="book-item-type-metadata-szte-rekord-link" class="element">
			<div class="row">
				<div class="col-xs-3"><a target="_blank" href="<?php echo $itemSzteLink ?>">SZTE rekord link</a></div>
				<div class="col-xs-9"></div>
			</div>
        </div>
		<?php endif ?>

		<?php if ($itemPimLink = metadata('item', array('Item Type Metadata', 'PIM rekord link'))): ?>
        <div id="book-item-type-metadata-pim-rekord-link" class="element">
			<div class="row">
				<div class="col-xs-3"><a target="_blank" href="<?php echo $itemPimLink ?>">PIM rekord link</a></div>
				<div class="col-xs-9"></div>
			</div>
        </div>
		<?php endif ?>		
		
    
					</div><!-- end element-set -->
                </div>
            </div>

            <!-- If the item belongs to a collection, the following creates a link to that collection. -->
            <?php if (get_collection_for_item($item)): ?>
            <div class="row">
                <div class="col-xs-12">
                    <hr />
                    <div id="collection">
                        <h4 style="display:inline"><span class="glyphicon glyphicon-book"></span> Kategória: </h4>
                        <h4 style="display:inline"><?php echo link_to_collection_for_item(); ?></h4>
                    </div>
                </div>
            </div>
            <?php endif; ?>

            <!-- The following prints a list of all tags associated with the item -->
            <?php // if (metadata($item, 'has tags')): ?>
            <div class="row">
                <div class="col-xs-12">
                    <hr />
                    <h4><span class="fa fa-tags fa-large"></span>Tárgyszavak</h4>
                    <div class="tags well well-small">
                        <?php if (tag_string($item) != null):
                                echo tag_string($item);
							//FIX KA 20180507: don't write anything.
                            //else:
                            //    echo __('No tags recorded for this item.');
                            endif;
                        ?>
                    </div>
                </div>
            </div>
            <?php // endif; ?>
            <!-- The following prints a citation for this item. -->
            <div class="row">
                <div class="col-xs-12">
                    <hr />
                    <h4><span class="fa fa-retweet fa-lg"></span>Idézet</h4>
                    <div class="element-text"><?php echo metadata($item,'citation',array('no_escape' => true)); ?></div>
                </div>
            </div>
		<!-- FIX KA 20180429: removed output formats
            <div class="row">
                <div id="item-output-formats" class="col-xs-12">
                    <hr />
                    <h4><span class="glyphicon glyphicon-export"></span> <?php echo __('Output Formats'); ?></h4>
                    <div class="element-text"><?php echo output_format_list(); ?></div>
                </div>
            </div>
		-->
            <div class="row">
                <div class="col-xs-12">
                    <hr />
                    <?php fire_plugin_hook('public_items_show', array('view' => $this, 'item' => $item)); ?>
                </div>
            </div>
        </div>
        <!-- The following returns all of the files associated with an item. -->
        <div id="itemfiles" class="col-md-3">

	<!-- FIX KA 20180429: external image 
	-->
	<?php if (metadata('item', array('Item Type Metadata', 'Jpg link'))): ?>
	<div class="externalImage">
		<a target="_blank" href="<?php echo metadata('item', array('Item Type Metadata', 'Fotó link')) ?>">
		<img class="img-rounded img-responsive" src="<?php echo metadata('item', array('Item Type Metadata', 'Jpg link')) ?>" alt="image" />
		</a>
	</div>
	<?php endif; ?>


            <?php if (metadata($item, 'has files')): ?>
            <h3><?php echo metadata($item, 'file_count') == 1 ? __('File') : __('Files'); ?></h3>
            <div class="element-text"><?php echo custom_files_for_item(
                // This might be easier for future customization: https://omeka.org/codex/Display_Specific_Metadata_for_an_Item_File
                //options
                array(
                    'imageSize' => 'fullsize',
                    'linkToFile' => true,
                    'linkToMetadata'=>false,
                    'imgAttributes' => array('class' => 'img-responsive'),
                ),
                // wrapper
                array('class' => 'file-image'),
                null);
            ?></div>
            <?php endif; ?>
        </div>
    </div>
<?php endif; ?>
    <br />
    <div class="row form-group">
        <div class="col-xs-12">
            <nav class="pager">
                <ul>
                    <li id="previous-item" class="previous"><?php echo link_to_previous_item_show(); ?></li>
                </ul>
                <ul>
                    <li id="next-item" class="next"><?php echo link_to_next_item_show(); ?></li>
                </ul>
            </nav>
        </div>
    </div>
</div><?php //end primary ?>
<?php echo foot();
