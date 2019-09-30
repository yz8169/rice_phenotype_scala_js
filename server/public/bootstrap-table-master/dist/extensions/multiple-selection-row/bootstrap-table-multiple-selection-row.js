/**
 *
 * for BootstrapTable v.1.12.1
 *
 * small plugin [multiple-select-rows = 'true'] to select/deselect rows with Ctrl & Shift keys
 *
 */
(function($) {

    'use strict';

    var BootstrapTable  = $.fn.bootstrapTable.Constructor,
        _initBody = BootstrapTable.prototype.initBody;

    BootstrapTable.prototype.initBody = function () {

        _initBody.apply(this, Array.prototype.slice.apply(arguments));

        if(this.options.multipleSelectRows) {

            var that = this;

            this.$body.find('> tr[data-index] > td').off('mousedown').on('mousedown', function (e) {
                that.options.ctrlKey  = e.ctrlKey;
                that.options.shiftKey = e.shiftKey;
            });

            this.$selectItem.off('click').on('click', function (event) {
                event.stopImmediatePropagation();

                var $this = $(this),
                    checked = $this.prop('checked'),
                    index = $this.data('index'),
                    row = that.data[index];

                if( $(this).is(':radio') ) {
                    $.each(that.options.data, function (i, row) {
                        row[that.header.stateField] = false;
                    });
                }

                row[that.header.stateField] = checked;

                if( that.options.shiftKey && that.options.lastSelectedRowIndex >= 0 ) {

                    var indexes = [that.options.lastSelectedRowIndex, index].sort();

                    for(var i = indexes[0] + 1; i < indexes[1]; i++) {
                        that.data[i][that.header.stateField] = true;
                        that.$selectItem.filter('[data-index="'+i+'"]').prop('checked', true);
                    }
                }
                else
                if( that.options.ctrlKey ) {
                    // do nothing
                }
                else {
                    that.$selectItem.not(this).each(function () {
                        that.data[$(this).data('index')][that.header.stateField] = false;
                    });
                    that.$selectItem.filter(':checked').not(this).prop('checked', false);
                }

                that.updateSelected();
                that.trigger(checked ? 'check' : 'uncheck', row, $this);
                that.options.ctrlKey = false;
                that.options.shiftKey = false;
                that.options.lastSelectedRowIndex = checked ? index : null;
            });
        }
    };
})(jQuery)