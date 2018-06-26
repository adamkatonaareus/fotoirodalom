<div class="item record">
    <?php
    $title = version_compare(OMEKA_VERSION, '2.5', '>=') ? metadata($item, 'display_title') : metadata($item, array('Dublin Core', 'Title'));
    $description = metadata($item, array('Dublin Core', 'Description'), array('no_escape' => true, 'snippet' => 150));
    ?>
    <h3 class="ellipsis"><?php echo link_to($item, 'show', $title); ?></h3>
    <div class="image-featured">
    <?php 

	//FIX KA 20180601: using Moly cover
	$image = moly_cover($item, false);

	//if (metadata($item, 'has files')):
        echo link_to_item(
	    $image,
            //item_image('square_thumbnail', array('class' => 'center-block img-responsive'), 0, $item),
            array('class' => 'image'), 'show', $item
        );
    //else:
    //    $noFile = '<img src="' . img('no-file.png') . '" class="img-rounded img-responsive img-thumbnail" alt="' . __('No file') . '" />';
    //    echo link_to_item($noFile, array('class' => 'image none'), 'show', $item);
    //endif; 

    ?>
    </div>
    <div class="item-description">
    <?php if ($description): ?>
        <div class="ellipsis"><?php echo $description; ?></div>
    <?php endif; ?>
    </div>
</div>
