(function ($) {
  var chipsHandleEvents = false;
  var materialChipsDefaults = {
    data: [],
    placeholder: '',
    secondaryPlaceholder: '',
    inputHiddenId: 'chipsInputHidden',
    seperator: ',',
    onStartLoadFunc:function(){},
    onStopLoadFunc:function(){}
  };

  $(document).ready(function() {
    // Handle removal of static chips.
    $(document).on('click', '.chip .close', function(e){
      var $chips = $(this).closest('.chips');
      if ($chips.attr('data-initialized')) {
        return;
      }
      $(this).closest('.chip').remove();
    });
  });

  $.fn.material_chip = function (options) {
    var self = this;
    this.$el = $(this);
    this.$document = $(document);
    this.SELS = {
      CHIPS: '.chips',
      CHIP: '.chip',
      INPUT: 'input',
      DELETE: '.material-icons',
      REMOVE_ALL :'.chips-removeAll',
      SELECTED_CHIP: '.selected',
    };

    if ('data' === options) {
      return this.$el.data('chips');
    }

    var curr_options = $.extend({}, materialChipsDefaults, options);


    // Initialize
    this.init = function() {
      var i = 0;
      var chips;
      self.$el.each(function(){
        var $chips = $(this);
        var chipId = Materialize.guid();

        if (!curr_options.data || !(curr_options.data instanceof Array)) {
          curr_options.data = [];
        }
        $chips.data('chips', curr_options.data);
        $chips.attr('data-index', i);
        $chips.attr('data-initialized', true);

        if (!$chips.hasClass(self.SELS.CHIPS)) {
          $chips.addClass('chips');
        }

        self.chips($chips, chipId);
        // add to inputHidden
        self.setInputHidden();
        i++;
      });
    };

    this.handleEvents = function(){
      var SELS = self.SELS;

      self.$document.off('click.chips-focus', SELS.CHIPS).on('click.chips-focus', SELS.CHIPS, function(e){
        $(e.target).find(SELS.INPUT).focus();
      });

      self.$document.off('click.chips-select', SELS.CHIP).on('click.chips-select', SELS.CHIP, function(e){
        $(SELS.CHIP).removeClass('selected');
        $(this).toggleClass('selected');
      });

      self.$document.off('keydown.chips').on('keydown.chips', function(e){
        if ($(e.target).is('input, textarea')) {
          return;
        }

        // delete
        var $chip = self.$document.find(SELS.CHIP + SELS.SELECTED_CHIP);
        var $chips = $chip.closest(SELS.CHIPS);
        var length = $chip.siblings(SELS.CHIP).length;
        var index;

        if (!$chip.length) {
          return;
        }

        if (e.which === 8 || e.which === 46) {
          e.preventDefault();

          index = $chip.index();
          self.deleteChip(index, $chips);

          var selectIndex = null;
          if ((index + 1) < length) {
            selectIndex = index;
          } else if (index === length || (index + 1) === length) {
            selectIndex = length - 1;
          }

          if (selectIndex < 0) selectIndex = null;

          if (null !== selectIndex) {
            self.selectChip(selectIndex, $chips);
          }
          if (!length) $chips.find('input').focus();

        // left
        } else if (e.which === 37) {
          index = $chip.index() - 1;
          if (index < 0) {
            return;
          }
          $(SELS.CHIP).removeClass('selected');
          self.selectChip(index, $chips);

        // right
        } else if (e.which === 39) {
          index = $chip.index() + 1;
          $(SELS.CHIP).removeClass('selected');
          if (index > length) {
            $chips.find('input').focus();
            return;
          }
          self.selectChip(index, $chips);
        }
      });
      self.$document.off('focusin.chips', SELS.CHIPS + ' ' + SELS.INPUT).on('focusin.chips', SELS.CHIPS + ' ' + SELS.INPUT, function(e){
        var $currChips = $(e.target).closest(SELS.CHIPS);
        $currChips.addClass('focus');
        $currChips.siblings('label, .prefix').addClass('active');
        $(SELS.CHIP).removeClass('selected');
      });

      self.$document.off('focusout.chips', SELS.CHIPS + ' ' + SELS.INPUT).on('focusout.chips', SELS.CHIPS + ' ' + SELS.INPUT, function(e){
        var $currChips = $(e.target).closest(SELS.CHIPS);
        $currChips.removeClass('focus');

        // Remove active if empty
        if (!$currChips.data('chips').length) {
          $currChips.siblings('label').removeClass('active');
        }
        $currChips.siblings('.prefix').removeClass('active');        
      });
      
      // customize by shk add double click event to copy  
      self.$document.off('dblclick.chips-add', SELS.CHIPS + ' ' + SELS.INPUT).on('dblclick.chips-add', SELS.CHIPS + ' ' + SELS.INPUT, function(e){
    	  	var $target = $(e.target);
    	  	var $chips = $target.closest(SELS.CHIPS);
    	  	$target.val(self.getInputHidden());
    	  	self.deleteAllChips($chips);
        });
      // customize by shk remove all by click on remove all button
      self.$document.off('click.chips-removeAll',SELS.REMOVE_ALL).on('click.chips-removeAll', SELS.REMOVE_ALL, function(e){
    	  var $target = $(e.target);
    	  var $chips = $target.siblings(SELS.CHIPS);
    	 // console.log($target.siblings(SELS.CHIPS));
    	  self.deleteAllChips($chips);
    	  self.getInputHidden();
        });
      // customization by shk add tags on change text box
      self.$document.off('blur.chips-add', SELS.CHIPS + ' ' + SELS.INPUT).on('blur.chips-add', SELS.CHIPS + ' ' + SELS.INPUT, function(e){
          var $target = $(e.target);
          var $chips = $target.closest(SELS.CHIPS);
          self.splitText($target.val(),$chips);
          //console.log($target.closest(SELS.CHIPS));
          $target.val('');

      });
      self.$document.off('keydown.chips-add', SELS.CHIPS + ' ' + SELS.INPUT).on('keydown.chips-add', SELS.CHIPS + ' ' + SELS.INPUT, function(e){
        var $target = $(e.target);
        var $chips = $target.closest(SELS.CHIPS);
        var chipsLength = $chips.children(SELS.CHIP).length;
        
        // enter and space
        if (13 === e.which || 32 === e.which) {
          e.preventDefault();
          self.splitText($target.val(),$chips);
          $target.val('');
          return;
        }
        
        
        // delete or left
         if ((8 === e.keyCode || 37 === e.keyCode) && '' === $target.val() && chipsLength) {
          e.preventDefault();
          self.selectChip(chipsLength - 1, $chips);
          $target.blur();
          return;
        }
         
      });
      
      // Click on delete icon in chip.
      self.$document.off('click.chips-delete', SELS.CHIPS + ' ' + SELS.DELETE).on('click.chips-delete', SELS.CHIPS + ' ' + SELS.DELETE, function(e) {
        var $target = $(e.target);
        var $chips = $target.closest(SELS.CHIPS);
        var $chip = $target.closest(SELS.CHIP);
        e.stopPropagation();
        self.deleteChip($chip.index(), $chips);
        $chips.find('input').focus();
      });
    };
    //customize by shk split text function
    this.splitText = function(str,chips){
        // customization by shk add tags just with space
        var splitedText = [];
        //console.log("splitText->str" + str);
        splitedText = str.split(curr_options.seperator);
        //customize shk for performance considerations 
        if(splitedText.length<500)
        {
        	curr_options.onStartLoadFunc();
        	for(var i = 0 ; i < splitedText.length ; i++)
        	{
        		self.addChip({tag: splitedText[i]}, chips);
        		//console.log("split text in loop: "+ splitedText[i]);
        	}
        	curr_options.onStopLoadFunc();
        }
        else 
        {
        	curr_options.onStartLoadFunc();
        	function test(){
        		if(i>splitedText.length/150)
        		{
        			curr_options.onStopLoadFunc();
        			clearInterval(interval);
        		}
        		for(var j = i*150 ; j <i*150+150  ; j++)
        		{
            		self.addChip({tag: splitedText[j]}, chips);
        		}
        		i++;
        	}
			var i=0;
			var interval = setInterval(test,0.000001);			
			//console.log("split text in loop: "+ splitedText[i]);
				
        }
    }
    //set input hidden function
    this.setInputHidden = function(){
    	var SELS = self.SELS;
    	var $chipsAddedText = "";
		jQuery(SELS.CHIPS+" "+SELS.CHIP).each(function(){
			$chipsAddedText = $chipsAddedText ==""?jQuery(this).text():$chipsAddedText + curr_options.seperator + jQuery(this).text();
		});
    	$("#"+curr_options.inputHiddenId).val($chipsAddedText);
    }
    this.getInputHidden = function(){
    	var targetVal = $("#"+curr_options.inputHiddenId).val();
    	$("#"+curr_options.inputHiddenId).val("");
    	return targetVal;
    }
    this.chips = function($chips, chipId) {
      var html = '';
      $chips.data('chips').forEach(function(elem){
        html += self.renderChip(elem);
      });
      html += '<input id="' + chipId +'" class="input" placeholder="">';
      $chips.html(html);
      self.setPlaceholder($chips);

      // Set for attribute for label
      var label = $chips.next('label');
      if (label.length) {
        label.attr('for', chipId);

        if ($chips.data('chips').length) {
          label.addClass('active');
        }
      }
    };

    this.renderChip = function(elem) {
      if (!elem.tag) return;

      var html = '<div class="chip">' + elem.tag;
      if (elem.image) {
        html += ' <img src="' + elem.image + '"> ';
      }
      html += '<i class="icon-cancel material-icons close"></i>';
      html += '</div>';
      return html;
    };

    this.setPlaceholder = function($chips) {
      if ($chips.data('chips').length && curr_options.placeholder) {
        $chips.find('input').prop('placeholder', curr_options.placeholder);

      } else if (!$chips.data('chips').length && curr_options.secondaryPlaceholder) {
        $chips.find('input').prop('placeholder', curr_options.secondaryPlaceholder);
      }
    };

    this.isValid = function($chips, elem) {
      var chips = $chips.data('chips');
      var exists = false;
      for (var i=0; i < chips.length; i++) {
        if (chips[i].tag === elem.tag) {
            exists = true;
            return;
        }
      }
      return '' !== elem.tag && !exists;
    };

    this.addChip = function(elem, $chips) {
      if (!self.isValid($chips, elem)) {
        //console.log("addChip=");
    	//console.log(elem);
    	//console.log("");
        return;
      }
      var chipHtml = self.renderChip(elem);
      var newData = [];
      var oldData = $chips.data('chips');
      for (var i = 0; i < oldData.length; i++) {
    	 // console.log("oldData[i]:"+ oldData[i]);
    	  newData.push(oldData[i]);
      }
      newData.push(elem);

      $chips.data('chips', newData);
      $(chipHtml).insertBefore($chips.find('input'));
      $chips.trigger('chip.add', elem);
      self.setPlaceholder($chips);
      //set input hidden
      self.setInputHidden();
    };

    this.deleteChip = function(chipIndex, $chips) {
      var chip = $chips.data('chips')[chipIndex];
      $chips.find('.chip').eq(chipIndex).remove();

      var newData = [];
      var oldData = $chips.data('chips');
      for (var i = 0; i < oldData.length; i++) {
        if (i !== chipIndex) {
          newData.push(oldData[i]);
        }
      }

      $chips.data('chips', newData);
      $chips.trigger('chip.delete', chip);
      self.setPlaceholder($chips);
      //set input hidden
      self.setInputHidden();
    };

    //customize by shk delete all chips 
    this.deleteAllChips = function($chips) {
    	$chips.find('.chip').remove();
    	$chips.data('chips','');
    };
    this.selectChip = function(chipIndex, $chips) {
      var $chip = $chips.find('.chip').eq(chipIndex);
      if ($chip && false === $chip.hasClass('selected')) {
        $chip.addClass('selected');
        $chips.trigger('chip.select', $chips.data('chips')[chipIndex]);
      }
    };

    this.getChipsElement = function(index, $chips) {
      return $chips.eq(index);
    };

    // init
    this.init();

    if (!chipsHandleEvents) {
      this.handleEvents();
      chipsHandleEvents = true;
    }
  };
}( jQuery ));